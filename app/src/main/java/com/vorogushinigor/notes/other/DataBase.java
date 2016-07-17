package com.vorogushinigor.notes.other;

/**
 * Created by viv on 07.07.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DataBase {
    private static final String MYLOG="DataBase";
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DataBase(Context context) {
        Log.i(MYLOG, "create");
        mDBHelper = new DBHelper(context);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void insert(Notes notes) {
        ContentValues cv = getCV(notes);
        long insertCount = mDB.insert(DBHelper.DB_TABLE_NAME, null, cv);
        Log.i(MYLOG, "insert " + String.valueOf(insertCount));
    }

    public void update(Notes notes) {
        ContentValues cv = getCV(notes);
        int updateCount = mDB.update(DBHelper.DB_TABLE_NAME, cv, DBHelper.DB_COL_TIME_CREATED + "= ?", new String[]{notes.getTimeCreated()});
        Log.i(MYLOG, "update " + String.valueOf(updateCount));
    }

    private ContentValues getCV(Notes notes) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.DB_COL_NAME, notes.getName());
        cv.put(DBHelper.DB_COL_MAIN, notes.getMain());
        cv.put(DBHelper.DB_COL_TIME_CREATED, notes.getTimeCreated());
        cv.put(DBHelper.DB_COL_TIME_CHANGED, notes.getTimeChanged());
        cv.put(DBHelper.DB_COL_PATH_IMAGE, notes.getPathPhoto());
        return cv;
    }

    public ArrayList<Notes> readAll() {
        Log.i(MYLOG, "readAll");
        ArrayList<Notes> notesArrayList = new ArrayList<>();
        Cursor c = mDB.query(DBHelper.DB_TABLE_NAME, null, null, null, null, null, null);
        int i = 0;
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(DBHelper.DB_COL_ID);
            int nameIndex = c.getColumnIndex(DBHelper.DB_COL_NAME);
            int mainIndex = c.getColumnIndex(DBHelper.DB_COL_MAIN);
            int time1Index = c.getColumnIndex(DBHelper.DB_COL_TIME_CREATED);
            int time2Index = c.getColumnIndex(DBHelper.DB_COL_TIME_CHANGED);
            int pathIndex = c.getColumnIndex(DBHelper.DB_COL_PATH_IMAGE);
            do {
                try {
                    Notes notes = new Notes(c.getString(nameIndex), c.getString(mainIndex), c.getString(time1Index), c.getString(time2Index), c.getString(pathIndex));
                    notesArrayList.add(i, notes);
                    i++;
                } catch (Exception e) {
                    Log.i(MYLOG, "error read");
                }

            } while (c.moveToNext());
        } else {
            Log.i(MYLOG, "0 rows");
        }
        c.close();
        return notesArrayList;
    }

    public void delete(Notes notes) {
        int delCount = mDB.delete(DBHelper.DB_TABLE_NAME, DBHelper.DB_COL_TIME_CREATED + "='" + notes.getTimeCreated() + "'", null);
        Log.i(MYLOG, "delete " + String.valueOf(delCount));
    }

    public void deleteAll() {
        Log.i(MYLOG, "DB: deleteAll");
        int delNumberCount = mDB.delete(DBHelper.DB_TABLE_NAME, null, null);
        Log.i(MYLOG, "deleted all (" + delNumberCount + ")");
    }

    public void close(){
        Log.i(MYLOG, "DB: close");
        mDB.close();
    }
}
