package com.utcs.mad.umad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.utcs.mad.umad.models.CompanyInfo;
import com.utcs.mad.umad.R;
import com.utcs.mad.umad.utils.GeneralUtils;
import com.utcs.mad.umad.utils.UserPrefStorage;
import com.utcs.mad.umad.views.SpacesItemDecoration;
import com.utcs.mad.umad.views.adapters.ScheduleAdapter;
import com.utcs.mad.umad.views.adapters.SponsorsRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SponsorsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    static final String TAG = "SponsorsFragment";
    private ArrayList<CompanyInfo> sponsors;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // Required empty public constructor
    public SponsorsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sponsors, container, false);
        sponsors = new ArrayList<>();

        setupRecyclerView(rootView);
        getSponsorData(false);

        return rootView;
    }

    private void setupRecyclerView(ViewGroup root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.sponsors_recyclerview);
        recyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        SponsorsRecyclerView adapter = new SponsorsRecyclerView(sponsors, getActivity());
        recyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (recyclerView.getAdapter().getItemViewType(position)) {
                    case 3:
                        return 2;
                    case 2:
                    case 1:
                        return 1;
                    default:
                        return -1;  //shouldn't occur
                }
            }
        });
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(25));

        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this);
    }

    private void getSponsorData(boolean forceGet) {
        Calendar cacheDate = UserPrefStorage.getCompanyCacheDate(getContext());
        if(GeneralUtils.isCacheValid(forceGet, cacheDate)) {
            Log.i(TAG, "getSponsorData: Network");
            getSponsorParseData();
        } else {
            Log.i(TAG, "getSponsorData: Cache");
            sponsors.clear();
            sponsors.addAll(UserPrefStorage.getCompanyCache(getContext()));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void getSponsorParseData() {
        refreshLayout.setRefreshing(true);
        sponsors.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Sponsor");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if( e == null) {
                    addParseSponsorsToList(parseObjects);
                    for (CompanyInfo companyInfo : sponsors) {
                        Log.i(TAG, "done: " + companyInfo.getName() + " " + companyInfo.getLevel());
                    }
                    UserPrefStorage.setCompanyCache(getContext(), sponsors);
                    Log.i(TAG, "done: SPONSORS");
                } else {
                    Log.e(TAG, "exception parse");
                }
                refreshLayout.setRefreshing(false);
            }
        });

        updateViewData();
    }

    private void addParseSponsorsToList(List<ParseObject> parseObjects) {
        for (ParseObject sponsor : parseObjects) {
            ParseObject umad = sponsor.getParseObject("umad");

            try {
                if (umad.fetchIfNeeded().getInt("year") == 2016) {
                    ParseObject curCompanyInfo = sponsor.getParseObject("company");
                    String level = (String) sponsor.get("level");
                    CompanyInfo curSponsor = new CompanyInfo(curCompanyInfo, level);
                    sponsors.add(curSponsor);
                    
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "addParseSponsorsToList: ADDING CACHE FOR SPONSORS");
    }

    private void updateViewData() {
        Log.i(TAG, "updateViewData: YOYOY");
//        Collections.sort(sponsors, new Comparator<CompanyInfo>() {
//            @Override
//            public int compare(CompanyInfo  company1, CompanyInfo  company2)
//            {
//                if(company1.getLevel() < company2.getLevel()) return 1;
//                else if (company1.getLevel() > company2.getLevel()) return -1;
//                else return 0;
//            }
//        });
        Collections.sort(sponsors);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public static SponsorsFragment newInstance() {
        SponsorsFragment f = new SponsorsFragment();
        return f;
    }


    @Override
    public void onRefresh() {
        getSponsorData(true);
    }
}
