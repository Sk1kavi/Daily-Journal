package com.example.dailyjournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

public class JournalDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "journal.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ENTRIES = "entries";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_MOOD = "mood";
    public static final String COLUMN_PHOTOS = "photos";
    public static final String COLUMN_VIDEOS = "videos";

    public JournalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ENTRIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_MOOD + " TEXT, " +
                COLUMN_PHOTOS + " TEXT, " +  // Store file paths as comma-separated strings
                COLUMN_VIDEOS + " TEXT);";   // Store file paths as comma-separated strings
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    public long addEntry(String date, String description, String mood, String photos, String videos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_MOOD, mood);
        values.put(COLUMN_PHOTOS, photos);
        values.put(COLUMN_VIDEOS, videos);
        return db.insert(TABLE_ENTRIES, null, values);
    }

    public Cursor getAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ENTRIES,
                new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_DESCRIPTION, COLUMN_MOOD},
                null, null, null, null, COLUMN_DATE + " DESC");
    }

    public Cursor getEntry(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ENTRIES,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public int updateEntry(long id, String description, String mood, String photos, String videos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_MOOD, mood);
        values.put(COLUMN_PHOTOS, photos);
        values.put(COLUMN_VIDEOS, videos);
        return db.update(TABLE_ENTRIES, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    public Cursor searchEntries(String query, String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = null;
        ArrayList<String> selectionArgs = new ArrayList<>();

        if (!TextUtils.isEmpty(query)) {
            selection = COLUMN_DESCRIPTION + " LIKE ?";
            selectionArgs.add("%" + query + "%");
        }

        if (!TextUtils.isEmpty(date)) {
            if (selection != null) {
                selection += " AND ";
            } else {
                selection = "";
            }
            selection += COLUMN_DATE + " = ?";
            selectionArgs.add(date);
        }

        return db.query(
                TABLE_ENTRIES,
                null,
                selection,
                selectionArgs.size() > 0 ? selectionArgs.toArray(new String[0]) : null,
                null,
                null,
                COLUMN_DATE + " DESC"
        );
    }
}