package com.utcs.mad.umad.models;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * CompanyInfo
 * This is a model to hold information for all of the company sponsors/entites that are in uMAD
 */
public class CompanyInfo {

    private static final String TAG = "CompanyInfo";

    private String name;
    private String website;
    private Bitmap image;
    private Bitmap thumbnail;
    private String twitterHandle;
    private int level;

    public CompanyInfo() {
        this.name = "";
        this.website = "";
        this.image = null;
    }

    public CompanyInfo(ParseObject parseObject, String level) throws ParseException {
        setLevel(level);
        this.name = (String) parseObject.fetchIfNeeded().get("name");
        this.website = (String) parseObject.fetchIfNeeded().get("website");
        this.twitterHandle = (String) parseObject.fetchIfNeeded().get("twitterHandle");
        ParseFile thumbnail = (ParseFile) parseObject.get("thumbnail");
        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if(e == null) {
                        createThumbnail(bytes);
                    }
                }
            });
        }
        ParseFile image = (ParseFile) parseObject.get("image");
        image.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if(e == null) {
                    createImage(bytes);
                }
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String companyName) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setCompanyWebsite(String website) {
        this.website = website;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getLevel() { return level; }

    public void setLevel(String level) {
        switch (level) {
            case "gold":
                this.level = 3;
                break;
            case "silver":
                this.level = 2;
                break;
            case "bronze":
                this.level = 1;
                break;
            default:
                this.level = 0;
                break;
        }
    }

    public String toString() {
        return name;
    }

    public void createImage(byte[] data) {
        this.image = Helper.decodeBitmapFromByteArray(data, data.length, data.length);

//        this.image = Bitmap.createScaledBitmap(this.image, 512, 512, true);
    }

    public void createThumbnail(byte[] data) {
        this.thumbnail = Helper.decodeBitmapFromByteArray(data, data.length, data.length);

//        this.thumbnail = Bitmap.createScaledBitmap(this.image, 256, 256, true);
    }
}
