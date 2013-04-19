package com.marakana.android.yamba;

import android.os.Bundle;
import android.widget.TextView;


public class TimelineDetailActivity extends YambaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_detail);
        ((TextView) findViewById(R.id.timeline_detail))
            .setText(getIntent().getStringExtra(TimelineDetailFragment.PARAM_DETAILS));
    }
}
