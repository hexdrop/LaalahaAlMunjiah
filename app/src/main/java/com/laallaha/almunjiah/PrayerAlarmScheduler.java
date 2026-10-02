package com.laallaha.almunjiah;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public final class PrayerAlarmScheduler {

    private static final int BASE_REQUEST_CODE = 8100;
    private static final int TOMORROW_FAJR_REQUEST_CODE = 8105;

    private PrayerAlarmScheduler() {
    }

    public static void scheduleNextPrayers(Context context) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        if (alarmManager == null) {
            return;
        }

        // أولًا: نمسح أي جدولة قديمة
        cancelAllPrayers(context, alarmManager);

        // لو التنبيهات كلها مقفولة، لا نعيد الجدولة
        if (!PrayerNotificationSettings.isEnabled(context)) {
            return;
        }

        Calendar now = Calendar.getInstance();

        PrayerTimesCalculator.Times today =
                PrayerTimesCalculator.calculate(
                        now,
                        PrayerLocation.getLatitude(context),
                        PrayerLocation.getLongitude(context)
                );

        String[] names = {
                "الفجر",
                "الظهر",
                "العصر",
                "المغرب",
                "العشاء"
        };

        double[] values = {
                today.fajr,
                today.dhuhr,
                today.asr,
                today.maghrib,
                today.isha
        };

        for (int i = 0; i < values.length; i++) {

            String prayerName = names[i];

            if (!PrayerNotificationSettings.isPrayerEnabled(
                    context,
                    prayerName
            )) {
                continue;
            }

            long prayerMillis =
                    PrayerTimesCalculator.toMillis(
                            values[i],
                            now
                    );

            if (prayerMillis <= now.getTimeInMillis()) {
                continue;
            }

            scheduleAlarm(
                    context,
                    alarmManager,
                    prayerName,
                    prayerMillis,
                    BASE_REQUEST_CODE + i
            );
        }

        scheduleTomorrowFajr(
                context,
                alarmManager,
                now
        );
    }

    private static void scheduleTomorrowFajr(
            Context context,
            AlarmManager alarmManager,
            Calendar now
    ) {

        if (!PrayerNotificationSettings.isPrayerEnabled(
                context,
                "الفجر"
        )) {
            return;
        }

        Calendar tomorrow =
                (Calendar) now.clone();

        tomorrow.add(
                Calendar.DAY_OF_YEAR,
                1
        );

        PrayerTimesCalculator.Times times =
                PrayerTimesCalculator.calculate(
                        tomorrow,
                        PrayerLocation.getLatitude(context),
                        PrayerLocation.getLongitude(context)
                );

        long fajrMillis =
                PrayerTimesCalculator.toMillis(
                        times.fajr,
                        tomorrow
                );

        scheduleAlarm(
                context,
                alarmManager,
                "الفجر",
                fajrMillis,
                TOMORROW_FAJR_REQUEST_CODE
        );
    }

    private static void scheduleAlarm(
            Context context,
            AlarmManager alarmManager,
            String prayerName,
            long triggerAtMillis,
            int requestCode
    ) {

        Intent intent =
                new Intent(
                        context,
                        PrayerNotificationReceiver.class
                );

        intent.putExtra(
                "prayer_name",
                prayerName
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        if (Build.VERSION.SDK_INT >= 31) {

            if (alarmManager.canScheduleExactAlarms()) {

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                );

            } else {

                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                );
            }

        } else if (Build.VERSION.SDK_INT >= 23) {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );

        } else {

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        }
    }

    public static void cancelAllPrayers(
            Context context
    ) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        if (alarmManager != null) {
            cancelAllPrayers(
                    context,
                    alarmManager
            );
        }
    }

    private static void cancelAllPrayers(
            Context context,
            AlarmManager alarmManager
    ) {

        for (int i = 0; i < 5; i++) {

            cancelAlarm(
                    context,
                    alarmManager,
                    BASE_REQUEST_CODE + i
            );
        }

        cancelAlarm(
                context,
                alarmManager,
                TOMORROW_FAJR_REQUEST_CODE
        );
    }

    private static void cancelAlarm(
            Context context,
            AlarmManager alarmManager,
            int requestCode
    ) {

        Intent intent =
                new Intent(
                        context,
                        PrayerNotificationReceiver.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_NO_CREATE
                                | PendingIntent.FLAG_IMMUTABLE
                );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
