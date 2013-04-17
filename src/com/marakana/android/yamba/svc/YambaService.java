package com.marakana.android.yamba.svc;

import java.util.ArrayList;
import java.util.List;

import com.marakana.android.yamba.YambaContract;
import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    public static final long POLL_INTERVAL = 2 * 60 * 1000;

    private static final int POLLER_INTENT_TAG = 42;

    private static final String PARAM_OP = "YambaService.OP";
    private static final int OP_POST = 6001;
    private static final int OP_POLL = 6002;
    private static final int OP_POLL_START = 6003;
    private static final int OP_POLL_STOP = 6004;

    private static final String PARAM_STATUS = "YambaService.STATUS";

    private static class SafeYambaClient {
        public static final int MAX_MESSAGES = 30;

        private YambaClient yamba;

        public synchronized List<Status> getTimeline() throws YambaClientException {
            return getYambaClient().getTimeline(MAX_MESSAGES);
        }

        public synchronized void postStatus(String message) throws YambaClientException {
            getYambaClient().postStatus(message);
        }

        private YambaClient getYambaClient() {
            if (null == yamba) {
                yamba = new YambaClient("student", "password", "http://yamba.marakana.com/api");
            }
            return yamba;
        }
    }

    public static void post(Context ctxt, String status) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POST);
        i.putExtra(PARAM_STATUS, status);
        ctxt.startService(i);
    }

    public static void startPolling(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLL_START);
        ctxt.startService(i);
    }

    public static void stopPolling(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLL_STOP);
        ctxt.startService(i);
    }


    public SafeYambaClient yamba;

    public YambaService() { super(TAG); }

    @Override
    public void onCreate() {
        super.onCreate();
        yamba = new SafeYambaClient();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int op = intent.getIntExtra(PARAM_OP, 0);
        switch (op) {
        case OP_POST:
            doPost(intent.getStringExtra(PARAM_STATUS));
            break;

        case OP_POLL:
            doPoll();
            break;

        case OP_POLL_START:
            doStartPolling();
            break;

        case OP_POLL_STOP:
            doStopPolling();
            break;

        default:
            throw new IllegalStateException("Unknown op: " + op);
        }
    }

    private void doPoll() {
        List<Status> timeline = null;
        try { timeline = yamba.getTimeline(); }
        catch (YambaClientException e) {
            Log.w(TAG, "post failed: ", e);
        }

        processTimeline(timeline);
    }

    private void doPost(String message) {
        if (TextUtils.isEmpty(message)) { return; }

        try { yamba.postStatus(message); }
        catch (YambaClientException e) {
            Log.w(TAG, "post failed: ", e);
        }
    }

    private void doStartPolling() {
        Log.d(TAG, "Polling started");
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
            .setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + 100,
                POLL_INTERVAL,
                getPollIntent());
    }

    private void doStopPolling() {
        Log.d(TAG, "Polling stopped");
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
            .cancel(getPollIntent());
    }

    private PendingIntent getPollIntent() {
        Intent i = new Intent(this, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLL);
        return PendingIntent.getService(this, POLLER_INTENT_TAG, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // Find the timestamp for the most recent record in the database
    // Build a list of new records for the database, omitting any record that is already there
    // Use the list of database records to perform a bulk update on the database
    private void processTimeline(List<Status> timeline) {
        long mostRecentStatus = getMostRecentTimestamp();

        List<ContentValues> update = new ArrayList<ContentValues>();
        for (Status status: timeline) {
            long t = status.getCreatedAt().getTime();
            if (t <= mostRecentStatus) { continue; }

            ContentValues vals = new ContentValues();
            vals.put(YambaContract.Timeline.Columns.TIMESTAMP, t);
            vals.put(YambaContract.Timeline.Columns.ID, status.getId());
            vals.put(YambaContract.Timeline.Columns.USER, status.getUser());
            vals.put(YambaContract.Timeline.Columns.STATUS, status.getMessage());
            update.add(vals);

            Log.d(TAG, "Add status: " + vals);
        }

        if (0 < update.size()) {
            // insert into database
        }
    }

    // find the most recent timestamp in the database
    private long getMostRecentTimestamp() {
        return Long.MIN_VALUE;
    }
}
