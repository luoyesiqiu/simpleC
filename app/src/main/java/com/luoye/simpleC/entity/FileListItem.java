package com.luoye.simpleC.entity;

import android.graphics.Bitmap;

/**
 * Created by zyw on 2017/2/15.
 */
public class FileListItem {

    public static final int UP_ITEM=0x1;
    public static final int FOLDER_ITEM=0x2;
    public static final int FILE_ITEM=0x3;
    private String name;
    private String size;
    private Bitmap icon;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private  int type;



    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public FileListItem(Bitmap icon, String name, String size, int type) {
        this.name = name;
        this.size = size;
        this.icon=icon;
        this.type=type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

}
