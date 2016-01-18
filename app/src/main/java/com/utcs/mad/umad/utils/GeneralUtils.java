package com.utcs.mad.umad.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Drew on 1/17/16.
 */
public class GeneralUtils {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(GeneralUtils.DEFAULT_DATE_FORMAT, Locale.ENGLISH);

    // Get it if asked, if there is no cache, or if cache is old
    public static boolean isCacheValid(boolean forceGet, Calendar cacheDate) {
        Calendar oneDayAgoCal = Calendar.getInstance(Locale.ENGLISH);
        oneDayAgoCal.add(Calendar.DATE, -1);
        return forceGet || cacheDate == null || cacheDate.before(oneDayAgoCal);
    }
}
