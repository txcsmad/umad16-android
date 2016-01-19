package com.utcs.mad.umad.utils;

import android.content.Context;
import android.content.Intent;

import com.parse.ParseUser;
import com.utcs.mad.umad.activities.LoginActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Drew on 1/17/16.
 */
public class GeneralUtils {
    private static final String DEFAULT_DATE_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String STICKY_DATE_FORMAT_STR = "h:mm a";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(GeneralUtils.DEFAULT_DATE_FORMAT_STR, Locale.ENGLISH);
    public static final DateFormat STICKY_DATE_FORMAT = new SimpleDateFormat(GeneralUtils.STICKY_DATE_FORMAT_STR, Locale.ENGLISH);

    // Get it if asked, if there is no cache, or if cache is old
    public static boolean isCacheValid(boolean forceGet, Calendar cacheDate) {
        Calendar oneDayAgoCal = Calendar.getInstance(Locale.ENGLISH);
        oneDayAgoCal.add(Calendar.DATE, -1);
        return forceGet || cacheDate == null || cacheDate.before(oneDayAgoCal);
    }

    public static void logout(Context context) {
        UserPrefStorage.setParseUserId(context, null);
        new File(context.getFilesDir().getPath(), "qr.png").delete();
        ParseUser.logOut();
        Intent newIntent = new Intent(context, LoginActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(newIntent);
    }
}
