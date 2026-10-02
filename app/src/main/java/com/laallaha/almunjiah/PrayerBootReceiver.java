package com.laallaha.almunjiah;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PrayerBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        PrayerAlarmScheduler.scheduleNextPrayers(
                context
        );
    }
}
