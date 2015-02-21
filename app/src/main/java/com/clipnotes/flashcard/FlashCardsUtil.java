package com.clipnotes.flashcard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    private final String DATABASE_NAME = "Flashcards.sqlite";
    private final String TABLE_NAME = "cards";
    private Context mContext;
    private SqlHelper mSqlHelper;
    private SQLiteDatabase mDatabase;
    private static final Map<String, String> mColumns;
    static {
        Map<String, String> columns = new HashMap<String, String>();
        columns.put("uuid", "TEXT");
        columns.put("date_created", "INTEGER");
        columns.put("front_text", "TEXT");
        columns.put("back_text", "TEXT");
        columns.put("category", "INTEGER");
        columns.put("sub_category", "INTEGER");
        columns.put("front_image", "BLOB");
        columns.put("back_image", "BLOB");
        mColumns = Collections.unmodifiableMap(columns);
    }

    public class CardData
    {
        private String mUUID;
        private String mFront;
        private String mBack;
        private int mCategory;
        private int mSubCategory;
        private Bitmap mFrontImage;
        private Bitmap mBackImage;

        public CardData(String uuid, String front, String back, int category, int sub_category,
                                                                Bitmap frontImage, Bitmap backImage)
        {
            init(uuid,
                 front,
                 back,
                 category,
                 sub_category,
                 frontImage,
                 backImage);
        }

        public CardData(Cursor cursor)
        {
            init(cursor.getString(cursor.getColumnIndex("uuid")),
                     cursor.getString(cursor.getColumnIndex("front_text")),
                     cursor.getString(cursor.getColumnIndex("back_text")),
                     cursor.getInt(cursor.getColumnIndex("category")),
                     cursor.getInt(cursor.getColumnIndex("sub_category")),
                     getBitmap(cursor.getBlob(cursor.getColumnIndex("front_image"))),
                     getBitmap(cursor.getBlob(cursor.getColumnIndex("back_image"))));

        }

        private void init(String uuid, String front, String back, int category, int sub_category,
                          Bitmap frontImage, Bitmap backImage)
        {
            mUUID = uuid;
            mFront = front;
            mBack = back;
            mCategory = category;
            mSubCategory = sub_category;
            mFrontImage = frontImage;
            mBackImage = backImage;
        }

        public String getUUID() { return mUUID; }
        public String getBack() {
            return mBack;
        }
        public String getFront() {
            return mFront;
        }
        public int getCategory() { return mCategory; }
        public int getSubCategory() { return mSubCategory; }
        public Bitmap getFrontImage() { return mFrontImage; }
        public Bitmap getBackImage() { return mBackImage; }

        public void setFront(String front)
        {
            mFront = front;
        }

        public void setBack(String back)
        {
            mBack = back;
        }
    }

    public FlashCardsUtil(Context context)
    {
        mContext = context;

        mSqlHelper = new SqlHelper(context, DATABASE_NAME);

        mSqlHelper.create(TABLE_NAME, mColumns);

        mDatabase = mSqlHelper.getDatabase();
    }

    public long createCard(String front, String back, int category, int sub_category, Bitmap frontBmp, Bitmap backBmp)
    {
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis() / 1000L;

        ContentValues values = new ContentValues();

        values.put("uuid", uuid);
        values.put("date_created", timestamp);
        values.put("front_text",   front);
        values.put("back_text",    back);
        values.put("category",     category);
        values.put("sub_category", sub_category);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(frontBmp != null)
        {
            frontBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            values.put("front_image",  stream.toByteArray());
        }

        if(backBmp != null)
        {
            backBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            values.put("back_image", stream.toByteArray());
        }

        return mDatabase.insert(TABLE_NAME, null, values);
    }

    public CardData getCardByUUID(String uuid)
    {
        return getCardByValue("uuid", uuid);
    }

    public ArrayList<CardData> getCardsByCategory(int category)
    {
        return getCardsByValue("category", Integer.toString(category));
    }

    public ArrayList<CardData> getCardsBySubCategory(int sub_category)
    {
        return getCardsByValue("sub_category", Integer.toString(sub_category));
    }

    private CardData getCardByValue(String column, String value)
    {
        String[] whereArgs = new String[1];
        whereArgs[0] = value;
        Cursor cursor = mDatabase.query(TABLE_NAME, getColumns(), "where " + column + " = ?", whereArgs, null, null, null);

        return new CardData(cursor);
    }

    private ArrayList<CardData> getCardsByValue(String column, String value)
    {
        String[] whereArgs = new String[1];
        whereArgs[0] = value;
        Cursor cursor = mDatabase.query(TABLE_NAME, getColumns(), column + " = ?", whereArgs, null, null, null);

        ArrayList<CardData> cards = new ArrayList<>();
        while(cursor.moveToNext())
        {
            cards.add(new CardData(cursor));
        }

        return cards;
    }

    private String[] getColumns()
    {
        String[] columns = new String[mColumns.size()];

        int index = 0;
        for(Map.Entry<String, String> entry : mColumns.entrySet())
        {
            columns[index] = entry.getKey();
            index++;
        }

        return columns;
    }

    private Bitmap getBitmap(byte[] bytes)
    {
        if(bytes != null)
            return BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length);
        else
            return null;
    }
}
