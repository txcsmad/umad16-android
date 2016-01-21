package com.utcs.mad.umad.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.utcs.mad.umad.R;
import com.utcs.mad.umad.utils.GeneralUtils;
import com.utcs.mad.umad.vision.BarcodeTrackerFactory;
import com.utcs.mad.umad.vision.CameraSourcePreview;
import com.utcs.mad.umad.vision.GraphicOverlay;
import com.utcs.mad.umad.vision.VisionCallback;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Drew on 1/19/16.
 */
public class VolunteerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private CameraSource mCameraSource;
    private boolean scanning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        setupToolbar();
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(resultCode == ConnectionResult.SUCCESS) {
            setupQrDetector();
        } else if (resultCode == ConnectionResult.SERVICE_MISSING ||
                resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                resultCode == ConnectionResult.SERVICE_DISABLED) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
            dialog.show();
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Scan QR");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraSource != null) {
            mCameraSource.release();
        }
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

    private void startCameraSource() {
        try {
            mPreview.start(mCameraSource, mGraphicOverlay);
            scanning = true;
        } catch (Exception e) {
            Toast.makeText(VolunteerActivity.this, "There was an error with the Camera!", Toast.LENGTH_SHORT).show();
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private void setupQrDetector() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, new VisionCallback() {
            @Override
            public void onFound(final Barcode barcode) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(scanning && barcode.format == Barcode.QR_CODE) {
                            scanning = false;
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(100);
                            showConfirmationDialog(barcode.rawValue);
                        }
                    }
                });
            }
        });
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        mCameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        if(!barcodeDetector.isOperational()) {
            Toast.makeText(VolunteerActivity.this, "There was an error with the Camera!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmationDialog(final String parseObjectId) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("_User");
        query.whereEqualTo("objectId",parseObjectId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject parseObject, ParseException e) {
                if (e == null) {
                    if (parseObject != null) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VolunteerActivity.this);
                        alertBuilder.setTitle("Verify Information");
                        try {
                            String message = "Name: " + parseObject.fetchIfNeeded().getString("name") + "\n" +
                                    "Email: " + parseObject.fetchIfNeeded().getString("email") + "\n";
                            alertBuilder.setMessage(message);
                            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertBuilder.setPositiveButton("Check-in", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    updateApplicationStatus(parseObject);
                                }
                            });
                            alertBuilder.show();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Toast.makeText(VolunteerActivity.this, "No one found with this ID!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    e.printStackTrace();
                    Toast.makeText(VolunteerActivity.this, "There was an issue pulling the info!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateApplicationStatus(ParseObject parseObjectUser) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Application");
        query.include("pointer_field");
        query.whereEqualTo("user", parseObjectUser);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Application_Status");
                    query.include("pointer_field");
                    query.whereEqualTo("application", parseObject);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                Calendar cal = Calendar.getInstance();
                                parseObject.put("arrivedAt", cal.getTime());
                                parseObject.saveEventually();
                                Toast.makeText(VolunteerActivity.this, "Check-in confirmed!", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
