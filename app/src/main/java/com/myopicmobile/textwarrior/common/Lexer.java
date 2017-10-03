/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.*;
import java.util.Vector;

import android.util.*;

/**
 * Does lexical analysis of a text for C-like languages.
 * The programming language syntax used is set as a static class variable.
 */
public class Lexer
{
	private final static int MAX_KEYWORD_LENGTH = 127;

	public final static int UNKNOWN = -1;
	public final static int NORMAL = 0;
	public final static int KEYWORD = 1;
	public final static int OPERATOR = 2;
	public final static int NAME = 3;
	public final static int LITERAL = 4;
	/** A word that starts with a special symbol, inclusive.
	 * Examples:
	 * :ruby_symbol
	 * */
	public final static int SINGLE_SYMBOL_WORD = 10;

	/** Tokens that extend from a single start symbol, inclusive, until the end of line.
	 * Up to 2 types of symbols are supported per language, denoted by A and B
	 * Examples:
	 * #include "myCppFile"
	 * #this is a comment in Python
	 * %this is a comment in Prolog
	 * */
	public final static int SINGLE_SYMBOL_LINE_A = 20;
	public final static int SINGLE_SYMBOL_LINE_B = 21;

	/** Tokens that extend from a two start symbols, inclusive, until the end of line.
	 * Examples:
	 * //this is a comment in C
	 * */
	public final static int DOUBLE_SYMBOL_LINE = 30;

	/** Tokens that are enclosed between a start and end sequence, inclusive,
	 * that can span multiple lines. The start and end sequences contain exactly
	 * 2 symbols.
	 * Examples:
	 * {- this is a...
	 *  ...multi-line comment in Haskell -}
	 * */
	public final static int DOUBLE_SYMBOL_DELIMITED_MULTILINE = 40;

	/** Tokens that are enclosed by the same single symbol, inclusive, and
	 * do not span over more than one line.
	 * Examples: 'c', "hello world"
	 * */
	public final static int SINGLE_SYMBOL_DELIMITED_A = 50;
	public final static int SINGLE_SYMBOL_DELIMITED_B = 51;

	private static Language _globalLanguage = LanguageNonProg.getInstance();
	synchronized public static void setLanguage(Language lang)
	{
		_globalLanguage = lang;
	}

	synchronized public static Language getLanguage()
	{
		return _globalLanguage;
	}


	private DocumentProvider _hDoc;
	private LexThread _workerThread = null;
	LexCallback _callback = null;

	public Lexer(LexCallback callback)
	{
		_callback = callback;
	}

	public void tokenize(DocumentProvider hDoc)
	{
		if (!Lexer.getLanguage().isProgLang())
		{
			return;
		}

		//tokenize will modify the state of hDoc; make a copy
		setDocument(new DocumentProvider(hDoc));
		if (_workerThread == null)
		{
			_workerThread = new LexThread(this);
			_workerThread.start();
		}
		else
		{
			_workerThread.restart();
		}
	}

	void tokenizeDone(List<Pair> result)
	{
		if (_callback != null)
		{
			_callback.lexDone(result);
		}
		_workerThread = null;
	}

	public void cancelTokenize()
	{
		if (_workerThread != null)
		{
			_workerThread.abort();
			_workerThread = null;
		}
	}

	public synchronized void setDocument(DocumentProvider hDoc)
	{
		_hDoc = hDoc;
	}

	public synchronized DocumentProvider getDocument()
	{
		return _hDoc;
	}





	private class LexThread extends Thread
	{
		private boolean rescan = false;
		private final Lexer _lexManager;
		/** can be set by another thread to stop the scan immediately */
		private final Flag _abort;
		/** A collection of Pairs, where Pair.first is the start
		 *  position of the token, and Pair.second is the type of the token.*/
		/**
		 * pair的集合，first表示token的开始，second表示token的类型
		 */
		private LinkedList<Pair> _tokens;

		public LexThread(Lexer p)
		{
			_lexManager = p;
			_abort = new Flag();
		}

