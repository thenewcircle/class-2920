package com.marakana.android.yamba;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimelineDetailFragment extends Fragment {
    public static final String PARAM_DETAILS = "TimelineDetailFragment.DETAILS";

    public static TimelineDetailFragment newInstance(String details) {
        TimelineDetailFragment frag = new TimelineDetailFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_DETAILS, details);
        frag.setArguments(args);
        return frag;
    }


    private TextView detailView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View v = inflater.inflate(R.layout.fragment_timeline_detail, container, false);

        detailView = (TextView) v.findViewById(R.id.timeline_detail_fragment);

        if (null == state) { state = getArguments(); }

        String details;
        if (null == state) { details = "nothing yet"; }
        else { details = state.getString(PARAM_DETAILS); }
        detailView.setText(details);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_DETAILS, detailView.getText().toString());
    }
 }
