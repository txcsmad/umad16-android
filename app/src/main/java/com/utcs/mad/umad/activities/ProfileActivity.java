package com.utcs.mad.umad.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.utcs.mad.umad.R;
import com.utcs.mad.umad.models.Helper;
import com.utcs.mad.umad.utils.GeneralUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Drew on 1/18/16.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private Toolbar toolbar;
    private TextView profileName;
    private TextView profileEmail;
    private TextView uMadStatus;

    private String status;
    private boolean alreadyLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        alreadyLoaded = false;

        increaseBrightness();
        setupToolbar();
        setupParseInfo();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.logout:
                GeneralUtils.logout(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupParseInfo() {
        profileName = (TextView) findViewById(R.id.profile_name);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        uMadStatus = (TextView) findViewById(R.id.umad_status);

        profileEmail.setText(ParseUser.getCurrentUser().getEmail());
        try {
            profileName.setText(ParseUser.getCurrentUser().fetchIfNeeded().getString("name"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        updateApplicationStatus();
    }

    private void updateApplicationStatus() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UMAD_Application");
        query.include("pointer_field");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
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
                                status = "";
                                try {
                                    status = parseObject.fetchIfNeeded().getString("status");
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                updateApplicationStatusViews();
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

    private void updateApplicationStatusViews() {
        uMadStatus.setText(status);
        switch (status) {
            case "Pending":
                uMadStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                findViewById(R.id.codeCard).setVisibility(View.GONE);
                break;
            case "Waitlisted":
                uMadStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                findViewById(R.id.codeCard).setVisibility(View.GONE);
                break;
            case "Accepted":
                uMadStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                findViewById(R.id.codeCard).setVisibility(View.GONE);
                break;
            case "Confirmed":
                uMadStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                if (!alreadyLoaded) {
                    alreadyLoaded = true;
                    loadQrCode(ParseUser.getCurrentUser().getObjectId());
                }
                break;
        }
    }

    private void loadQrCode(final String qrSecret) {
        new Thread(new Runnable() {
            public void run() {
                QRCodeWriter writer = new QRCodeWriter();
                final Bitmap code;
                try {
                    if(!isCodeSaved()) {
                        BitMatrix bitMatrix = writer.encode(qrSecret, BarcodeFormat.QR_CODE, 512, 512);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        code = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                code.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : getResources().getColor(R.color.cardview_light_background));
                            }
                        }

                        saveCodeToInternalStorage(code);
                    } else {
                        code = loadCodeFromInternalStorage();
                    }

                    ProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) findViewById(R.id.codeCardCode)).setImageBitmap(code);
                            findViewById(R.id.codeCard).setVisibility(View.VISIBLE);
                            findViewById(R.id.codeCardCode).setVisibility(View.VISIBLE);
                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean saveCodeToInternalStorage(Bitmap image) {
        try {
            FileOutputStream fos = this.openFileOutput("qr.png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private Bitmap loadCodeFromInternalStorage() {
        try {
            File f = new File(getFilesDir().getPath(), "qr.png");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean isCodeSaved() {
        return new File(getFilesDir().getPath(), "qr.png").exists();
    }

    private void increaseBrightness() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1;
        getWindow().setAttributes(layoutParams);
    }
}
