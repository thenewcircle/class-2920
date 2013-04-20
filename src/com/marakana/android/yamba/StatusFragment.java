package com.marakana.android.yamba;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class StatusFragment extends Fragment {
    private static final int MAX_STATUS_LEN = 140;
    private static final int WARN_CHAR_CNT = 10;
    private static final int ERROR_CHAR_CNT = 0;

    private static class Poster extends AsyncTask<String, Void, Void> {
        private ContentResolver resolver;

        public Poster(ContentResolver resolver) { this.resolver = resolver; }

        @Override
        protected Void doInBackground(String... params) {
            ContentValues status = new ContentValues();
            status.put(YambaContract.Posts.Columns.STATUS, params[0]);
            resolver.insert(YambaContract.Posts.URI, status);
            return null;
        }

        @Override
        protected void onCancelled(Void result) { cleanup(); }

        @Override
        protected void onPostExecute(Void result) { cleanup(); }

        private void cleanup() { poster = null; }
    }

    private static Poster poster;


    private TextView count;
    private EditText status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status, container, false);

        count = (TextView) v.findViewById(R.id.status_count);

        status = (EditText) v.findViewById(R.id.status_status);

        status.addTextChangedListener(
                new TextWatcher() {
                    @Override public void afterTextChanged(Editable s) { }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,  int n, int a) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        updateCount();
                    }
                } );

        v.findViewById(R.id.status_submit).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) { post(); }
                });

        return v;
    }

    /**
     * Set the number in the "count" text field to be 140
     * minus the number of characters in "status"
     * if n is > 10 make the text green
     * else if n is > 0 make the text yellow
     * else make the text red
     */
    void updateCount() {
        int n = MAX_STATUS_LEN - status.getText().toString().length();

        int color = Color.GREEN;
        if (ERROR_CHAR_CNT > n) { color = Color.RED; }
        else if (WARN_CHAR_CNT > n) { color = Color.YELLOW; }

        count.setText(String.valueOf(n));
        count.setTextColor(color);
    }

    /**
     * Get the text from status EditText object
     * Clear the status
     * Post status to the network
     */
    void post() {
        if (null != poster)  { return; }

        final String message = status.getText().toString();

        if (TextUtils.isEmpty(message)) { return; }

        status.setText("");

        poster = new Poster(getActivity().getContentResolver());
        poster.execute(message);
    }
}
