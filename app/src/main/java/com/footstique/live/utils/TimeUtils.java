package com.footstique.live.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.TimeZone;

public class TimeUtils {
    private static final String PREFS_NAME = "fs_prefs";
    private static final String KEY_TZ_ID = "tz_id"; // e.g., "Asia/Riyadh"; if absent, use device default

    public static TimeZone getPreferredTimeZone(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String id = prefs.getString(KEY_TZ_ID, null);
            if (id == null || id.equalsIgnoreCase("device")) {
                return TimeZone.getDefault();
            }
            TimeZone tz = TimeZone.getTimeZone(id);
            return tz != null ? tz : TimeZone.getDefault();
        } catch (Exception e) {
            return TimeZone.getDefault();
        }
    }

    public static void setPreferredTimeZone(Context context, String timeZoneId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TZ_ID, timeZoneId != null ? timeZoneId : "device").apply();
    }
}
