package com.marakana.android.yamba;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineFragment extends ListFragment  implements LoaderCallbacks<Cursor> {
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "create loader");
        return new CursorLoader(
                getActivity(),
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        // display the cursor in the list view
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(),
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

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        Cursor c = (Cursor) getListAdapter().getItem(pos);
        String details = c.getString(c.getColumnIndex(YambaContract.Timeline.Columns.STATUS));
        Log.d(TAG, "details: " + details);
        Intent i = new Intent(getActivity(), TimelineDetailActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }
 }
