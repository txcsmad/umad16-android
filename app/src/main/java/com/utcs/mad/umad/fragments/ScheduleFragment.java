package com.utcs.mad.umad.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.utcs.mad.umad.models.CompanyInfo;
import com.utcs.mad.umad.models.EventInfo;
import com.utcs.mad.umad.R;
import com.utcs.mad.umad.views.adapters.ScheduleAdapter;
import com.utcs.mad.umad.activities.EventActivity;
import com.utcs.mad.umad.activities.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;



/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    static final String TAG = "ScheduleFragment";

    // Required empty public constructor
    public ScheduleFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_schedule, container, false);
        final String[] times = getActivity().getResources().getStringArray(R.array.times);
        final StickyListHeadersListView stickyListView = (StickyListHeadersListView) v.findViewById(R.id.schedule_list);

        stickyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplication(), EventActivity.class);
                intent.putExtra("id", position);
                getActivity().startActivity(intent);
            }
        });

        // Get data from parse if cache is empty
        if(MainActivity.eventInfoListCache == null || MainActivity.companiesCache == null) {
            getEventDataFromParse(times, stickyListView);
        } else {
            ScheduleAdapter scheduleAdapter =
                    new ScheduleAdapter(getActivity().getApplicationContext(), times, MainActivity.companiesCache);
            stickyListView.setAdapter(scheduleAdapter);
        }
        return v;
    }

    private void getEventDataFromParse(final String[] times, final StickyListHeadersListView stickyListView) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Session");

        query.orderByAscending("regTime").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null) {
                    MainActivity.eventInfoListCache = new ArrayList<EventInfo>();
                    for (ParseObject event : parseObjects) {
                        storeEventInfo(event);
                        storeCompanyInfo(event);
                    }

                    ScheduleAdapter scheduleAdapter = new ScheduleAdapter(getActivity().getApplicationContext(), times, MainActivity.companiesCache);
                    stickyListView.setAdapter(scheduleAdapter);
                } else {
                    Log.e(TAG, "Parse Exception: " + e);
                }
            }
        });
    }

    private void storeEventInfo(ParseObject event) {
        EventInfo item = new EventInfo();
        try {
            ParseObject company = event.fetchIfNeeded().getParseObject("company");
            item.setCompanyName((String) company.get("name"));
            item.setCompanyWebsite((String) company.get("website"));
            item.setTwitterHandle((String) company.get("twitterHandle"));
            item.setStartingTime((Date) event.get("startTime"));
            item.setEndingTime((Date) event.get("endTime"));
            item.setRoom((String) event.get("room"));
            item.setSessionName((String) event.get("sessionName"));
            item.setSpeaker((String) event.get("speaker"));
            item.setDescription((String) event.get("description"));
            item.setRegTime((int) event.get("regTime"));
            MainActivity.eventInfoListCache.add(item);
        } catch (Exception e) {

        }
    }

    private void storeCompanyInfo(ParseObject event) {
        CompanyInfo company = new CompanyInfo();
        try {
            ParseObject companyParse = event.fetchIfNeeded().getParseObject("company");
            company.setName((String) companyParse.get("name"));
            ParseFile parseFile = companyParse.getParseFile("thumbnail");
            byte[] data = null;
            try {
                data = parseFile.getData();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
//            company.setData(data);
            MainActivity.companiesCache.add(company);
        } catch (Exception e) {

        }
    }

    public static ScheduleFragment newInstance() {
        ScheduleFragment f = new ScheduleFragment();
        return f;
    }
}
