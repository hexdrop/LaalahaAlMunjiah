package com.laallaha.almunjiah;

import android.content.Context;
import android.content.SharedPreferences;

public final class PrayerLocation {

    private static final String PREFS = "PRAYER_LOCATION";

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String HAS_LOCATION = "has_location";

    private PrayerLocation() {
    }

    public static void save(
            Context context,
            double latitude,
            double longitude
    ) {
        context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        ).edit()
                .putBoolean(HAS_LOCATION, true)
                .putString(LATITUDE, String.valueOf(latitude))
                .putString(LONGITUDE, String.valueOf(longitude))
                .apply();
    }

    public static boolean hasLocation(Context context) {
        return context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        ).getBoolean(HAS_LOCATION, false);
    }

    public static double getLatitude(Context context) {
        String value = context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        ).getString(LATITUDE, null);

        if (value == null) {
            return 30.0444; // Cairo fallback
        }

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 30.0444;
        }
    }

    public static double getLongitude(Context context) {
        String value = context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        ).getString(LONGITUDE, null);

        if (value == null) {
            return 31.2357; // Cairo fallback
        }

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 31.2357;
        }
    }
}
