package com.marakana.android.yamba;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.marakana.android.yamba.svc.YambaService;


public class TimelineActivity extends YambaActivity {
    private static final String DETAIL_FRAGMENT = "timeline.details";


    private boolean usingFrags;

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        if (!usingFrags) { startActivity(intent); }
        else { launchDetails(intent.getStringExtra(TimelineDetailFragment.PARAM_DETAILS)); }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_timeline == item.getItemId()) { return true; }
        else { return super.onOptionsItemSelected(item); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        usingFrags = null != findViewById(R.id.fragment_timeline_details);

        if (usingFrags) { initDetails(); }
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

    private void initDetails() {
        FragmentManager fragMgr = getFragmentManager();

        if (null != fragMgr.findFragmentByTag(DETAIL_FRAGMENT)) { return; }

        FragmentTransaction xact = fragMgr.beginTransaction();
        xact.add(
                R.id.fragment_timeline_details,
                TimelineDetailFragment.newInstance("nothing yet"),
                DETAIL_FRAGMENT);
        xact.commit();
    }

    private void launchDetails(String details) {
        FragmentManager fragMgr = getFragmentManager();

        FragmentTransaction xact = fragMgr.beginTransaction();

        xact.replace(
                R.id.fragment_timeline_details,
                TimelineDetailFragment.newInstance(details),
                DETAIL_FRAGMENT);

        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        xact.commit();
    }
}
