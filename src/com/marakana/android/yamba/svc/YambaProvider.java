package com.marakana.android.yamba.svc;

import java.util.UUID;

import android.content.ContentProvider;
import android.content.ContentUris;
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
    private static final int POST_DIR = 3;

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
                YambaContract.Posts.TABLE,
                POST_DIR);
    }

    private static final ProjectionMap PROJ_MAP_TIMELINE = new ProjectionMap.Builder()
        .addColumn(YambaContract.Timeline.Columns.ID, YambaDBHelper.COL_ID)
        .addColumn(YambaContract.Timeline.Columns.TIMESTAMP, YambaDBHelper.COL_TIMESTAMP)
        .addColumn(YambaContract.Timeline.Columns.USER, YambaDBHelper.COL_USER)
        .addColumn(YambaContract.Timeline.Columns.STATUS, YambaDBHelper.COL_STATUS)
        .addColumn(YambaContract.Timeline.Columns.MAX_TIMESTAMP, "max(" + YambaDBHelper.COL_TIMESTAMP + ")")
        .build();

    private static final ColumnMap COL_MAP_TIMELINE = new ColumnMap.Builder()
        .addColumn(YambaContract.Timeline.Columns.ID, YambaDBHelper.COL_ID, ColumnMap.Type.LONG)
        .addColumn(YambaContract.Timeline.Columns.TIMESTAMP, YambaDBHelper.COL_TIMESTAMP, ColumnMap.Type.LONG)
        .addColumn(YambaContract.Timeline.Columns.USER, YambaDBHelper.COL_USER, ColumnMap.Type.STRING)
        .addColumn(YambaContract.Timeline.Columns.STATUS, YambaDBHelper.COL_STATUS, ColumnMap.Type.STRING)
        .build();

    private static final ColumnMap COL_MAP_POSTS = new ColumnMap.Builder()
        .addColumn(YambaContract.Posts.Columns.TIMESTAMP, YambaDBHelper.COL_TIMESTAMP, ColumnMap.Type.LONG)
        .addColumn(YambaContract.Posts.Columns.TRANSACTION, YambaDBHelper.COL_XACT, ColumnMap.Type.STRING)
        .addColumn(YambaContract.Posts.Columns.STATUS, YambaDBHelper.COL_STATUS, ColumnMap.Type.STRING)
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

        long pk = -1;
        switch (uriMatcher.match(uri)) {
            case TIMELINE_ITEM:
                pk = ContentUris.parseId(uri);

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

        if (0 < pk) { qb.appendWhere(YambaDBHelper.COL_ID + " = " + pk); }

        Cursor c = qb.query(getDb(), proj, sel, selArgs, null, null, sort);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
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

        if (0 < count) { getContext().getContentResolver().notifyChange(uri, null); }

        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues vals) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "insert@ " + uri + ": " + vals); }

        switch (uriMatcher.match(uri)) {
            case POST_DIR:
                break;

            default:
                throw new UnsupportedOperationException("URI unsupported in bulk insert: " + uri);
        }

        doPost(vals);

        long pk = getDb().insertOrThrow(YambaDBHelper.TABLE_POSTS, null, COL_MAP_POSTS.translateCols(vals));
        if (0 >= pk) { return null; }

        uri = uri.buildUpon().appendPath(String.valueOf(pk)).build();

        getContext().getContentResolver().notifyChange(uri, null);

        return uri;
    }

    @Override
    public int update(Uri uri, ContentValues vals, String where, String[] whereArgs) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "update@ " + uri + ": " + vals); }

        switch (uriMatcher.match(uri)) {
            case POST_DIR:
                break;

            default:
                throw new UnsupportedOperationException("URI unsupported in bulk insert: " + uri);
        }

        int n = getDb().update(YambaDBHelper.TABLE_POSTS, COL_MAP_POSTS.translateCols(vals), where, whereArgs);

        if (0 < n)  { getContext().getContentResolver().notifyChange(uri, null); }

        return n;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        throw new IllegalArgumentException("Unsupported operation: delete");
    }

    private SQLiteDatabase getDb() { return dbHelper.getWritableDatabase(); }

    private void doPost(ContentValues vals) {
        String status = vals.getAsString(YambaContract.Posts.Columns.STATUS);
        String xact = UUID.randomUUID().toString();
        YambaService.post(getContext(), status, xact);
        vals.put(YambaContract.Posts.Columns.TRANSACTION, xact);
    }
}