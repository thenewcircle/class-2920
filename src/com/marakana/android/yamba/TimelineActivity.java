package com.marakana.android.yamba;

import com.marakana.android.yamba.svc.YambaService;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class TimelineActivity extends ListActivity implements LoaderCallbacks<Cursor> {
    private static final String TAG = "TIMELINE";

    private static final int TIMELINE_LOADER = 64;

    private static final String[] PROJ = new String[] {
        YambaContract.Timeline.Columns.ID,
        YambaContract.Timeline.Columns.USER,
        YambaContract.Timeline.Columns.TIMESTAMP,
        YambaContract.Timeline.Columns.STATUS
    };

    private static final String[] FROM = new String[PROJ.length - 1];
    static { System.arraycopy(PROJ, 1, FROM, 0, FROM.length); }

    private static final int[] TO = new int[] {
        R.id.timeline_user,
        R.id.timeline_timestamp,
        R.id.timeline_status
    };

    public class TimelineBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (R.id.timeline_timestamp != view.getId()) { return false; }

            String s = "long ago";

            long t = cursor.getLong(columnIndex);
            if (0 < t) {
                s = DateUtils.getRelativeTimeSpanString(t, System.currentTimeMillis(), 0).toString();
            }

            ((TextView) view).setText(s);

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "create loader");
        return new CursorLoader(
                this,
                YambaContract.Timeline.URI,
                PROJ,
                null,
                null,
                YambaContract.Timeline.Columns.TIMESTAMP + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        Log.d(TAG, "load finished");
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        Log.d(TAG, "loader reset");
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display the cursor in the list view
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.timeline_row,
                null,
                FROM,
                TO,
                0);
        adapter.setViewBinder(new TimelineBinder());
        setListAdapter(adapter);

        // get a cursor from the content provider:
        //   -  init loader manager
        //   -  loader manager will ask Loader
        //   -  give it the loader: it will run it
        //   -  loader manager will give us the resulting cursor.
        getLoaderManager().initLoader(TIMELINE_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YambaService.startPolling(this);
    }

    @Override
    protected void onPause() {
        YambaService.stopPolling(this);
        super.onPause();
    }
}
