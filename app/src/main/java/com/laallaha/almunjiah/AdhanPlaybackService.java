package com.laallaha.almunjiah;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

public class AdhanPlaybackService extends Service {

    private static final String CHANNEL_ID = "adhan_playback";
    private static final int NOTIFICATION_ID = 9101;

    private static final String ACTION_STOP_ADHAN =
            "com.laallaha.almunjiah.STOP_ADHAN";

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        startForeground(
                NOTIFICATION_ID,
                buildNotification()
        );
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        if (
                intent != null
                        && ACTION_STOP_ADHAN.equals(
                                intent.getAction()
                        )
        ) {

            stopAdhan();

            return START_NOT_STICKY;
        }

        String prayerName =
                intent != null
                        ? intent.getStringExtra("prayer_name")
                        : "الصلاة";

        startAdhan(prayerName);

        return START_NOT_STICKY;
    }

    private void startAdhan(String prayerName) {

        stopCurrentPlayer();

        try {

            mediaPlayer =
                    MediaPlayer.create(
                            this,
                            R.raw.adhan
                    );

            if (mediaPlayer == null) {
                stopSelf();
                return;
            }

            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(
                                    AudioAttributes.USAGE_ALARM
                            )
                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_MUSIC
                            )
                            .build()
            );

            mediaPlayer.setOnCompletionListener(
                    mp -> {

                        stopCurrentPlayer();

                        removeNotification();

                        stopSelf();
                    }
            );

            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        stopCurrentPlayer();

                        removeNotification();

                        stopSelf();

                        return true;
                    }
            );

            mediaPlayer.start();

            /*
             * نعيد بناء الإشعار بعد معرفة اسم الصلاة.
             */
            updateNotification(prayerName);

        } catch (Exception e) {

            stopCurrentPlayer();

            removeNotification();

            stopSelf();
        }
    }

    private Notification buildNotification() {

        /*
         * زر إيقاف الأذان.
         */
        Intent stopIntent =
                new Intent(
                        this,
                        AdhanPlaybackService.class
                );

        stopIntent.setAction(
                ACTION_STOP_ADHAN
        );

        PendingIntent stopPendingIntent =
                PendingIntent.getService(
                        this,
                        9103,
                        stopIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        /*
         * فتح التطبيق.
         */
        Intent openIntent =
                new Intent(
                        this,
                        MainActivity.class
                );

        openIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent openPendingIntent =
                PendingIntent.getActivity(
                        this,
                        9102,
                        openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {

            builder =
                    new Notification.Builder(
                            this,
                            CHANNEL_ID
                    );

        } else {

            builder =
                    new Notification.Builder(this);
        }

        builder.setSmallIcon(
                R.drawable.ic_quran
        );

        builder.setContentTitle(
                "🔊 الأذان"
        );

        builder.setContentText(
                "حان الآن وقت الصلاة"
        );

        builder.setContentIntent(
                openPendingIntent
        );

        builder.setOngoing(true);

        builder.setCategory(
                Notification.CATEGORY_ALARM
        );

        builder.setPriority(
                Notification.PRIORITY_HIGH
        );

        builder.addAction(
                new Notification.Action.Builder(
                        null,
                        "إيقاف الأذان",
                        stopPendingIntent
                ).build()
        );

        return builder.build();
    }

    private void updateNotification(
            String prayerName
    ) {

        Intent stopIntent =
                new Intent(
                        this,
                        AdhanPlaybackService.class
                );

        stopIntent.setAction(
                ACTION_STOP_ADHAN
        );

        PendingIntent stopPendingIntent =
                PendingIntent.getService(
                        this,
                        9103,
                        stopIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Intent openIntent =
                new Intent(
                        this,
                        MainActivity.class
                );

        openIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent openPendingIntent =
                PendingIntent.getActivity(
                        this,
                        9102,
                        openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {

            builder =
                    new Notification.Builder(
                            this,
                            CHANNEL_ID
                    );

        } else {

            builder =
                    new Notification.Builder(this);
        }

        builder.setSmallIcon(
                R.drawable.ic_quran
        );

        builder.setContentTitle(
                "🔊 الأذان • " + prayerName
        );

        builder.setContentText(
                "جاري تشغيل الأذان"
        );

        builder.setContentIntent(
                openPendingIntent
        );

        builder.setOngoing(true);

        builder.setCategory(
                Notification.CATEGORY_ALARM
        );

        builder.setPriority(
                Notification.PRIORITY_HIGH
        );

        builder.addAction(
                new Notification.Action.Builder(
                        null,
                        "إيقاف الأذان",
                        stopPendingIntent
                ).build()
        );

        NotificationManager manager =
                (NotificationManager)
                        getSystemService(
                                NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.notify(
                    NOTIFICATION_ID,
                    builder.build()
            );
        }
    }

    private void stopAdhan() {

        stopCurrentPlayer();

        removeNotification();

        stopSelf();
    }

    private void removeNotification() {

        NotificationManager manager =
                (NotificationManager)
                        getSystemService(
                                NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.cancel(
                    NOTIFICATION_ID
            );
        }
    }

    private void stopCurrentPlayer() {

        if (mediaPlayer != null) {

            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {
            }

            try {
                mediaPlayer.reset();
            } catch (Exception ignored) {
            }

            try {
                mediaPlayer.release();
            } catch (Exception ignored) {
            }

            mediaPlayer = null;
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "أذان الصلاة",
                        NotificationManager.IMPORTANCE_HIGH
                );

        channel.setDescription(
                "تشغيل أذان الصلاة في لعلها المنجيه"
        );

        channel.setLockscreenVisibility(
                Notification.VISIBILITY_PUBLIC
        );

        NotificationManager manager =
                getSystemService(
                        NotificationManager.class
                );

        if (manager != null) {
            manager.createNotificationChannel(
                    channel
            );
        }
    }

    @Override
    public void onDestroy() {

        stopCurrentPlayer();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
