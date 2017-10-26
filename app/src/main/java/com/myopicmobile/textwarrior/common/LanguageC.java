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
import java.util.Arrays;

/**
 * Singleton class containing the symbols and operators of the C language
 */
public class LanguageC extends Language{
	private static Language _theOne = null;
	
	private final static String[] keywords = {
		"char", "double", "float", "int", "long", "short", "void",
		"auto", "const", "extern", "register", "static", "volatile",
		"signed", "unsigned", "sizeof", "typedef",
		"enum", "struct", "union",
		"break", "case", "continue", "default", "do", "else", "for",
		"goto", "if", "return", "switch", "while",
		"define","include","ifdef","endif","ifndef","error","elif","line","pragma","undef"
		};
	private  final  static  String[] funtions={

			"abort","abs","acos","asctime","asin","assert","atan","atan2","atexit","atof","atoi","atol"
			,"bsearch","calloc","ceil","clearerr","clock","cos","cosh","ctime","difftime","div"
			,"exit","exp","fabs","fclose","feof","ferror","fflush","fgetc","fgetpos","fgets","floor"
			,"fmod","fopen","fprintf","fputc","fputs","fread","free","freopen","frexp","fscanf","fseek","fsetpos","ftell","fwrite"
			,"getc","getchar","getenv","gets","gmtime","isalnum","isalpha","iscntrl","isdigit","isgraph","islower","isprint","ispunct","isspace","isupper","isxdigit","labs","ldexp","ldiv","localtime","log","log10","longjmp"
			,"malloc","memchr","memcmp","memcpy","memmove","memset","mktime","modf","perror","pow","printf"
			,"putc","putchar","puts","qsort","raise","rand","realloc","remove","rename","rewind"
			,"scanf","setbuf","setjmp","setvbuf","signal","sin","sinh","sprintf","sqrt","srand","sscanf","strcat","strchr","strcmp","strcoll","strcpy","strcspn","strerror","strftime","strlen","strncat","strncmp","strncpy","strpbrk","strrchr","strspn","strstr","strtod","strtok","strtol","strtoul","strxfrm","system"
			,"tan","tanh","time","tmpfile","tmpnam","tolower","toupper","ungetc","va_arg","vprintf","vfprintf"
			,"__LINE__","__FILE__","__DATE__","__TIME__","_cplusplus","__STDC__"

	};
	private  final  static  String[] header={
			"math.h","stdio.h","stdlib.h","string.h","time.h","errno.h","ctype.h","local.h"
	};
	private final static char[] BASIC_C_OPERATORS = {
			'(', ')', '{', '}', '.', ',', ';', '=', '+', '-',
			'/', '*', '&', '!', '|', ':', '[', ']', '<', '>',
			'?', '~', '%', '^'
	};
	public static Language getInstance(){
		if(_theOne == null){
			_theOne = new LanguageC();
		}
		return _theOne;
	}
	
	private LanguageC(){
		String[] diyWord= new String[header.length+funtions.length];
		System.arraycopy(funtions,0,diyWord,0,funtions.length);
		System.arraycopy(header,0,diyWord,funtions.length,header.length);
		setKeywords(keywords);
		setNames(diyWord);
		setOperators(BASIC_C_OPERATORS);

	}
}
