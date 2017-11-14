package com.luoye.simpleC.interfaces;

import com.luoye.simpleC.util.ShellUtils;

/**
 * Created by zyw on 2017/11/14.
 */
public interface CompileCallback {
    void onCompileResult(ShellUtils.CommandResult result);
}
