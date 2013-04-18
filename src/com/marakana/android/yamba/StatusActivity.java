package com.marakana.android.yamba;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.marakana.android.yamba.svc.YambaService;


public class StatusActivity extends Activity {
    private static final String TAG = "STATUS";

    private static final int MAX_STATUS_LEN = 140;
    private static final int WARN_CHAR_CNT = 10;
    private static final int ERROR_CHAR_CNT = 0;


    private TextView count;
    private EditText status;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        count = (TextView) findViewById(R.id.status_count);

        status = (EditText) findViewById(R.id.status_status);

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

        findViewById(R.id.status_submit).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v) { post(); }
                });
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

        YambaService.post(this, message);
    }
}
