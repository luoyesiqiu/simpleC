package com.luoye.simpleC.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by zyw on 2017/10/1.
 */
public class ConstantPool {
    public static  final int OK_SELECT_RESULT_CODE=0;
    public  static  final  String FILE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"simpleC";
}
