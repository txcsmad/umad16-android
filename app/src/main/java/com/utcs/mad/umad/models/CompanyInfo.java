package com.utcs.mad.umad.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * CompanyInfo
 * This is a model to hold information for all of the company sponsors/entites that are in uMAD
 */
public class CompanyInfo implements Parcelable {

    private static final String TAG = "CompanyInfo";

    private String name;
    private String website;
    private String twitterHandle;
    private int level;
    private Bitmap image;
    private Bitmap thumbnail;

    public CompanyInfo() {
        this.name = "";
        this.website = "";
        this.twitterHandle = "";
        this.image = null;
        this.thumbnail = null;
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
        this.image = Helper.byteArrayToBitmap(data, data.length, data.length);
    }

    public void createThumbnail(byte[] data) {
        this.thumbnail = Helper.byteArrayToBitmap(data, data.length, data.length);
    }

    /*
     * PARCELABLE
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(website);
        dest.writeString(twitterHandle);
        dest.writeInt(level);

        byte [] imageBytes = Helper.bitmapToByteArray(image);
        dest.writeInt(imageBytes.length);
        dest.writeByteArray(imageBytes);

        byte [] thumbnailBytes = Helper.bitmapToByteArray(thumbnail);
        dest.writeInt(thumbnailBytes.length);
        dest.writeByteArray(thumbnailBytes);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        website = in.readString();
        twitterHandle = in.readString();
        level = in.readInt();

        int imageLength = in.readInt();
        byte[] imageBytes = new byte[imageLength];
        in.readByteArray(imageBytes);
        image = Helper.byteArrayToBitmap(imageBytes, imageBytes.length, imageBytes.length);

        int thumbnailLength = in.readInt();
        byte[] thumbnailBytes = new byte[thumbnailLength];
        in.readByteArray(thumbnailBytes);
        image = Helper.byteArrayToBitmap(thumbnailBytes, thumbnailBytes.length, thumbnailBytes.length);
    }

    private CompanyInfo(Parcel in) {
        readFromParcel(in);
    }

    public static Parcelable.Creator<CompanyInfo> CREATOR = new Parcelable.Creator<CompanyInfo>() {
        public CompanyInfo createFromParcel(Parcel source) {
            return new CompanyInfo(source);
        }

        public CompanyInfo[] newArray(int size) {
            return new CompanyInfo[size];
        }
    };
}
