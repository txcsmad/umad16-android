package com.utcs.mad.umad.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.utcs.mad.umad.models.CompanyInfo;
import com.utcs.mad.umad.models.EventInfo;
import com.utcs.mad.umad.R;
import com.utcs.mad.umad.utils.GeneralUtils;
import com.utcs.mad.umad.utils.UserPrefStorage;
import com.utcs.mad.umad.views.adapters.ScheduleAdapter;
import com.utcs.mad.umad.activities.EventActivity;
import com.utcs.mad.umad.activities.MainActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ScheduleFragment";
    private SwipeRefreshLayout refreshLayout;
    private StickyListHeadersListView stickyListView;
    private ArrayList<EventInfo> events;
    private String[] times = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_schedule, container, false);
        times = getActivity().getResources().getStringArray(R.array.times);
        events = new ArrayList<>();

        setupStickyList(root);
        getEventData(false);
        return root;
    }

    private void setupStickyList(ViewGroup root) {
        stickyListView = (StickyListHeadersListView) root.findViewById(R.id.schedule_list);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);

        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(getActivity().getApplicationContext(), times, events);
        stickyListView.setAdapter(scheduleAdapter);

        // The fix to get the SwipeRefreshLayout to work properly with a sectioned header
        stickyListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // The magic lays here. Get the listwrappers top point.
                View childView = stickyListView.getWrappedList().getChildAt(0);
                int top = (childView == null) ? 0 : childView.getTop();

                // If at the absolute top then enable the SwipeRefreshLayout
                if (top >= 0) {
                    refreshLayout.setEnabled(true);
                    Log.i(TAG, "onScroll: true");
                } else {
                    refreshLayout.setEnabled(false);
                    Log.i(TAG, "onScroll: false");
                }
            }
        });

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setRefreshing(true);

        stickyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplication(), EventActivity.class);
                intent.putExtra("event", events.get(position));
                getActivity().startActivity(intent);
            }
        });
    }

    private void getEventData(boolean forceGet) {
        Calendar cacheDate = UserPrefStorage.getScheduleCacheDate(getContext());
        if(GeneralUtils.isCacheValid(forceGet, cacheDate)) {
            Log.i(TAG, "getEventData: Network");
            getEventDataFromParse();
        } else {
            Log.i(TAG, "getEventData: Cache");
            events = UserPrefStorage.getScheduleCache(getContext());
            ScheduleAdapter scheduleAdapter = new ScheduleAdapter(getActivity().getApplicationContext(), times, events);
            stickyListView.setAdapter(scheduleAdapter);
        }
    }

    private void getEventDataFromParse() {
        refreshLayout.setRefreshing(true);
        events.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Session");
        query.orderByAscending("startTime").findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null) {
//                    Log.i(TAG, "done: length: " + parseObjects.size());
                    for (ParseObject event : parseObjects) {
                        try {
                            ParseObject umad = event.fetchIfNeeded().getParseObject("umad");
                            if (umad != null && umad.fetchIfNeeded().getInt("year") == 2016) {
                                events.add(new EventInfo(event));
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

//                    Log.i(TAG, "getEventDataFromParse: " + events.size());
                    UserPrefStorage.setScheduleCache(getContext(), events);
                    ScheduleAdapter scheduleAdapter = new ScheduleAdapter(getActivity().getApplicationContext(), times, events);
                    stickyListView.setAdapter(scheduleAdapter);
                } else {
                    Log.e(TAG, "Parse Exception: " + e);
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public static ScheduleFragment newInstance() {
        ScheduleFragment f = new ScheduleFragment();
        return f;
    }

    @Override
    public void onRefresh() {
        getEventData(true);
    }
}
