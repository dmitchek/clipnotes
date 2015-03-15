package com.clipnotes.flashcard.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
public class Card {

    private static final String TABLE_NAME = "cards";
    private String mUUID;
    private String mFront;
    private String mBack;
    private Bitmap mFrontImage;
    private Bitmap mBackImage;
    private String mStackUUID;
    private long   mDateCreated;
    private String mCreatedBy;

    private static final String UUID_COL =     "uuid";
    private static final String FRONT_TEXT =   "front_text";
    private static final String BACK_TEXT =    "back_text";
    private static final String FRONT_IMAGE =  "front_image";
    private static final String BACK_IMAGE =   "back_image";
    private static final String STACK_UUID =   "stack_uuid";
    private static final String DATE_CREATED = "date_created";
    private static final String CREATED_BY =   "created_by";


    public static final Map<String, String> CARD_COLUMNS;
    static {
        Map<String, String> columns = new HashMap<String, String>();
        columns.put(UUID_COL, "TEXT");
        columns.put(DATE_CREATED, "INTEGER");
        columns.put(FRONT_TEXT, "TEXT");
        columns.put(BACK_TEXT, "TEXT");
        columns.put(FRONT_IMAGE, "BLOB");
        columns.put(BACK_IMAGE, "BLOB");
        columns.put(STACK_UUID, "TEXT");
        columns.put(CREATED_BY, "TEXT");
        CARD_COLUMNS = Collections.unmodifiableMap(columns);
    }

    public Card()
    {
        init("", "", "", null, null, "", 0, "");
    }

    public Card(String uuid, String front, String back, Bitmap frontImage, Bitmap backImage,
                    String stack, long dateCreated, String createdBy)

    {
        init(uuid,
                front,
                back,
                frontImage,
                backImage,
                stack,
                dateCreated,
                createdBy);
    }

    public Card(Cursor cursor)
    {
        init(cursor.getString(cursor.getColumnIndex(UUID_COL)),
            cursor.getString(cursor.getColumnIndex(FRONT_TEXT)),
            cursor.getString(cursor.getColumnIndex(BACK_TEXT)),
            getBitmap(cursor.getBlob(cursor.getColumnIndex(FRONT_IMAGE))),
            getBitmap(cursor.getBlob(cursor.getColumnIndex(BACK_IMAGE))),
            cursor.getString(cursor.getColumnIndex(STACK_UUID)),
            cursor.getLong(cursor.getColumnIndex(DATE_CREATED)),
            cursor.getString(cursor.getColumnIndex(CREATED_BY)));
    }

    private void init(String uuid, String front, String back, Bitmap frontImage, Bitmap backImage,
                      String stack, long dateCreated, String createdBy)
    {
        mUUID = uuid;
        mFront = front;
        mBack = back;
        mFrontImage = frontImage;
        mBackImage = backImage;
        mStackUUID = stack;
        mDateCreated = dateCreated;
        mCreatedBy = createdBy;
    }

    public String getUUID() { return mUUID; }
    public String getBack() { return mBack; }
    public String getFront() { return mFront; }
    public Bitmap getFrontImage() { return mFrontImage; }
    public Bitmap getBackImage() { return mBackImage; }
    public String getStackUUID() { return mStackUUID; }
    public long   getDateCreated() { return mDateCreated; }
    public String getCreatedBy()   { return mCreatedBy; }

    public static Card createCard(String front, String back, Bitmap frontBmp, Bitmap backBmp,
                           String stack, String createdBy)
    {
        String uuid = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis() / 1000L;

        return new Card(uuid, front, back, frontBmp, backBmp, stack, timestamp, createdBy);
    }

    public static long insertCard(Context context, Card card) {

        SqlHelper sqlHelper = new SqlHelper(context, FlashCardsUtil.DATABASE_NAME);

        sqlHelper.create(TABLE_NAME, CARD_COLUMNS);

        SQLiteDatabase database = sqlHelper.getDatabase();

        ContentValues values = new ContentValues();

        values.put(UUID_COL, card.getUUID());
        values.put(FRONT_TEXT, card.getFront());
        values.put(BACK_TEXT, card.getBack());
        values.put(STACK_UUID, card.getStackUUID());
        values.put(DATE_CREATED, card.getDateCreated());
        values.put(CREATED_BY, card.getCreatedBy());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (card.getFrontImage() != null) {
            card.getFrontImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
            values.put(FRONT_IMAGE, stream.toByteArray());
        }

        if (card.getBackImage() != null) {
            card.getBackImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
            values.put(BACK_IMAGE, stream.toByteArray());
        }

        return database.insert(TABLE_NAME, null, values);
    }

    public static Card getCardByUUID(Context context, String uuid)
    {
        Cursor cursor = FlashCardsUtil.getByStringValue(context,TABLE_NAME, FlashCardsUtil.getColumns(CARD_COLUMNS),
                UUID_COL, uuid);

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return new Card(cursor);
        }
        else
            return new Card();
    }

    public static ArrayList<Card> getCardsByStack(Context context, String stack)
    {
        Cursor cursor = FlashCardsUtil.getByStringValue(context, TABLE_NAME, FlashCardsUtil.getColumns(CARD_COLUMNS),
                STACK_UUID, stack);

        ArrayList<Card> cards = new ArrayList<>();
        if(cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                cards.add(new Card(cursor));
            }
        }

        return cards;
    }

    private Bitmap getBitmap(byte[] bytes)
    {
        if(bytes != null)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        else
            return null;
    }
}
