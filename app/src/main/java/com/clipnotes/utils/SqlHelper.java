package com.clipnotes.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by dave on 2/15/15.
 */
public class SqlHelper {

    private final String TAG = "SqlHelper";
    private String mDatabaseName;
    private String mDatabaseTable;
    private SQLiteDatabase mDatabase;
    private DatabaseErrorHandler mErrorHandler;
    private SQLiteDatabase.CursorFactory mCursorFactory;

    public SqlHelper(Context context, String name)
    {
        File mDatabaseFile = context.getDatabasePath(name);
        mDatabaseName = name;

        SQLiteDatabase.CursorFactory cursorFactory = new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                return null;
            }
        };

        mErrorHandler = new DatabaseErrorHandler() {
            @Override
            public void onCorruption(SQLiteDatabase dbObj) {
                Log.v(TAG, "Database corruption: " + dbObj.getPath());
            }
        };

        mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabaseFile, mCursorFactory);

    }

    public void create(String table, Map<String, String> columns)
    {
        String query = "CREATE TABLE IF NOT EXISTS " + table + " (";

        int index = 0;
        for(Map.Entry<String, String> entry : columns.entrySet())
        {
            query += entry.getKey() + " " + entry.getValue();

            index++;
            if(index < columns.size())
                query += ", ";
        }

        query += ");";

        mDatabase.execSQL(query);
    }

    public void insert(List<Object> values)
    {
        String query = "INSERT INTO " + mDatabaseTable + " VALUES(";

        for(Object obj : values)
        {
            if (obj instanceof String){
                query += "'" + obj.toString() + "'";
            } else
            {
                query += obj.toString();
            }

            query += ", ";
        }

        query += ");";

        mDatabase.execSQL(query);
    }

    public SQLiteDatabase getDatabase()
    {
        return mDatabase;
    }
}
