package com.marakana.android.yamba;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class StatusActivity extends Activity {
    private static final String TAG = "STATUS";

    private static final int MAX_STATUS_LEN = 140;
    private static final int WARN_CHAR_CNT = 10;
    private static final int ERROR_CHAR_CNT = 0;

    private YambaClient yamba;
    private TextView count;
    private EditText status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        yamba = new YambaClient("student", "password", "http://yamba.marakana.com/api");

        count =  (TextView) findViewById(R.id.status_count);

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

        ((Button) findViewById(R.id.status_submit)).setOnClickListener(
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
     * post status to the network
     *
     * !!!NAIVE SOLUTION!  DOES NOT WORK!!!
     * - can't resolve hostname yamba.marakana.com (strict mode)
     * - can't use network from UI thread
     * - Application not responding.
     */
    void post() {
        String message = status.getText().toString();

        if (TextUtils.isEmpty(message)) { return; }

        status.setText("");

        int success = R.string.post_succeeded;
        try { yamba.postStatus(message); }
        catch (YambaClientException e) {
            success = R.string.post_failed;
            Log.w(TAG, "post failed: ", e);
        }

        Toast.makeText(this, success, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
