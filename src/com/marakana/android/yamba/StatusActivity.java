package com.marakana.android.yamba;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

public class StatusActivity extends Activity {
    private TextView count;
    private EditText status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

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
    }

    // Set the number in the "count" text field to be 140
    // minus the number of characters in "status"
    // if n is > 10 make the text green
    // else if n is > 0 make the text yellow
    // else make the text red
    void updateCount() {
        // TODO Auto-generated method stub
        int n = 22;

        count.setText(String.valueOf(n));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
