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
 * Singleton class containing the symbols and operators of the C++ language
 */
public class LanguageCpp extends LanguageCFamily{
	private static LanguageCFamily _theOne = null;
	
	private final static String[] keywords = {
		"bool", "char", "double", "float", "int", "long", "short", "void", "wchar_t",
		"auto", "const", "extern", "mutable", "register", "static", "volatile",
		"signed", "unsigned", "true", "false",
		"new", "delete", "sizeof", "typedef", "typeid", "typename",
		"const_cast", "dynamic_cast", "reinterpret_cast", "static_cast",
		"class", "enum", "explicit", "operator", "struct", "template", "union", "virtual",
		"private", "protected", "public", "friend", "this",
		"break", "case", "catch", "continue", "default", "do", "else", "for",
		"goto", "if", "return", "switch", "throw", "try", "while",
		"export", "namespace", "using", "asm", "inline",
		"and", "and_eq", "bitand", "bitor", "compl", "not", "not_eq",
		"or", "or_eq", "xor", "xor_eq"
		};

	public static LanguageCFamily getCharacterEncodings(){
		if(_theOne == null){
			_theOne = new LanguageCpp();
		}
		return _theOne;
	}
	
	private LanguageCpp(){
		super.registerKeywords(keywords);
	}
}
