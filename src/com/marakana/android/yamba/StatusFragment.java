package com.marakana.android.yamba;

import com.marakana.android.yamba.svc.YambaService;

import android.app.Fragment;
import android.graphics.Color;
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
    private static final String TAG = "STATUS";

    private static final int MAX_STATUS_LEN = 140;
    private static final int WARN_CHAR_CNT = 10;
    private static final int ERROR_CHAR_CNT = 0;


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
        final String message = status.getText().toString();

        if (TextUtils.isEmpty(message)) { return; }

        status.setText("");

        YambaService.post(getActivity(), message);
    }
}