		@Override
		public void run()
		{
			do{
				rescan = false;
				_abort.clear();
				tokenize();
			}
			while(rescan);

			if (!_abort.isSet())
			{
				// lex complete
				_lexManager.tokenizeDone(_tokens);
			}
		}

		public void restart()
		{
			rescan = true;
			_abort.set();
		}

		public void abort()
		{
			_abort.set();
		}

		/**
		 * Scans the document referenced by _lexManager for tokens.
		 * The result is stored internally.
		 *扫描结果存在list
		 * *******************************
		 * #include <stdio.h>
		 * int main(void)
		 * {
		 *
		 *     return 0;
		 * }
		 * *******************************
		 * 以上的C代码将产生以下词法分析器
		 * (0,20)---->#include <stdio.h>
		 * (20,1)---->int
		 * (24,0)空格
		 * (29,1)----->void
		 * (33,0)
		 * (42,1)----->return
		 * (49,0)
		 * ------------------------------
		 * 它的特点：first记录从哪开始有标记，second记录标记的类型
		 */
		public void tokenize(){
			DocumentProvider hDoc = getDocument();
			Language language = Lexer.getLanguage();
			//
			LinkedList<Pair> tokens = new LinkedList<>();

			//language.isProgLang()返回真
			if(!language.isProgLang()){
				tokens.add(new Pair(0, NORMAL));
				_tokens = tokens;
				return;
			}
			char[] candidateWord = new char[MAX_KEYWORD_LENGTH];
			//当前字符在词中的位置
			int currentCharInWord = 0;
			//
			int spanStartPosition = 0;
			int workingPosition = 0;
			int state = UNKNOWN;
			char prevChar = 0;

			hDoc.seekChar(0);
			while (hDoc.hasNext() && !_abort.isSet()){
				char currentChar = hDoc.next();

				switch(state){
					case UNKNOWN: //fall-through
					case NORMAL: //fall-through
					case KEYWORD: //fall-through
					case SINGLE_SYMBOL_WORD:
						int pendingState = state;
						boolean stateChanged = false;
						//单行注释开始
						if (language.isLineStart(prevChar, currentChar)){
							pendingState = DOUBLE_SYMBOL_LINE;
							stateChanged = true;
						}
						//多行注释开始
						else if (language.isMultilineStartDelimiter(prevChar, currentChar)){
							pendingState = DOUBLE_SYMBOL_DELIMITED_MULTILINE;
							stateChanged = true;
						}
						//字符串的开始
						else if (language.isDelimiterA(currentChar)){
							pendingState = SINGLE_SYMBOL_DELIMITED_A;
							stateChanged = true;
						}
						//字符的开始
						else if (language.isDelimiterB(currentChar)){
							pendingState = SINGLE_SYMBOL_DELIMITED_B;
							stateChanged = true;
						}
						//宏定义开始
						else if (language.isLineAStart(currentChar)){
							pendingState = SINGLE_SYMBOL_LINE_A;
							stateChanged = true;
						}
						//不会发生，除非被覆盖
						else if (language.isLineBStart(currentChar)){
							pendingState = SINGLE_SYMBOL_LINE_B;
							stateChanged = true;
						}

						//状态是否改变
						if(stateChanged){
							if (pendingState == DOUBLE_SYMBOL_LINE ||
									pendingState == DOUBLE_SYMBOL_DELIMITED_MULTILINE){
								// account for previous char
								spanStartPosition = workingPosition - 1;
								//TODO consider less greedy approach and avoid adding token for previous char
								if(tokens.getLast().getFirst() == spanStartPosition){
									tokens.removeLast();
								}
							}
							else{
								spanStartPosition = workingPosition;
							}

							// If a span appears mid-word, mark the chars preceding
							// it as NORMAL, if the previous span isn't already NORMAL
							if(currentCharInWord > 0 && state != NORMAL){
								tokens.add(new Pair(workingPosition-(workingPosition - currentCharInWord), NORMAL));
							}

							state = pendingState;
							tokens.add(new Pair(spanStartPosition, state));
							currentCharInWord = 0;
						}
						//判断是否空白行或者运算符
						else if (language.isWhitespace(currentChar) || language.isOperator(currentChar)){
							if (currentCharInWord > 0){
								// full word obtained; mark the beginning of the word accordingly
								//不会发生
								if( language.isWordStart(candidateWord[0]) ){
									spanStartPosition = workingPosition - currentCharInWord;
									state = SINGLE_SYMBOL_WORD;
									tokens.add(new Pair(spanStartPosition, state));
								}
								//关键字
								else if(language.isKeyword( new String(candidateWord, 0, currentCharInWord)) ){
									spanStartPosition = workingPosition - currentCharInWord;
									state = KEYWORD;

									tokens.add(new Pair(spanStartPosition, state));

								}
								else if (state != NORMAL){
									spanStartPosition = workingPosition - currentCharInWord;
									state = NORMAL;
									tokens.add(new Pair(spanStartPosition, state));
								}
								currentCharInWord = 0;
							}

							// mark operators as normal
							if (state != NORMAL && language.isOperator(currentChar) ){
								state = NORMAL;
								tokens.add(new Pair(workingPosition, state));
							}
						}
						//累计关键字
						else if (currentCharInWord < MAX_KEYWORD_LENGTH){
							// collect non-whitespace chars up to MAX_KEYWORD_LENGTH
							candidateWord[currentCharInWord] = currentChar;
							currentCharInWord++;
						}
						break;


					case DOUBLE_SYMBOL_LINE: // fall-through
					case SINGLE_SYMBOL_LINE_A: // fall-through
					case SINGLE_SYMBOL_LINE_B:
						if (currentChar == '\n'){
							state = UNKNOWN;
						}
						break;


					case SINGLE_SYMBOL_DELIMITED_A:
						if ((language.isDelimiterA(currentChar) && !language.isEscapeChar(prevChar)) ||
								currentChar == '\n'){
							state = UNKNOWN;
						}
						// consume escape of the escape character by assigning
						// currentChar as something else so that it would not be
						// treated as an escape char in the next iteration
						else if (language.isEscapeChar(currentChar) && language.isEscapeChar(prevChar)){
							currentChar = ' ';
						}
						break;


					case SINGLE_SYMBOL_DELIMITED_B:
						if ((language.isDelimiterB(currentChar) && !language.isEscapeChar(prevChar)) ||
								currentChar == '\n'){
							state = UNKNOWN;
						}
						// consume escape of the escape character by assigning
						// currentChar as something else so that it would not be
						// treated as an escape char in the next iteration
						else if (language.isEscapeChar(currentChar)
								&& language.isEscapeChar(prevChar)){
							currentChar = ' ';
						}
						break;

					case DOUBLE_SYMBOL_DELIMITED_MULTILINE:
						if (language.isMultilineEndDelimiter(prevChar, currentChar)){
							state = UNKNOWN;
						}
						break;

					default:
						TextWarriorException.assertVerbose(false, "Invalid state in TokenScanner");
						break;
				}//switch
				++workingPosition;
				prevChar = currentChar;
			}//while
			// end state machine   结束状态机


			if (tokens.isEmpty()){
				// return value cannot be empty
				tokens.add(new Pair(0, NORMAL));
			}

			_tokens = tokens;
			//printList(_tokens);
		}

	}//end inner class

	private  void log(String log)
	{
		System.out.println("------------------>Lexer:"+log);
	}

	private  void printList(List<Pair> list)
	{
		System.out.println("------------------>:Lexer start,Lexer len:"+list.size());
		for (int i=0;i<list.size();i++) {
			Pair pair=list.get(i);
			System.out.println("---------------->"+pair.toString());//不打印？
		}
		System.out.println("------------------>:Lexer end");

	}

	public interface LexCallback
	{
		public void lexDone(List<Pair> results);
	}
}
