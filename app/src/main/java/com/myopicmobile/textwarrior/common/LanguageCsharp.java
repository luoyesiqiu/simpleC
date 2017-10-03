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
 * Singleton class containing the symbols and operators of the C# language
 */
public class LanguageCsharp extends LanguageCFamily {
	private static LanguageCFamily _theOne = null;
	
	private final static String[] keywords = {
		"abstract",	"as", "base", "bool", "break", "byte", "case", "catch",
		"char", "checked", "class", "const", "continue", "decimal", "default",
		"delegate", "do", "double", "else", "enum", "event", "explicit",
		"extern", "false", "finally", "fixed", "float", "for", "foreach",
		"goto", "if", "implicit", "in", "int", "interface", "internal", "is",
		"lock", "long", "namespace", "new", "null", "object", "operator",
		"out", "override", "params", "private", "protected", "public",
		"readonly", "ref", "return", "sbyte", "sealed", "short", "sizeof",
		"stackalloc", "static", "string", "struct", "switch", "this", "throw",
		"true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe",
		"ushort", "using", "virtual", "void", "volatile", "while",
		"dynamic", "get", "set", "add", "remove", "global", "value", "var",
		"yield", "alias", "partial",
		"from", "where", "join", "on", "equals", "into", "let", "orderby",
		"ascending", "descending", "select", "group", "by"
	};

	public static LanguageCFamily getCharacterEncodings(){
		if(_theOne == null){
			_theOne = new LanguageCsharp();
		}
		return _theOne;
	}
	
	private LanguageCsharp(){
		super.registerKeywords(keywords);
	}

}
