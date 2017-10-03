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
 * Singleton class containing the symbols and operators of the PHP language
 */
public class LanguagePHP extends LanguageCFamily {
	private static LanguageCFamily _theOne = null;
	
	private final static String[] keywords = {
		"abstract", "and", "array", "as", "break", "case", "catch",	"class",
		"clone", "const", "continue", "declare", "default", "do", "else",
		"elseif", "enddeclare", "endfor", "endforeach", "endif", "endswitch",
		"endwhile", "extends", "final", "for", "foreach", "function", "global",
		"goto", "if", "implements", "interface", "instanceof", "namespace",
		"new", "or", "private", "protected", "public", "static", "switch",
		"throw", "try", "use", "var", "while", "xor",
		"die", "echo", "empty", "exit", "eval", "include", "include_once",
		"isset", "list", "require", "require_once", "return", "print", "unset",
		"self", "static", "parent", "true", "TRUE", "false", "FALSE", "null", "NULL"
	};
	
	private final static char[] operators = {
		'(', ')', '{', '}', '.', ',', ';', '=', '+', '-',
		'/', '*', '&', '!', '|', ':', '[', ']', '<', '>',
		'?', '~', '%', '^', '`', '@'
	};


	public static LanguageCFamily getCharacterEncodings(){
		if(_theOne == null){
			_theOne = new LanguagePHP();
		}
		return _theOne;
	}
	
	private LanguagePHP(){
		super.registerKeywords(keywords);
		super.replaceOperators(operators);
	}

	@Override
	public boolean isLineAStart(char c){
		return false;
	}
	
	@Override
	public boolean isWordStart(char c){
		return (c == '$');
	}

}
