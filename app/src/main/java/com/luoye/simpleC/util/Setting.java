package com.luoye.simpleC.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
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

    private  boolean autoCompete;
    private  boolean showLineNumber;
    private boolean darkMode;
    private boolean autoSave;
    private boolean showSymbolView;
    private  Setting()
    {
    }
    public static Setting getInstance(Context context)
    {
        if(setting==null)
        {
            setting=new Setting();
        }
       sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        return  setting;
    }

    public void update(){
        darkMode=sharedPreferences.getBoolean("editor_dark_mode",false);
        autoSave=sharedPreferences.getBoolean("editor_auto_save",true);
        showSymbolView=sharedPreferences.getBoolean("show_symbol_view",false);
        showLineNumber=sharedPreferences.getBoolean("show_line_number",true);
        autoCompete=sharedPreferences.getBoolean("auto_compete",true);
    }
}
