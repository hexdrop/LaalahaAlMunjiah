package com.laallaha.almunjiah;

import android.content.Context;
import android.content.SharedPreferences;

public final class PrayerNotificationSettings {

    private static final String PREFS =
            "PRAYER_NOTIFICATION_SETTINGS";

    private static final String ENABLED =
            "enabled";

    private static final String ADHAN_ENABLED =
            "adhan_enabled";

    private static final String FAJR =
            "fajr";

    private static final String DHUHR =
            "dhuhr";

    private static final String ASR =
            "asr";

    private static final String MAGHRIB =
            "maghrib";

    private static final String ISHA =
            "isha";

    private PrayerNotificationSettings() {
    }

    private static SharedPreferences prefs(
            Context context
    ) {
        return context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    public static boolean isEnabled(
            Context context
    ) {
        return prefs(context).getBoolean(
                ENABLED,
                true
        );
    }

    public static void setEnabled(
            Context context,
            boolean enabled
    ) {
        prefs(context)
                .edit()
                .putBoolean(
                        ENABLED,
                        enabled
                )
                .apply();
    }

    public static boolean isAdhanEnabled(
            Context context
    ) {
        return prefs(context).getBoolean(
                ADHAN_ENABLED,
                true
        );
    }

    public static void setAdhanEnabled(
            Context context,
            boolean enabled
    ) {
        prefs(context)
                .edit()
                .putBoolean(
                        ADHAN_ENABLED,
                        enabled
                )
                .apply();
    }

    public static boolean isPrayerEnabled(
            Context context,
            String prayerName
    ) {
        if (!isEnabled(context)) {
            return false;
        }

        return prefs(context).getBoolean(
                keyForPrayer(prayerName),
                true
        );
    }

    public static void setPrayerEnabled(
            Context context,
            String prayerName,
            boolean enabled
    ) {
        prefs(context)
                .edit()
                .putBoolean(
                        keyForPrayer(prayerName),
                        enabled
                )
                .apply();
    }

    private static String keyForPrayer(
            String prayerName
    ) {

        if ("الفجر".equals(prayerName)) {
            return FAJR;
        }

        if ("الظهر".equals(prayerName)) {
            return DHUHR;
        }

        if ("العصر".equals(prayerName)) {
            return ASR;
        }

        if ("المغرب".equals(prayerName)) {
            return MAGHRIB;
        }

        if ("العشاء".equals(prayerName)) {
            return ISHA;
        }

        return prayerName;
    }
}
