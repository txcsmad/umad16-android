package com.mad.umad.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.mad.umad.R;
import com.mad.umad.utils.GeneralUtils;
import com.mad.umad.views.tab.SlidingTabsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity
 * This is the first screen the users see, this hosts the 3 main fragments of the app.
 * Schedule - To see the events in a time sorted manner
 * Twitter - See twitter posts from MAD during the uMAD conference
 * Sponsors - See the lovely companies that sponsored and made uMAD happen
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    public static int screenWidth;
    public static int screenHeight;
    public static int screenDensity;
    public Toolbar toolbar;
    private boolean isVolunteer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isVolunteer = false;
        checkIfVolunteer();

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
        if (isVolunteer) {
            getMenuInflater().inflate(R.menu.menu_main_volunteer, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
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
            case R.id.scan_qr:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            GeneralUtils.CAMERA_PERMISSION_RESULT);
                } else {
                    startActivity(new Intent(this, VolunteerActivity.class));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GeneralUtils.CAMERA_PERMISSION_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startActivity(new Intent(this, VolunteerActivity.class));
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "This feature doesn't work without the camera permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void checkIfVolunteer() {
        ParseQuery<ParseRole> query1 = ParseRole.getQuery();
        query1.whereEqualTo("name", "UMAD Volunteer");
        query1.whereEqualTo("users", ParseUser.getCurrentUser());

        ParseQuery<ParseRole> query2 = ParseRole.getQuery();
        query2.whereEqualTo("name", "Administrator");
        query2.whereEqualTo("users", ParseUser.getCurrentUser());

        ArrayList<ParseQuery<ParseRole>> queryList = new ArrayList<>();
        queryList.add(query1);
        queryList.add(query2);

        ParseQuery<ParseRole> query = ParseQuery.or(queryList);
        query.findInBackground(new FindCallback<ParseRole>() {
            @Override
            public void done(List<ParseRole> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        updateMenuForVolunteer();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateMenuForVolunteer() {
        Log.i(TAG, "updateMenuForVolunteer: ");
        isVolunteer = true;
        invalidateOptionsMenu();
    }

}
