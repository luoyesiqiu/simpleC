package com.myopicmobile.textwarrior.common;
public enum CType{
	EOF,
 IDENTIFIER,//标识符
 INTEGER_LITERAL, //整数
 KEYWORD, //关键字
 FLOATING_POINT_LITERAL, //浮点
 COMMENT, //注释
 STRING_LITERAL,//字符串
 COMMA, //逗号
 SEMICOLON,//分号
 RBRACK, //右中括号
 LBRACK,//左中括号
 LPAREN,//左括号
 RPAREN,//右括号
 RBRACE, //右大括号
 LBRACE, //左大括号
 DOT, //点
 OPERATOR, //其他运算符
 CHARACTER_LITERAL,//字符
 STRING, //字符串
 WHITE_SPACE,//空白符
 DEFINE_LINE,//宏定义
 NEW_LINE, //换行符
 WHITE_CHAR,
 PRETREATMENT_LINE//预处理
}