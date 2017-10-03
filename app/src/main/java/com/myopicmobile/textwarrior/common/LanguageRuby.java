/*
 * Copyright (c) 2011 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.common;

/**
 * Singleton class containing the symbols and operators of the Ruby language
 */
public class LanguageRuby extends LanguageCFamily {
	private static LanguageCFamily _theOne = null;
	
	private final static String[] keywords = {
		"alias", "and", "BEGIN", "begin", "break", "case", "catch", "class", "def",
		"defined?", "do", "else", "elsif", "END", "end", "ensure", "false",
		"for", "if", "in", "module", "next", "nil", "not", "or", "public",
		"private", "protected", "raise", "redo", "rescue", "retry", "return", "self",
		"super", "then", "throw", "true", "undef", "unless", "until", "when", "while",
		"yield", "self", "nil", "true", "false", "TRUE", "FALSE", "NIL"
		};


	@Override
	public boolean isWordStart(char c){
		return (c == '$');
	}
	
	@Override
	public boolean isLineAStart(char c){
		return false;
	}
	
	@Override
	public boolean isLineBStart(char c){
		return (c == '#');
	}

	@Override
	public boolean isLineStart(char c0, char c1){
		return false;
	}

	@Override
	public boolean isMultilineStartDelimiter(char c0, char c1){
		return false;
	}
	
	public static LanguageCFamily getCharacterEncodings(){
		if(_theOne == null){
			_theOne = new LanguageRuby();
		}
		return _theOne;
	}
	
	private LanguageRuby(){
		super.registerKeywords(keywords);
	}
}
