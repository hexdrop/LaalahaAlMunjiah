package com.laallaha.almunjiah;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public class PrayerNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "prayer_times";

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        String prayerName =
                intent.getStringExtra("prayer_name");

        if (prayerName == null || prayerName.isEmpty()) {
            prayerName = "الصلاة";
        }

        /*
         * أولًا: جدولة الصلوات القادمة.
         */
        PrayerAlarmScheduler.scheduleNextPrayers(context);

        /*
         * التأكد من أن الصلاة نفسها مفعّلة.
         *
         * هذا يحمي النظام أيضًا من أي Alarm قديم
         * تم إنشاؤه قبل تغيير الإعدادات.
         */
        if (!PrayerNotificationSettings.isPrayerEnabled(
                context,
                prayerName
        )) {
            return;
        }

        /*
         * تشغيل الأذان.
         *
         * الفجر مؤجل حاليًا لأن ملف الأذان الموجود
         * لا يحتوي على "الصلاة خير من النوم".
         */
        if (
                !"الفجر".equals(prayerName)
                && PrayerNotificationSettings.isAdhanEnabled(context)
        ) {

            try {

                Intent adhanIntent =
                        new Intent(
                                context,
                                AdhanPlaybackService.class
                        );

                adhanIntent.putExtra(
                        "prayer_name",
                        prayerName
                );

                if (Build.VERSION.SDK_INT >= 26) {

                    context.startForegroundService(
                            adhanIntent
                    );

                } else {

                    context.startService(
                            adhanIntent
                    );
                }

            } catch (Exception ignored) {
                // لا نوقف إشعار الصلاة إذا فشل تشغيل الأذان.
            }
        }

        /*
         * إنشاء قناة الإشعارات.
         */
        createChannel(context);

        /*
         * فتح التطبيق عند الضغط على الإشعار.
         */
        Intent openApp =
                new Intent(
                        context,
                        MainActivity.class
                );

        openApp.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        prayerName.hashCode(),
                        openApp,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        /*
         * Android 13+
         * يحتاج إذن POST_NOTIFICATIONS.
         */
        if (Build.VERSION.SDK_INT >= 33) {

            if (
                    context.checkSelfPermission(
                            android.Manifest.permission.POST_NOTIFICATIONS
                    )
                            != PackageManager.PERMISSION_GRANTED
            ) {
                return;
            }
        }

        Notification notification;

        if (Build.VERSION.SDK_INT >= 26) {

            Notification.Builder builder =
                    new Notification.Builder(
                            context,
                            CHANNEL_ID
                    );

            builder.setSmallIcon(
                    R.drawable.ic_quran
            );

            builder.setContentTitle(
                    "حان الآن وقت صلاة " + prayerName
            );

            builder.setContentText(
                    "تقبل الله منا ومنكم • لعلها المنجيه"
            );

            builder.setStyle(
                    new Notification.BigTextStyle()
                            .bigText(
                                    "حان الآن وقت صلاة "
                                            + prayerName
                                            + "\n\n"
                                            + "تقبل الله منا ومنكم\n"
                                            + "لعلها المنجيه"
                            )
            );

            builder.setAutoCancel(true);

            builder.setContentIntent(
                    pendingIntent
            );

            notification =
                    builder.build();

        } else {

            Notification.Builder builder =
                    new Notification.Builder(
                            context
                    );

            builder.setSmallIcon(
                    R.drawable.ic_quran
            );

            builder.setContentTitle(
                    "حان الآن وقت صلاة " + prayerName
            );

            builder.setContentText(
                    "تقبل الله منا ومنكم • لعلها المنجيه"
            );

            builder.setAutoCancel(true);

            builder.setContentIntent(
                    pendingIntent
            );

            notification =
                    builder.build();
        }

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.notify(
                    prayerName.hashCode(),
                    notification
            );
        }
    }

    private void createChannel(Context context) {

        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager == null) {
            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "تنبيهات الصلاة",
                        NotificationManager.IMPORTANCE_HIGH
                );

        channel.setDescription(
                "تنبيهات مواقيت الصلاة في لعلها المنجيه"
        );

        manager.createNotificationChannel(
                channel
        );
    }
}
