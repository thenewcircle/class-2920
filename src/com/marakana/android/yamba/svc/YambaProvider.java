package com.marakana.android.yamba.svc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.marakana.android.yamba.BuildConfig;
import com.marakana.android.yamba.YambaContract;


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
        uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE,
                TIMELINE_DIR);
        uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE + "/#",
                TIMELINE_ITEM);
        uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE + "/#",
                TIMELINE_ITEM);

    }

    private static final ProjectionMap PROJ_MAP_TIMELINE = new ProjectionMap.Builder()
    .addColumn(YambaContract.Timeline.Columns.MAX_TIMESTAMP, "max(" + YambaDBHelper.COL_TIMESTAMP + ")")
    .build();

    private static final ColumnMap COL_MAP_TIMELINE = new ColumnMap.Builder()
    .addColumn(YambaContract.Timeline.Columns.TIMESTAMP, YambaDBHelper.COL_TIMESTAMP, ColumnMap.Type.LONG)
    /// fill me in
    .build();


    private YambaDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new YambaDBHelper(getContext());
        return null != dbHelper;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TIMELINE_ITEM:
                return YambaContract.Timeline.ITEM_TYPE;
            case TIMELINE_DIR:
                return YambaContract.Timeline.DIR_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "query: " + uri); }

        switch (uriMatcher.match(uri)) {
            case TIMELINE_DIR:
                break;

            default:
                throw new IllegalArgumentException("URI unsupported in query: " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            qb.setStrict(true);
        }

        qb.setProjectionMap(PROJ_MAP_TIMELINE.getProjectionMap());

        qb.setTables(YambaDBHelper.TABLE_TIMELINE);

        Cursor c = qb.query(getDb(), proj, sel, selArgs, null, null, sort);

        return c;
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

        SQLiteDatabase db = getDb();

        int count = 0;
        try {
            db.beginTransaction();
            for (ContentValues val: vals) {
                if (0 < db.insert(YambaDBHelper.TABLE_TIMELINE, null, COL_MAP_TIMELINE.translateCols(val))) {
                    count++;
                }
            }
            db.setTransactionSuccessful();
        }
        finally { db.endTransaction(); }

        return count;
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