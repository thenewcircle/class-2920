package com.marakana.android.yamba;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;

import com.marakana.android.yamba.svc.YambaService;


public class TimelineActivity extends ListActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
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
