package com.mad.umad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mad.umad.models.CompanyInfo;
import com.mad.umad.models.EventInfo;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Drew on 1/17/16.
 */
public class UserPrefStorage {

    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /* USER CACHE */

    public static String getParseUserId(Context context) {
        return getPrefs(context).getString("parse-user-id", null);
    }

    public static void setParseUserId(Context context, String parseUserId) {
        getPrefs(context).edit().putString("parse-user-id", parseUserId).commit();
    }

    /* SCHEDULE CACHE */

    public static Calendar getScheduleCacheDate(Context context) {
        String dateStr = getPrefs(context).getString("schedule-cache", "");
        if(dateStr.equals("")) return null;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(GeneralUtils.DATE_FORMAT.parse(dateStr));
            return cal;
        } catch (ParseException e) {
            return null;
        }
    }

    private static void setScheduleCacheDate(Context context, Calendar cal) {
        String dateStr = GeneralUtils.DATE_FORMAT.format(cal.getTime());
        getPrefs(context).edit().putString("schedule-cache", dateStr).commit();
    }

    public static ArrayList<EventInfo> getScheduleCache(Context context) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventInfo>>() {}.getType();
        String json = getPrefs(context).getString("schedule", "");
        return gson.fromJson(json, type);
    }

    public static void setScheduleCache(Context context, List<EventInfo> scheduleList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventInfo>>() {}.getType();
        String json = gson.toJson(scheduleList, type);
        getPrefs(context).edit().putString("schedule", json).commit();
        setScheduleCacheDate(context, Calendar.getInstance());
    }

    /* COMPANY CACHE */

    public static Calendar getCompanyCacheDate(Context context) {
        String dateStr = getPrefs(context).getString("company-cache", "");
        if(dateStr.equals("")) return null;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(GeneralUtils.DATE_FORMAT.parse(dateStr));
            return cal;
        } catch (ParseException e) {
            return null;
        }
    }

    private static void setCompanyCacheDate(Context context, Calendar cal) {
        String dateStr = GeneralUtils.DATE_FORMAT.format(cal.getTime());
        getPrefs(context).edit().putString("company-cache", dateStr).commit();
    }

    public static ArrayList<CompanyInfo> getCompanyCache(Context context) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<CompanyInfo>>() {}.getType();
        String json = getPrefs(context).getString("company", "");
        return gson.fromJson(json, type);
    }

    public static void setCompanyCache(Context context, List<CompanyInfo> companyList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<CompanyInfo>>() {}.getType();
        String json = gson.toJson(companyList, type);
        getPrefs(context).edit().putString("company", json).commit();
        setCompanyCacheDate(context, Calendar.getInstance());
    }


}
