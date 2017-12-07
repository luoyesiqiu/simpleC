package com.luoye.simpleC.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by zyw on 2017/10/1.
 */
public class ConstantPool {
    public static final String TEMP_FILE_NAME="temp.c";
    public static final String TEMP_BIN_NAME="temp";
    public static final String INDENT_FILE_NAME="indent.c";
    public  static  final  String INDENT_ARGS="-nbap -bli0 -i2 -l79 -ts2 -ncs -npcs -npsl -fca -lc79 -fc1 -ts1 -ce -br -cdw -brs -brf";
    public static final String PROBLEMS_URL="https://github.com/luoyesiqiu/C--Problems/blob/master/Problems.md";
    public static  final int OK_SELECT_RESULT_CODE=0;
    public  static  final  String FILE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"simpleC";
}
