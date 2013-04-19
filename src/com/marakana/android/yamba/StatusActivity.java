package com.marakana.android.yamba;

import android.os.Bundle;
import android.view.MenuItem;


public class StatusActivity extends YambaActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_status == item.getItemId()) { return true; }
        else { return super.onOptionsItemSelected(item); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
    }
}
