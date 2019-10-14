package com.luoye.simpleC.resource.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zyw on 2017/12/3.
 */
public class Setting {
    private static SharedPreferences sharedPreferences;
    private static Setting setting=null;
    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean isShowSymbolView() {
        return showSymbolView;
    }

    public void setShowSymbolView(boolean showSymbolView) {
        this.showSymbolView = showSymbolView;
    }

    public boolean isShowLineNumber() {
        return showLineNumber;
    }

    public void setShowLineNumber(boolean showLineNumber) {
        this.showLineNumber = showLineNumber;
    }

    public boolean isAutoCompete() {
        return autoCompete;
    }

    public void setAutoCompete(boolean autoCompete) {
        this.autoCompete = autoCompete;
    }

    public boolean isGccCompile() {
        return gccCompile==0;
    }

    public void setGccCompile(boolean gccCompile) {
        this.gccCompile = gccCompile?0:1;
    }

    private  int gccCompile;
    private  boolean autoCompete;
    private  boolean showLineNumber;
    private boolean darkMode;
    private boolean autoSave;
    private boolean showSymbolView;
    private  Setting()
    {
        update();
    }
    public static Setting getInstance(Context context) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        if(setting==null)
        {
            setting=new Setting();
        }
        return  setting;
    }

    public void update(){
        darkMode=sharedPreferences.getBoolean("editor_dark_mode",false);
        autoSave=sharedPreferences.getBoolean("editor_auto_save",true);
        showSymbolView=sharedPreferences.getBoolean("show_symbol_view",false);
        showLineNumber=sharedPreferences.getBoolean("show_line_number",true);
        autoCompete=sharedPreferences.getBoolean("auto_compete",true);
        gccCompile=Integer.parseInt(sharedPreferences.getString("select_compiler","0"));
    }
}
