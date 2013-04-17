package com.marakana.android.yamba.svc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.marakana.android.yamba.BuildConfig;


/**
 * YambaProvider
 */
public class YambaProvider extends ContentProvider {
    private static final String TAG = "CP";

    private static final int TIMELINE_DIR = 1;
    private static final int TIMELINE_ITEM = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    private static final ProjectionMap PROJ_MAP_TIMELINE = new ProjectionMap.Builder()
        .build();

    private static final ColumnMap COL_MAP_TIMELINE = new ColumnMap.Builder()
        .build();


    private YambaDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "query: " + uri); }

        switch (uriMatcher.match(uri)) {
            case TIMELINE_ITEM:

            case TIMELINE_DIR:
                break;

            default:
                throw new IllegalArgumentException("URI unsupported in query: " + uri);
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues vals) {
        throw new IllegalArgumentException("Unsupported operation: insert");
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] vals) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "bulk insert: " + uri); }

        switch (uriMatcher.match(uri)) {
            case TIMELINE_DIR:
                break;

            default:
                throw new UnsupportedOperationException("URI unsupported in bulk insert: " + uri);
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues vals, String where, String[] whereArgs) {
        throw new IllegalArgumentException("Unsupported operation: insert");
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        throw new IllegalArgumentException("Unsupported operation: delete");
    }

    private SQLiteDatabase getDb() { return dbHelper.getWritableDatabase(); }
}