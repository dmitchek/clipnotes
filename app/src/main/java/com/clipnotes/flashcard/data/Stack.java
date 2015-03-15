package com.clipnotes.flashcard.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.clipnotes.flashcard.FlashCardsUtil;
import com.clipnotes.utils.SqlHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dave on 2/21/15.
 */
public class Stack {

    private static final String TABLE_NAME = "stacks";
    private String mUUID;
    private String mTitle;
    private String mSubjectId;
    private long   mDateCreated;
    private String mCreatedBy;

    private static final String UUID_COL = "uuid";
    private static final String TITLE = "title";
    private static final String SUBJECT_ID = "subject_id";
    private static final String DATE_CREATED = "date_created";
    private static final String CREATED_BY = "created_by";

    public static final Map<String, String> STACK_COLUMNS;
    static {
        Map<String, String> columns = new HashMap<String, String>();
        columns.put(UUID_COL, "TEXT");
        columns.put(DATE_CREATED, "INTEGER");
        columns.put(TITLE, "TEXT");
        columns.put(SUBJECT_ID, "TEXT");
        columns.put(CREATED_BY, "TEXT");
        STACK_COLUMNS = Collections.unmodifiableMap(columns);
    }


    public Stack()
    {
        init("", "", "", 0, "");
    }

    public Stack(String uuid, String title, String subjectId, long dateCreated,
                 String createdBy)

    {
        init(uuid,
             title,
             subjectId,
             dateCreated,
             createdBy);
    }

    public Stack(Cursor cursor)
    {
        init(cursor.getString(cursor.getColumnIndex(UUID_COL)),
             cursor.getString(cursor.getColumnIndex(TITLE)),
             cursor.getString(cursor.getColumnIndex(SUBJECT_ID)),
             cursor.getLong(cursor.getColumnIndex(DATE_CREATED)),
             cursor.getString(cursor.getColumnIndex(CREATED_BY)));
    }

    private void init(String uuid, String title, String subjectId,
                      long dateCreated, String createdBy)
    {
        mUUID = uuid;
        mTitle = title;
        mSubjectId = subjectId;
        mDateCreated = dateCreated;
        mCreatedBy = createdBy;
    }

    public String getUUID() { return mUUID;}
    public String getTitle() { return mTitle; }
    public String getSubjectId() { return mSubjectId; }
    public long getDateCreated() { return mDateCreated; }
    public String getCreatedBy() { return mCreatedBy; }

    public static Stack createStack(String title, String subjectId, String createdBy)
    {
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis() / 1000L;

        return new Stack(uuid, title, subjectId, timestamp, createdBy);
    }

    public static long insertStack(Context context, Stack stack) {

        SqlHelper sqlHelper = new SqlHelper(context, FlashCardsUtil.DATABASE_NAME);

        sqlHelper.create(TABLE_NAME, STACK_COLUMNS);

        SQLiteDatabase database = sqlHelper.getDatabase();

        ContentValues values = new ContentValues();

        values.put(UUID_COL, stack.getUUID());
        values.put(TITLE, stack.getTitle());
        values.put(SUBJECT_ID, stack.getSubjectId());
        values.put(DATE_CREATED, stack.getDateCreated());
        values.put(CREATED_BY, stack.getCreatedBy());

        return database.insert(TABLE_NAME, null, values);
    }

    public static Stack getStackByUUID(Context context, String uuid)
    {
        Cursor cursor = FlashCardsUtil.getByStringValue(context,TABLE_NAME, FlashCardsUtil.getColumns(STACK_COLUMNS),
                UUID_COL, uuid);

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return new Stack(cursor);
        }
        else
            return new Stack();
    }
}
