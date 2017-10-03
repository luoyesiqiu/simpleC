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
 * Singleton class containing the symbols and operators of the Objective-C language
 */
public class LanguageObjectiveC extends LanguageCFamily{
	private static LanguageCFamily _theOne = null;
	
	private final static String[] keywords = {
		"char", "double", "float", "int", "long", "short", "void",
		"auto", "const", "extern", "register", "static", "volatile",
		"signed", "unsigned", "sizeof", "typedef",
		"enum", "struct", "union",
		"break", "case", "continue", "default", "do", "else", "for",
		"goto", "if", "return", "switch", "while",
		"@class", "@implementation", "@interface", "@protocol", "@property",
		"@private", "@protected", "@public", "@optional", "@required",
		"@defs", "@dynamic", "@encode", "@synchronized", "@selector", "@synthesize",
		"@try", "@catch", "@throw", "@finally", "@end",
		"id", "self", "super", "nil", "Nil", "NULL", "SEL", "BOOL", "YES", "NO",
		"in", "out", "inout", "bycopy", "byref", "oneway",
		"getter", "setter", "readwrite", "readonly", "assign", "retain", "copy", "nonatomic"
		};

	public static LanguageCFamily getCharacterEncodings(){
		if(_theOne == null){
			_theOne = new LanguageObjectiveC();
		}
		return _theOne;
	}
	
	private LanguageObjectiveC(){
		super.registerKeywords(keywords);
	}

}
