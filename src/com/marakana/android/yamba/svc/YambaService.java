package com.marakana.android.yamba.svc;

import java.util.List;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    /** Max results in one poll */
    public static final int MAX_MESSAGES = 30;

    private static final String PARAM_OP = "YambaService.OP";
    private static final int OP_POST = 6001;
    private static final int OP_POLL = 6002;

    private static final String PARAM_STATUS = "YambaService.STATUS";

    public static void post(Context ctxt, String status) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POST);
        i.putExtra(PARAM_STATUS, status);
        ctxt.startService(i);
    }

    public static void poll(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLL);
        ctxt.startService(i);
    }


    private YambaClient yamba;

    public YambaService() { super(TAG); }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate thread: " + Thread.currentThread());

        yamba = new YambaClient("student", "password", "http://yamba.marakana.com/api");
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

        default:
            throw new IllegalStateException("Unknown op: " + op);
        }
    }

    private void doPoll() {
        List<Status> timeline = null;
        try { timeline = yamba.getTimeline(MAX_MESSAGES); }
        catch (YambaClientException e) {
            Log.w(TAG, "post failed: ", e);
        }

        for (Status status: timeline) {
            Log.d(TAG,
                    "Status: " + status.getId()
                    + ", " + status.getCreatedAt()
                    + ", " + status.getUser()
                    + ", " + status.getMessage());
        }
    }

    private void doPost(String message) {
        if (TextUtils.isEmpty(message)) { return; }

        try { yamba.postStatus(message); }
        catch (YambaClientException e) {
            Log.w(TAG, "post failed: ", e);
        }
    }
}
