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
 * Keys and displayed names for various character encoding schemes
 */
public class EncodingScheme {
	public static final String TEXT_ENCODING_AUTO = "Auto";
	public static final String TEXT_ENCODING_LATIN1 = "ISO-8859-1";
	public static final String TEXT_ENCODING_UTF8 = "UTF-8";
	public static final String TEXT_ENCODING_UTF16BE = "UTF-16BE";
	public static final String TEXT_ENCODING_UTF16LE = "UTF-16LE";
	
	private static final String ALIAS_TEXT_ENCODING_AUTO = "Auto";
	private static final String ALIAS_TEXT_ENCODING_LATIN1 = "Latin-1";
	private static final String ALIAS_TEXT_ENCODING_UTF8 = "UTF-8";
	private static final String ALIAS_TEXT_ENCODING_UTF16BE = "UTF-16BE";
	private static final String ALIAS_TEXT_ENCODING_UTF16LE = "UTF-16LE";

	public static final String LINE_BREAK_AUTO = "Auto";
	public static final String LINE_BREAK_LF = "Unix";
	public static final String LINE_BREAK_CR = "Mac OS 9";
	public static final String LINE_BREAK_CRLF = "Windows";
//	public static final String LINE_BREAK_LS = "LS"; // \u2028
//	public static final String LINE_BREAK_NEL = "NEL"; // \u0085
	
	private static final String ALIAS_LINE_BREAK_AUTO = "Auto";
	private static final String ALIAS_LINE_BREAK_LF = "Android / BlackBerry / iOS / Linux / Mac";
	private static final String ALIAS_LINE_BREAK_CR = "Mac OS 9";
	private static final String ALIAS_LINE_BREAK_CRLF = "Windows / Symbian / webOS";
//	private static final String ALIAS_LINE_BREAK_LS = "Unicode LS";
//	private static final String ALIAS_LINE_BREAK_NEL = "Unicode NEL";

	public final static String lineTerminators[] = {
		LINE_BREAK_AUTO, LINE_BREAK_LF,
		LINE_BREAK_CRLF, LINE_BREAK_CR
		};
	
	public final static String lineTerminatorsAliases[] = {
		ALIAS_LINE_BREAK_AUTO, ALIAS_LINE_BREAK_LF,
		ALIAS_LINE_BREAK_CRLF, ALIAS_LINE_BREAK_CR
	};

	public final static String encodingSchemes[] = {
		TEXT_ENCODING_AUTO, TEXT_ENCODING_LATIN1, TEXT_ENCODING_UTF8,
		TEXT_ENCODING_UTF16BE, TEXT_ENCODING_UTF16LE
	};
	
	public final static String encodingSchemesAliases[] = {
		ALIAS_TEXT_ENCODING_AUTO, ALIAS_TEXT_ENCODING_LATIN1, ALIAS_TEXT_ENCODING_UTF8,
		ALIAS_TEXT_ENCODING_UTF16BE, ALIAS_TEXT_ENCODING_UTF16LE
	};
}
