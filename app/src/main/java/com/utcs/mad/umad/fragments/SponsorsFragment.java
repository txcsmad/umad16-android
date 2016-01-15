package com.utcs.mad.umad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.utcs.mad.umad.views.SpacesItemDecoration;
import com.utcs.mad.umad.views.adapters.SponsorsRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SponsorsFragment extends Fragment {

    static final String TAG = "SponsorsFragment";
    private ArrayList<CompanyInfo> sponsors;

    private RecyclerView recyclerView;

    // Required empty public constructor
    public SponsorsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sponsors, container, false);
        sponsors = new ArrayList<>();

        setupRecyclerView(rootView);
        getSponsorParseData();

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

    }

    private void getSponsorParseData() {
        sponsors.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Sponsor");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if( e == null) {
                    addParseSponsorsToList(parseObjects);
                } else {
                    Log.e(TAG, "exception parse");
                }
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
    }

    private void updateViewData() {
        Collections.sort(sponsors, new Comparator<CompanyInfo>() {
            @Override
            public int compare(CompanyInfo  company1, CompanyInfo  company2)
            {
                if(company1.getLevel() < company2.getLevel()) return 1;
                else if (company1.getLevel() > company2.getLevel()) return -1;
                else return 0;
            }
        });
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public static SponsorsFragment newInstance() {
        SponsorsFragment f = new SponsorsFragment();
        return f;
    }


}
