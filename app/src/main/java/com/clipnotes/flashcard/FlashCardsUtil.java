package com.clipnotes.flashcard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.clipnotes.flashcard.data.Card;
import com.clipnotes.utils.SqlHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dave on 2/15/15.
 */
public class FlashCardsUtil {

    public static final String DATABASE_NAME = "Flashcards.sqlite";

    public static String[] getColumns(Map<String, String> columns)
    {
        String[] columnStrings = new String[columns.size()];

        int index = 0;
        for(Map.Entry<String, String> entry : columns.entrySet())
        {
            columnStrings[index] = entry.getKey();
            index++;
        }

        return columnStrings;
    }

    public static SQLiteDatabase getDatabase(Context context)
    {
        SqlHelper sqlHelper = new SqlHelper(context, DATABASE_NAME);

        return sqlHelper.getDatabase();
    }

    public static Cursor getByStringValue(Context context, String table, String[] columns, String column, String value)
    {
        return FlashCardsUtil.getDatabase(context).query(table, columns,
                                                                  column + " LIKE '%" + value + "%'",
                                                                  null, null, null, null);
    }
}
