package com.luoye.simpleC.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by zyw on 2017/11/17.
 */
public class CFileNameFilter implements FilenameFilter {
    @Override
    public boolean accept(File file, String fileName) {
        return fileName.endsWith(".c")||fileName.endsWith(".h")||fileName.endsWith(".cpp");
    }
}
