package com.utcs.mad.umad.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;
import com.utcs.mad.umad.BuildConfig;
import com.utcs.mad.umad.R;

import io.fabric.sdk.android.Fabric;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwitterFeedFragment extends Fragment {

    StatusesService statusesService;
    SwipeRefreshLayout swipeLayout;
    private ListView twitterListView;

    // Required empty public constructor
    public TwitterFeedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_twitter_feed, container, false);
        setupTwitter((ViewGroup) root);
        setupSwipeRefreshLayout(root);

        return root;
    }

    // Gather the tweets from MAD account and put them into the list adapter
    private void setupTwitter(ViewGroup root) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));
        twitterListView = (ListView) root.findViewById(R.id.timeline);
        getTwitterFeed();
    }

    private void setupSwipeRefreshLayout(View root) {
        swipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeColors(R.color.primary_dark, R.color.primary, R.color.primary_accent);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTwitterFeed();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    private void getTwitterFeed() {
        UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName("utcsmad")
                .build();
        TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(), userTimeline);
        twitterListView.setAdapter(adapter);
    }

    public static TwitterFeedFragment newInstance() {
        TwitterFeedFragment f = new TwitterFeedFragment();
        return f;
    }
}
