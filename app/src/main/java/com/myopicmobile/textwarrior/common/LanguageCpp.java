/*
 * Copyright (c) 2011 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.common;

public class LanguageCpp extends Language{
	private static Language _theOne = null;
	
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
		"export", "namespace", "using", "asm", "inline","restrict"
		};
	private  final  static  String[] cFunctions={
		"abort","abs","acos","asctime","asin","assert","atan","atan2","atexit","atof","atoi","atol"
		,"bsearch","calloc","ceil","clearerr","clock","cos","cosh","ctime","difftime","div"
		,"exit","exp","fabs","fclose","feof","ferror","fflush","fgetc","fgetpos","fgets","floor"
		,"fmod","fopen","fprintf","fputc","fputs","fread","free","freopen","frexp","fscanf","fseek","fsetpos","ftell","fwrite"
		,"getc","getchar","getenv","gets","gmtime","isalnum","isalpha","iscntrl","isdigit","isgraph","islower","isprint","ispunct","isspace","isupper","isxdigit","labs","ldexp","ldiv","localtime","log","log10","longjmp"
		,"malloc","memchr","memcmp","memcpy","memmove","memset","mktime","modf","perror","pow","printf"
		,"putc","putchar","puts","qsort","raise","rand","realloc","remove","rename","rewind"
		,"scanf","setbuf","setjmp","setvbuf","signal","sin","sinh","sprintf","sqrt","srand","sscanf","strcat","strchr","strcmp","strcoll","strcpy","strcspn","strerror","strftime","strlen","strncat","strncmp","strncpy","strpbrk","strrchr","strspn","strstr","strtod","strtok","strtol","strtoul","strxfrm","system"
		,"tan","tanh","time","tmpfile","tmpnam","tolower","toupper","ungetc","va_arg","vprintf","vfprintf"
		,"__LINE__","__FILE__","__DATE__","__TIME__","__cplusplus","__STDC__","__func__","__VA_ARGS__","__attribute__"

	};
	private  final  static  String[] cppWords={
			"std"
			//io
			,"fstream","ifstream","ofstream","cout","cin","cerr","endl"
			//模板库
			,"bitset","string","list","deque","map","multimap","multiset","set","priority_queue","queue","stack","vector"
			//c++11
			,"array","forward_list","unordered_map","unordered_set"
			,"any","append","assign","at","back","bad","begin","c_str","capacity","clear","compare","copy","count","data","empty","end","eof","equal_range","erase","fail","fill","find","find_first_not_of","find_first_of","find_last_not_of","find_last_of","flags","flip","flush","front","fstream"
			,"gcount","get","get_allocator","getline","good","ignore","insert","iterator","key_comp","length","lower_bound","max_size","merge","none","open","peek","pop","pop_back","pop_front","precision","push","push_back","push_front","put","putback"
			,"rbegin","rdstate","read","remove","remove_if","rend","replace","reserve","reset","resize","reverse","rfind","seekg","seekp","set","setf","size","sort","splice","substr","swap","sync_with_stdio","tellg","tellp","test","to_string","to_ulong","top"
			,"unique","unsetf","upper_bound","value_comp","width","write"
	};

	private  final  static  String[] extraWord={
			"define","include","ifdef","endif","ifndef","error","elif","line","pragma","undef","main"

	};
	private final static char[] BASIC_C_OPERATORS = {
			'(', ')', '{', '}', '.', ',', ';', '=', '+', '-',
			'/', '*', '&', '!', '|', ':', '[', ']', '<', '>',
			'?', '~', '%', '^'
	};
	public static Language getInstance(){
		if(_theOne == null){
			_theOne = new LanguageCpp();
		}
		return _theOne;
	}
	public void addNames(String[] names) {
		String[] old=this.getNames();
		String[] news=new String[old.length + names.length];
		System.arraycopy(old, 0, news, 0, old.length);
		System.arraycopy(names, 0, news, old.length, names.length);
		this.setNames(news);

	}
	private LanguageCpp(){
		setOperators(BASIC_C_OPERATORS);
		setKeywords(keywords);
		setNames(cFunctions);
		addNames(cppWords);
		addNames(extraWord);
	}
}
