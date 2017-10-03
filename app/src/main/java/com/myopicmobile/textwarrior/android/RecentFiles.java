/*
 * Copyright (c) 2011 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.android;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RecentFiles {
	/** Maximum number of files stored in file history */
	public final static int MAX_HISTORY_SIZE = 10;
	private LinkedList<String> _recentFiles = new LinkedList<String>();
    private final Context _ctx;

    public RecentFiles(Context ctx) {
        this._ctx = ctx;
        loadFromPersistentStore();
    }

    public List<String> getRecentFiles(){
    	return _recentFiles;
    }
    
    /**
     * Adds filename to the top of the recent history list.
     * If filename was previously in the list, it will be promoted to
     * the top of the list.
     * 
     * @param filename Full path of file to add to the recent history list
     */
    public void addRecentFile(String filename){
    	//remove existing entry, if any
    	_recentFiles.remove(filename);
    	
    	//promote to head of list
    	_recentFiles.addFirst(filename);
    	
    	//trim list
    	if(_recentFiles.size() > MAX_HISTORY_SIZE){
    		_recentFiles.removeLast();
    	}
    }
    
    /**
     * Open the recent files database. If it cannot be opened, try to create a 
     * new instance. If it cannot be created, throw an exception.
     * 
     * @throws SQLException if the database could be neither opened or created
     */
    private void loadFromPersistentStore() throws SQLException {
        RecentFilesDbHelper dbHelper = new RecentFilesDbHelper(_ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(DATABASE_TABLE, new String[] {KEY_FILENAME},
        		null, null, null, null, KEY_RECENT_RANK);
        int filenameColumn = c.getColumnIndexOrThrow(KEY_FILENAME);
        while(c.moveToNext()){
        	_recentFiles.add(c.getString(filenameColumn));
        }
        c.close();
        dbHelper.close();
    }
    
    public void save(){
        RecentFilesDbHelper dbHelper = new RecentFilesDbHelper(_ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
    	// Delete all rows and add the contents of _recentFiles.
    	// This is simpler than checking the db if a filename exists and then
    	// calculating and updating the appropriate rank.
    	db.delete(DATABASE_TABLE, null, null);
    	
        ContentValues initialValues = new ContentValues();
    	for(int i = 0; i < _recentFiles.size(); ++i){
            initialValues.put(KEY_FILENAME, _recentFiles.get(i));
            initialValues.put(KEY_RECENT_RANK, i);
    		db.insert(DATABASE_TABLE, null, initialValues);
    	}
        dbHelper.close();
    }

    private static final String TAG = "RecentFiles";

    private static final String DATABASE_NAME = "textwarrior";
    private static final String DATABASE_TABLE = "recent";
    private static final int DATABASE_VERSION = 1;
    
    private static final String KEY_FILENAME = "filename";
    private static final String KEY_RECENT_RANK = "rank";
    private static final String KEY_ROW_ID = "_id";
    
    private static final String DATABASE_CREATE =
        "create table " + DATABASE_TABLE + " ("
        + KEY_ROW_ID + " integer primary key autoincrement, "
        + KEY_FILENAME + " text unique not null, "
        + KEY_RECENT_RANK + " integer unique not null);";

    private static final String DATABASE_DELETE =
    	"DROP TABLE IF EXISTS " + DATABASE_TABLE;


    private static class RecentFilesDbHelper extends SQLiteOpenHelper {
        RecentFilesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(DATABASE_DELETE);
            onCreate(db);
        }
    }

}
