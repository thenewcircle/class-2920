package com.marakana.android.yamba;

import android.os.Bundle;

import com.marakana.android.yamba.svc.YambaService;


public class TimelineActivity extends YambaActivity {

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
