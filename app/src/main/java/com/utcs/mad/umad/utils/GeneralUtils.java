package com.utcs.mad.umad.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Drew on 1/17/16.
 */
public class GeneralUtils {
    private static final String DEFAULT_DATE_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String STICKY_DATE_FORMAT_STR = "hh:mm a";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(GeneralUtils.DEFAULT_DATE_FORMAT_STR, Locale.ENGLISH);
    public static final DateFormat STICKY_DATE_FORMAT = new SimpleDateFormat(GeneralUtils.STICKY_DATE_FORMAT_STR, Locale.ENGLISH);

    // Get it if asked, if there is no cache, or if cache is old
    public static boolean isCacheValid(boolean forceGet, Calendar cacheDate) {
        Calendar oneDayAgoCal = Calendar.getInstance(Locale.ENGLISH);
        oneDayAgoCal.add(Calendar.DATE, -1);
        return forceGet || cacheDate == null || cacheDate.before(oneDayAgoCal);
    }
}
