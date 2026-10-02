package com.laallaha.almunjiah;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.IBinder;

public class QuranAudioService extends Service {

    private MediaPlayer mediaPlayer;
    private int currentSurahIndex = -1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {
        return START_NOT_STICKY;
    }

    public boolean isPlaying() {
        try {
            return mediaPlayer != null
                    && mediaPlayer.isPlaying();
        } catch (Exception ignored) {
            return false;
        }
    }

    public int getCurrentSurahIndex() {
        return currentSurahIndex;
    }

    public int getCurrentPosition() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            }
        } catch (Exception ignored) {
        }

        return 0;
    }

    public int getDuration() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getDuration();
            }
        } catch (Exception ignored) {
        }

        return 0;
    }

    private void releasePlayer() {
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

        currentSurahIndex = -1;
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
