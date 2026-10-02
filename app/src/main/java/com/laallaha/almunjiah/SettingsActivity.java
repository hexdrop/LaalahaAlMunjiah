package com.laallaha.almunjiah;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private static final String PREFS = "app_settings";

    private static final String KEY_NIGHT_MODE = "quran_night_mode";
    private static final String KEY_READING_RESUME = "reading_resume_enabled";

    private final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private final int EMERALD = Color.rgb(18, 115, 85);
    private final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private final int GOLD = Color.rgb(190, 155, 75);
    private final int GOLD_SOFT = Color.rgb(248, 241, 220);
    private final int CREAM = Color.rgb(249, 248, 243);
    private final int WHITE = Color.WHITE;
    private final int TEXT = Color.rgb(36, 45, 41);
    private final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private final int BORDER = Color.rgb(228, 231, 226);

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);

        buildScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs == null) {
            prefs = getSharedPreferences(
                    PREFS,
                    MODE_PRIVATE
            );
        }

        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(CREAM);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(14), dp(16), dp(28));

        scroll.addView(
                root,
                new ScrollView.LayoutParams(
                        -1,
                        -2
                )
        );

        root.addView(buildHeader());

        root.addView(space(18));

        root.addView(sectionTitle("المظهر"));
        root.addView(buildSettingsCard());

        root.addView(space(18));

        root.addView(sectionTitle("القراءة"));
        root.addView(buildReadingCard());

        root.addView(space(18));

        root.addView(sectionTitle("الصوت والتلاوة"));
        root.addView(buildAudioCard());

        root.addView(space(18));

        root.addView(sectionTitle("الصلاة والتنبيهات"));
        root.addView(buildPrayerCard());

        root.addView(space(18));

        root.addView(sectionTitle("حول التطبيق"));
        root.addView(buildAboutCard());

        setContentView(scroll);
    }

    private View buildHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        EMERALD_DARK,
                        EMERALD
                }
        );

        bg.setCornerRadius(dp(26));
        header.setBackground(bg);
        header.setElevation(dp(5));

        TextView icon = text(
                "⚙",
                24,
                Color.WHITE,
                true
        );
        icon.setGravity(Gravity.CENTER);

        TextView title = text(
                "الإعدادات",
                23,
                Color.WHITE,
                true
        );
        title.setGravity(Gravity.CENTER);

        TextView subtitle = text(
                "خصص تجربتك في لعلها المنجيه",
                12,
                Color.rgb(226, 240, 234),
                false
        );
        subtitle.setGravity(Gravity.CENTER);

        header.addView(
                icon,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(30)
                )
        );

        header.addView(space(4));

        header.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        header.addView(space(3));

        header.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        return header;
    }

    private LinearLayout buildSettingsCard() {
        LinearLayout card = card();

        boolean night = prefs.getBoolean(KEY_NIGHT_MODE, false);

        card.addView(
                buildSwitchRow(
                        "الوضع الليلي",
                        "راحة أكبر للعين أثناء قراءة القرآن",
                        night,
                        (buttonView, isChecked) ->
                                prefs.edit()
                                        .putBoolean(KEY_NIGHT_MODE, isChecked)
                                        .apply()
                )
        );

        return card;
    }

    private LinearLayout buildReadingCard() {
        LinearLayout card = card();

        boolean resume = prefs.getBoolean(
                KEY_READING_RESUME,
                true
        );

        card.addView(
                buildSwitchRow(
                        "متابعة القراءة",
                        "إظهار آخر موضع وصلت إليه في القرآن",
                        resume,
                        (buttonView, isChecked) ->
                                prefs.edit()
                                        .putBoolean(
                                                KEY_READING_RESUME,
                                                isChecked
                                        )
                                        .apply()
                )
        );

        card.addView(divider());

        card.addView(
                buildActionRow(
                        "القرآن الكريم",
                        "فتح المصحف وإكمال القراءة",
                        "›",
                        v -> startActivity(
                                new Intent(
                                        SettingsActivity.this,
                                        QuranActivity.class
                                )
                        )
                )
        );

        android.content.SharedPreferences reading =
                getSharedPreferences(
                        "quran_reading",
                        MODE_PRIVATE
                );

        int savedSurahIndex =
                reading.getInt(
                        "surah_index",
                        -1
                );

        int savedAyahIndex =
                reading.getInt(
                        "ayah_index",
                        -1
                );

        String savedSurahName =
                reading.getString(
                        "surah_name",
                        ""
                );

        card.addView(divider());

        if (savedSurahIndex >= 0
                && savedAyahIndex >= 0
                && !savedSurahName.trim().isEmpty()) {

            card.addView(
                    buildSavedReadingRow(
                            "آخر موضع محفوظ",
                            "سورة " + savedSurahName
                                    + " • الآية "
                                    + (savedAyahIndex + 1),
                            v -> clearReadingPosition()
                    )
            );

        } else {

            card.addView(
                    buildActionRow(
                            "موضع القراءة",
                            "لا يوجد موضع محفوظ حاليًا",
                            "",
                            null
                    )
            );
        }

        card.addView(divider());

        card.addView(
                buildActionRow(
                        "إعادة ضبط إعدادات القراءة",
                        "الوضع الليلي + متابعة القراءة",
                        "↻",
                        v -> resetReadingSettings()
                )
        );

        return card;
    }

    private LinearLayout buildSavedReadingRow(
            String titleText,
            String valueText,
            View.OnClickListener clearListener
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(6), dp(8), dp(6), dp(8));
        row.setMinimumHeight(dp(72));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView title = text(
                titleText,
                16,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        TextView value = text(
                valueText,
                12,
                TEXT_SECONDARY,
                false
        );
        value.setGravity(Gravity.RIGHT);

        TextView badge = text(
                "آخر قراءة",
                11,
                EMERALD_DARK,
                true
        );
        badge.setGravity(Gravity.CENTER);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(GOLD_SOFT);
        badgeBg.setCornerRadius(dp(12));
        badge.setBackground(badgeBg);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));

        LinearLayout stateLine = new LinearLayout(this);
        stateLine.setOrientation(LinearLayout.HORIZONTAL);
        stateLine.setGravity(Gravity.CENTER_VERTICAL);

        stateLine.addView(
                badge,
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                )
        );

        stateLine.addView(space(8));

        stateLine.addView(
                value,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        texts.addView(title);
        texts.addView(space(5));
        texts.addView(stateLine);

        TextView clear = text(
                "×",
                27,
                EMERALD,
                true
        );
        clear.setGravity(Gravity.CENTER);
        clear.setClickable(true);
        clear.setFocusable(true);
        clear.setOnClickListener(clearListener);

        row.addView(
                clear,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(56)
                )
        );

        row.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        return row;
    }

    private void clearReadingPosition() {
        getSharedPreferences(
                "quran_reading",
                MODE_PRIVATE
        )
                .edit()
                .remove("surah_index")
                .remove("ayah_index")
                .remove("surah_name")
                .apply();

        Toast.makeText(
                this,
                "تم مسح موضع القراءة المحفوظ",
                Toast.LENGTH_SHORT
        ).show();

        buildScreen();
    }

    private void resetReadingSettings() {
        prefs.edit()
                .putBoolean(
                        KEY_NIGHT_MODE,
                        false
                )
                .putBoolean(
                        KEY_READING_RESUME,
                        true
                )
                .apply();

        Toast.makeText(
                this,
                "تمت إعادة إعدادات القراءة للوضع الافتراضي",
                Toast.LENGTH_SHORT
        ).show();

        buildScreen();
    }

    private LinearLayout buildCurrentStateRow(
            String titleText,
            String valueText,
            String actionText,
            View.OnClickListener listener
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(6), dp(8), dp(6), dp(8));
        row.setMinimumHeight(dp(72));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(listener);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView title = text(
                titleText,
                16,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        LinearLayout stateLine = new LinearLayout(this);
        stateLine.setOrientation(LinearLayout.HORIZONTAL);
        stateLine.setGravity(Gravity.CENTER_VERTICAL);

        TextView badge = text(
                "محدد حاليًا",
                11,
                EMERALD_DARK,
                true
        );
        badge.setGravity(Gravity.CENTER);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(EMERALD_SOFT);
        badgeBg.setCornerRadius(dp(12));
        badge.setBackground(badgeBg);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));

        TextView value = text(
                valueText,
                12,
                TEXT_SECONDARY,
                false
        );
        value.setGravity(Gravity.RIGHT);

        stateLine.addView(
                badge,
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                )
        );

        stateLine.addView(
                space(8)
        );

        stateLine.addView(
                value,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        texts.addView(title);
        texts.addView(space(5));
        texts.addView(stateLine);

        TextView action = text(
                actionText,
                26,
                EMERALD,
                true
        );
        action.setGravity(Gravity.CENTER);

        row.addView(
                action,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(56)
                )
        );

        row.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        return row;
    }

    private LinearLayout buildAudioCard() {
        LinearLayout card = card();

        SharedPreferences audio =
                getSharedPreferences(
                        "quran_audio",
                        MODE_PRIVATE
                );

        String reciter = audio.getString(
                "selected_reciter_name",
                "الشيخ أحمد العجمي"
        );

        card.addView(
                buildCurrentStateRow(
                        "القارئ الحالي",
                        reciter,
                        "›",
                        v -> startActivity(
                                new Intent(
                                        SettingsActivity.this,
                                        QuranActivity.class
                                )
                        )
                )
        );

        card.addView(divider());

        card.addView(
                buildActionRow(
                        "التلاوة",
                        "إدارة التلاوة من داخل المصحف",
                        "›",
                        v -> startActivity(
                                new Intent(
                                        SettingsActivity.this,
                                        QuranActivity.class
                                )
                        )
                )
        );

        return card;
    }

    private LinearLayout buildFeaturedActionRow(
            String titleText,
            String subtitleText,
            String badgeText,
            String actionText,
            View.OnClickListener listener
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(6), dp(8), dp(6), dp(8));
        row.setMinimumHeight(dp(76));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(listener);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView title = text(
                titleText,
                16,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        LinearLayout stateLine = new LinearLayout(this);
        stateLine.setOrientation(LinearLayout.HORIZONTAL);
        stateLine.setGravity(Gravity.CENTER_VERTICAL);

        TextView badge = text(
                badgeText,
                11,
                EMERALD_DARK,
                true
        );
        badge.setGravity(Gravity.CENTER);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(EMERALD_SOFT);
        badgeBg.setCornerRadius(dp(12));
        badge.setBackground(badgeBg);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));

        TextView subtitle = text(
                subtitleText,
                12,
                TEXT_SECONDARY,
                false
        );
        subtitle.setGravity(Gravity.RIGHT);

        stateLine.addView(
                badge,
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                )
        );

        stateLine.addView(space(8));

        stateLine.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        texts.addView(title);
        texts.addView(space(5));
        texts.addView(stateLine);

        TextView action = text(
                actionText,
                26,
                EMERALD,
                true
        );
        action.setGravity(Gravity.CENTER);

        row.addView(
                action,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(56)
                )
        );

        row.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        return row;
    }

    private LinearLayout buildPrayerCard() {
        LinearLayout card = card();

        card.addView(
                buildFeaturedActionRow(
                        "إعدادات الصلاة",
                        "مواقيت الصلاة والتنبيهات والأذان",
                        "الصلاة",
                        "›",
                        v -> startActivity(
                                new Intent(
                                        SettingsActivity.this,
                                        PrayerSettingsActivity.class
                                )
                        )
                )
        );

        return card;
    }

    private LinearLayout buildIdentityRow(
            String titleText,
            String subtitleText,
            String badgeText
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(6), dp(8), dp(6), dp(8));
        row.setMinimumHeight(dp(68));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView title = text(
                titleText,
                17,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        TextView subtitle = text(
                subtitleText,
                12,
                TEXT_SECONDARY,
                false
        );
        subtitle.setGravity(Gravity.RIGHT);

        texts.addView(title);
        texts.addView(space(4));
        texts.addView(subtitle);

        TextView badge = text(
                badgeText,
                11,
                EMERALD_DARK,
                true
        );
        badge.setGravity(Gravity.CENTER);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(GOLD_SOFT);
        badgeBg.setCornerRadius(dp(12));
        badge.setBackground(badgeBg);
        badge.setPadding(dp(9), dp(5), dp(9), dp(5));

        row.addView(
                badge,
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                )
        );

        row.addView(
                space(10)
        );

        row.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        return row;
    }

    private LinearLayout buildAboutCard() {
        LinearLayout card = card();

        String version = "غير متاح";

        try {
            version = getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;
        } catch (Exception ignored) {
        }

        card.addView(
                buildInfoRow(
                        "لعلها المنجيه",
                        "الإصدار " + version
                )
        );

        card.addView(divider());

        card.addView(
                buildInfoRow(
                        "الهوية",
                        "قرآن • أذكار • صلاة • قبلة"
                )
        );

        return card;
    }

    private LinearLayout buildSwitchRow(
            String titleText,
            String subtitleText,
            boolean checked,
            CompoundButton.OnCheckedChangeListener listener
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(6), dp(8), dp(6), dp(8));
        row.setMinimumHeight(dp(68));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView title = text(
                titleText,
                16,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        TextView subtitle = text(
                subtitleText,
                12,
                TEXT_SECONDARY,
                false
        );
        subtitle.setGravity(Gravity.RIGHT);

        texts.addView(title);
        texts.addView(space(4));
        texts.addView(subtitle);

        Switch sw = new Switch(this);
        sw.setChecked(checked);
        sw.setOnCheckedChangeListener(listener);

        row.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        row.addView(
                sw,
                new LinearLayout.LayoutParams(
                        dp(60),
                        dp(56)
                )
        );

        return row;
    }

    private LinearLayout buildActionRow(
            String titleText,
            String subtitleText,
            String actionText,
            View.OnClickListener listener
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(6), dp(8), dp(6), dp(8));
        row.setMinimumHeight(dp(64));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(listener);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView title = text(
                titleText,
                16,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        TextView subtitle = text(
                subtitleText,
                12,
                TEXT_SECONDARY,
                false
        );
        subtitle.setGravity(Gravity.RIGHT);

        texts.addView(title);
        texts.addView(space(4));
        texts.addView(subtitle);

        TextView action = text(
                actionText,
                26,
                EMERALD,
                true
        );
        action.setGravity(Gravity.CENTER);

        row.addView(
                action,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(56)
                )
        );

        row.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                )
        );

        return row;
    }

    private LinearLayout buildInfoRow(
            String titleText,
            String subtitleText
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(4), dp(7), dp(4), dp(7));

        TextView title = text(
                titleText,
                16,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        TextView subtitle = text(
                subtitleText,
                12,
                TEXT_SECONDARY,
                false
        );
        subtitle.setGravity(Gravity.RIGHT);

        row.addView(title);
        row.addView(space(3));
        row.addView(subtitle);

        return row;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(255, 255, 253));
        bg.setCornerRadius(dp(26));
        bg.setStroke(dp(1), Color.rgb(224, 230, 225));

        card.setBackground(bg);
        card.setElevation(dp(4));

        return card;
    }

    private TextView sectionTitle(String value) {
        TextView t = text(
                value,
                16,
                EMERALD_DARK,
                true
        );

        t.setGravity(Gravity.RIGHT);

        t.setPadding(
                dp(6),
                dp(2),
                dp(6),
                dp(10)
        );

        return t;
    }

    private View divider() {
        View v = new View(this);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(BORDER);
        bg.setCornerRadius(dp(1));

        v.setBackground(bg);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(1)
                );
        lp.setMargins(dp(4), dp(7), dp(4), dp(7));
        v.setLayoutParams(lp);

        return v;
    }

    private View space(int height) {
        View v = new View(this);
        v.setLayoutParams(
                new LinearLayout.LayoutParams(
                        1,
                        dp(height)
                )
        );
        return v;
    }

    private TextView text(
            String value,
            float size,
            int color,
            boolean bold
    ) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setTypeface(
                Typeface.DEFAULT,
                bold ? Typeface.BOLD : Typeface.NORMAL
        );
        t.setIncludeFontPadding(true);
        return t;
    }

    private int dp(int value) {
        return Math.round(
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

}
