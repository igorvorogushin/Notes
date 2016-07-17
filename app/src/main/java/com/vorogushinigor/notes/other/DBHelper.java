package com.vorogushinigor.notes.other;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by viv on 08.07.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "DB_NOTES";
    public static final String DB_TABLE_NAME = "TABLE_NOTES";
    public static final String DB_COL_ID="COL_ID";
    public static final String DB_COL_NAME="COL_NAME";
    public static final String DB_COL_MAIN="COL_MAIN";
    public static final String DB_COL_TIME_CREATED="COL_TIME_CREATED";
    public static final String DB_COL_TIME_CHANGED="COL_TIME_CHANGED";
    public static final String DB_COL_PATH_IMAGE="COL_PATH_IMAGE";
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+DB_TABLE_NAME+" ("
                + DB_COL_ID+ " integer primary key autoincrement,"
                + DB_COL_NAME+ " text,"
                + DB_COL_MAIN+ " text,"
                + DB_COL_TIME_CREATED+ " text,"
                + DB_COL_TIME_CHANGED+ " text,"
                + DB_COL_PATH_IMAGE+ " text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

