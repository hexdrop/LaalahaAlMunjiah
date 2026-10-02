package com.laallaha.almunjiah;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class PrayerSettingsActivity extends Activity {

    private final int EMERALD_DARK =
            Color.rgb(10, 79, 59);

    private final int EMERALD_SOFT =
            Color.rgb(231, 244, 238);

    private final int GOLD =
            Color.rgb(190, 155, 75);

    private final int CREAM =
            Color.rgb(249, 248, 243);

    private final int TEXT =
            Color.rgb(36, 45, 41);

    private final int TEXT_SECONDARY =
            Color.rgb(105, 113, 108);

    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(
                EMERALD_DARK
        );

        getWindow().setNavigationBarColor(
                CREAM
        );

        buildScreen();
    }

    private void buildScreen() {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setBackgroundColor(CREAM);

        content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(30)
        );

        scroll.addView(
                content,
                new ScrollView.LayoutParams(
                        -1,
                        -2
                )
        );

        buildHeader();
        buildPrayerNotifications();
        buildAdhan();

        setContentView(scroll);
    }

    private void buildHeader() {

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.VERTICAL
        );

        header.setGravity(
                Gravity.CENTER
        );

        GradientDrawable bg =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                EMERALD_DARK,
                                Color.rgb(18, 115, 85)
                        }
                );

        bg.setCornerRadius(dp(24));

        header.setBackground(bg);

        header.setPadding(
                dp(20),
                dp(24),
                dp(20),
                dp(24)
        );

        TextView title =
                text(
                        "إعدادات الصلاة",
                        23,
                        Color.WHITE,
                        Typeface.BOLD
                );

        TextView subtitle =
                text(
                        "تحكم في التنبيهات والأذان",
                        14,
                        Color.rgb(220, 235, 228),
                        Typeface.NORMAL
                );

        header.addView(title);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                );

        subParams.topMargin = dp(6);

        header.addView(
                subtitle,
                subParams
        );

        content.addView(
                header,
                matchParams(0)
        );
    }

    private void buildPrayerNotifications() {

        addSectionTitle(
                "تنبيهات الصلاة"
        );

        addSwitchCard(
                "تفعيل تنبيهات الصلاة",
                "تشغيل أو إيقاف جميع تنبيهات الصلاة",
                PrayerNotificationSettings.isEnabled(this),
                (button, checked) -> {

                    PrayerNotificationSettings.setEnabled(
                            this,
                            checked
                    );

                    PrayerAlarmScheduler.scheduleNextPrayers(
                            this
                    );
                },
                true
        );

        addSwitchCard(
                "الفجر",
                "تنبيه عند دخول وقت الفجر",
                PrayerNotificationSettings.isPrayerEnabled(
                        this,
                        "الفجر"
                ),
                (button, checked) -> updatePrayer(
                        "الفجر",
                        checked
                ),
                false
        );

        addSwitchCard(
                "الظهر",
                "تنبيه عند دخول وقت الظهر",
                PrayerNotificationSettings.isPrayerEnabled(
                        this,
                        "الظهر"
                ),
                (button, checked) -> updatePrayer(
                        "الظهر",
                        checked
                ),
                false
        );

        addSwitchCard(
                "العصر",
                "تنبيه عند دخول وقت العصر",
                PrayerNotificationSettings.isPrayerEnabled(
                        this,
                        "العصر"
                ),
                (button, checked) -> updatePrayer(
                        "العصر",
                        checked
                ),
                false
        );

        addSwitchCard(
                "المغرب",
                "تنبيه عند دخول وقت المغرب",
                PrayerNotificationSettings.isPrayerEnabled(
                        this,
                        "المغرب"
                ),
                (button, checked) -> updatePrayer(
                        "المغرب",
                        checked
                ),
                false
        );

        addSwitchCard(
                "العشاء",
                "تنبيه عند دخول وقت العشاء",
                PrayerNotificationSettings.isPrayerEnabled(
                        this,
                        "العشاء"
                ),
                (button, checked) -> updatePrayer(
                        "العشاء",
                        checked
                ),
                false
        );
    }

    private void buildAdhan() {

        addSectionTitle(
                "الأذان"
        );

        addSwitchCard(
                "تشغيل الأذان",
                "تشغيل الأذان الكامل عند دخول وقت الصلاة",
                PrayerNotificationSettings.isAdhanEnabled(this),
                (button, checked) ->
                        PrayerNotificationSettings.setAdhanEnabled(
                                this,
                                checked
                        ),
                true
        );

        TextView note =
                text(
                        "أذان الفجر سيُضاف لاحقًا بملف مستقل يحتوي على «الصلاة خير من النوم».",
                        13,
                        TEXT_SECONDARY,
                        Typeface.NORMAL
                );

        note.setPadding(
                dp(6),
                dp(8),
                dp(6),
                dp(8)
        );

        content.addView(
                note,
                matchParams(0)
        );
    }

    private void updatePrayer(
            String prayerName,
            boolean enabled
    ) {

        PrayerNotificationSettings.setPrayerEnabled(
                this,
                prayerName,
                enabled
        );

        PrayerAlarmScheduler.scheduleNextPrayers(
                this
        );
    }

    private void addSectionTitle(
            String titleText
    ) {

        TextView title =
                text(
                        titleText,
                        18,
                        EMERALD_DARK,
                        Typeface.BOLD
                );

        LinearLayout.LayoutParams params =
                matchParams(0);

        params.topMargin = dp(22);
        params.bottomMargin = dp(8);

        content.addView(
                title,
                params
        );
    }

    private void addSwitchCard(
            String titleText,
            String subtitleText,
            boolean checked,
            CompoundButton.OnCheckedChangeListener listener,
            boolean mainSwitch
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(14),
                dp(12),
                dp(14)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(18));
        bg.setStroke(
                dp(1),
                Color.rgb(228, 231, 226)
        );

        card.setBackground(bg);

        LinearLayout texts =
                new LinearLayout(this);

        texts.setOrientation(
                LinearLayout.VERTICAL
        );

        TextView title =
                text(
                        titleText,
                        16,
                        TEXT,
                        Typeface.BOLD
                );

        TextView subtitle =
                text(
                        subtitleText,
                        12,
                        TEXT_SECONDARY,
                        Typeface.NORMAL
                );

        texts.addView(title);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        subParams.topMargin = dp(4);

        texts.addView(
                subtitle,
                subParams
        );

        card.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        Switch sw =
                new Switch(this);

        sw.setChecked(checked);

        if (mainSwitch) {
            sw.setThumbTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.WHITE
                    )
            );
        }

        sw.setOnCheckedChangeListener(
                listener
        );

        card.addView(
                sw,
                new LinearLayout.LayoutParams(
                        dp(58),
                        -2
                )
        );

        LinearLayout.LayoutParams cardParams =
                matchParams(0);

        cardParams.bottomMargin =
                dp(9);

        content.addView(
                card,
                cardParams
        );
    }

    private TextView text(
            String value,
            float size,
            int color,
            int style
    ) {

        TextView tv =
                new TextView(this);

        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);
        tv.setTypeface(
                Typeface.DEFAULT,
                style
        );

        tv.setGravity(
                Gravity.RIGHT
        );

        return tv;
    }

    private LinearLayout.LayoutParams matchParams(int height) {
        return new LinearLayout.LayoutParams(
                -1,
                height == 0
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : height
        );
    }
    private int dp(int value) {
        return Math.round(
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
