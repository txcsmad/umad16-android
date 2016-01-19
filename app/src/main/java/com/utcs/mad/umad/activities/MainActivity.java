package com.utcs.mad.umad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.core.TwitterSession;
import com.utcs.mad.umad.models.CompanyInfo;
import com.utcs.mad.umad.models.EventInfo;
import com.utcs.mad.umad.R;
import com.utcs.mad.umad.utils.GeneralUtils;
import com.utcs.mad.umad.views.tab.SlidingTabsFragment;

import java.util.ArrayList;

/**
 * Main Activity
 * This is the first screen the users see, this hosts the 3 main fragments of the app.
 * Schedule - To see the events in a time sorted manner
 * Twitter - See twitter posts from MAD during the uMAD conference
 * Sponsors - See the lovely companies that sponsored and made uMAD happen
 */
public class MainActivity extends ActionBarActivity{

    public static int screenWidth;
    public static int screenHeight;
    public static int screenDensity;
    public Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();

        // Gather information here for static knowledge of screen dimensions to prevent issues
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenDensity = getResources().getDisplayMetrics().densityDpi;

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsFragment fragment = new SlidingTabsFragment();
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" " + getString(R.string.app_name));
        toolbar.setLogo(R.drawable.ic_launcher_white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.logout:
                GeneralUtils.logout(this);
                return true;
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
