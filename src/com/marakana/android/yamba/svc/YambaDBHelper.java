package com.marakana.android.yamba.svc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class YambaDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "yamba.db";
    public static final int VERSION = 1;

    public static final String TABLE_TIMELINE = "timeline";
    public static final String COL_ID = "id";
    public static final String COL_TIMESTAMP = "created_at";
    public static final String COL_USER = "user";
    public static final String COL_STATUS = "status";

    public YambaDBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_TIMELINE + "("
                        + COL_ID + " INTEGER PRIMARY KEY,"
                        + COL_TIMESTAMP + " INTEGER,"
                        + COL_USER + " TEXT,"
                        + COL_STATUS + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_TIMELINE);
        onCreate(db);
    }
}
