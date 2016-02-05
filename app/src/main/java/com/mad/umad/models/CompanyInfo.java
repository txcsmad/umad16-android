package com.mad.umad.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * CompanyInfo
 * This is a model to hold information for all of the company sponsors/entites that are in uMAD
 */
public class CompanyInfo implements Parcelable, Comparable<CompanyInfo> {

    private static final String TAG = "CompanyInfo";

    private String name;
    private String website;
    private String twitterHandle;
    private int level;

    public CompanyInfo() {
        this.name = "";
        this.website = "";
        this.twitterHandle = "";
    }

    public CompanyInfo(ParseObject parseObject, String level) throws ParseException {
        setLevel(level);
        this.name = (String) parseObject.fetchIfNeeded().get("name");
        this.website = (String) parseObject.fetchIfNeeded().get("website");
        this.twitterHandle = (String) parseObject.fetchIfNeeded().get("twitterHandle");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        website = in.readString();
        twitterHandle = in.readString();
        level = in.readInt();
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

    /*
     * COMPARATOR
     */
    @Override
    public int compareTo(CompanyInfo another) {
        Log.i(TAG, "compareTo: " + getName() + " " + getLevel() + " --- " + another.getName() + " " + another.getLevel());
        return another.getLevel() - getLevel();
    }
}
