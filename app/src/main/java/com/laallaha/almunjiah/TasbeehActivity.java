package com.laallaha.almunjiah;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Vibrator;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TasbeehActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private static final int BORDER = Color.rgb(228, 231, 226);

    private static final String PREFS = "tasbeeh_data";
    private static final String COUNT = "count";
    private static final String TARGET = "target";
    private static final String DHIKR = "dhikr";

    private SharedPreferences prefs;

    private int count;
    private int target;
    private String dhikr;

    private TextView countView;
    private TextView targetView;
    private TextView progressView;
    private TextView dhikrView;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        count = prefs.getInt(COUNT, 0);
        target = prefs.getInt(TARGET, 33);
        dhikr = prefs.getString(DHIKR, "سبحان الله");

        buildPage();
        updateUI();
    }

    private void buildPage() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        root.addView(buildHeader());

        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setFillViewport(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(25)
        );

        // ========================================================
        // DHIKR CARD
        // ========================================================

        LinearLayout dhikrCard = new LinearLayout(this);
        dhikrCard.setOrientation(LinearLayout.VERTICAL);
        dhikrCard.setGravity(Gravity.CENTER);
        dhikrCard.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        GradientDrawable dhikrBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        WHITE,
                        Color.rgb(244, 249, 246)
                }
        );

        dhikrBg.setCornerRadius(dp(26));
        dhikrBg.setStroke(dp(1), BORDER);
        dhikrCard.setBackground(dhikrBg);
        dhikrCard.setElevation(dp(4));

        TextView small = text(
                "الذِّكر الحالي",
                12,
                GOLD,
                true
        );

        dhikrCard.addView(small);

        dhikrView = text(
                dhikr,
                28,
                EMERALD_DARK,
                true
        );

        dhikrView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams dhikrParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        dhikrParams.topMargin = dp(4);

        dhikrCard.addView(
                dhikrView,
                dhikrParams
        );

        content.addView(
                dhikrCard,
                matchWrap()
        );

        // ========================================================
        // COUNTER
        // ========================================================

        countView = text(
                String.valueOf(count),
                72,
                EMERALD_DARK,
                true
        );

        countView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams countParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(105)
                );

        countParams.topMargin = dp(18);

        content.addView(
                countView,
                countParams
        );

        targetView = text(
                "من " + target,
                14,
                TEXT_SECONDARY,
                false
        );

        targetView.setGravity(Gravity.CENTER);

        content.addView(
                targetView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(28)
                )
        );

        // ========================================================
        // PROGRESS
        // ========================================================

        LinearLayout progressContainer =
                new LinearLayout(this);

        progressContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        GradientDrawable progressBg =
                new GradientDrawable();

        progressBg.setColor(
                Color.rgb(225, 234, 229)
        );

        progressBg.setCornerRadius(dp(6));

        progressContainer.setBackground(
                progressBg
        );

        progressBar = new View(this);

        GradientDrawable barBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                GOLD,
                                EMERALD
                        }
                );

        barBg.setCornerRadius(dp(6));
        progressBar.setBackground(barBg);

        progressContainer.addView(
                progressBar,
                new LinearLayout.LayoutParams(
                        0,
                        dp(8)
                )
        );

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(8)
                );

        progressParams.topMargin = dp(8);

        content.addView(
                progressContainer,
                progressParams
        );

        progressView = text(
                "0%",
                12,
                EMERALD,
                true
        );

        progressView.setGravity(Gravity.CENTER);

        content.addView(
                progressView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(28)
                )
        );

        // ========================================================
        // BIG TASBEEH BUTTON
        // ========================================================

        TextView tapButton = text(
                "سَبِّح",
                28,
                WHITE,
                true
        );

        tapButton.setGravity(Gravity.CENTER);

        GradientDrawable tapBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        EMERALD,
                        EMERALD_DARK
                }
        );

        tapBg.setShape(
                GradientDrawable.OVAL
        );

        tapButton.setBackground(tapBg);
        tapButton.setElevation(dp(10));

        LinearLayout.LayoutParams tapParams =
                new LinearLayout.LayoutParams(
                        dp(190),
                        dp(190)
                );

        tapParams.topMargin = dp(14);

        content.addView(
                tapButton,
                tapParams
        );

        tapButton.setOnClickListener(v -> increment());

        TextView hint = text(
                "اضغط للذكر • اضغط بهدوء واستحضر القلب",
                11,
                TEXT_SECONDARY,
                false
        );

        hint.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams hintParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(35)
                );

        hintParams.topMargin = dp(4);

        content.addView(hint, hintParams);

        // ========================================================
        // DHIKR CHOICES
        // ========================================================

        TextView chooseTitle = text(
                "اختر الذكر",
                16,
                TEXT,
                true
        );

        chooseTitle.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams chooseParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(32)
                );

        chooseParams.topMargin = dp(12);

        content.addView(
                chooseTitle,
                chooseParams
        );

        LinearLayout choices =
                new LinearLayout(this);

        choices.setOrientation(
                LinearLayout.HORIZONTAL
        );

        choices.setGravity(Gravity.CENTER);

        addChoice(choices, "سبحان الله");
        addChoice(choices, "الحمد لله");
        addChoice(choices, "الله أكبر");
        addChoice(choices, "لا إله إلا الله");

        content.addView(
                choices,
                matchWrap()
        );

        // ========================================================
        // RESET
        // ========================================================

        Button reset = new Button(this);

        reset.setText("بدء جولة جديدة");
        reset.setTextSize(13);
        reset.setTextColor(EMERALD_DARK);
        reset.setAllCaps(false);
        reset.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        GradientDrawable resetBg =
                new GradientDrawable();

        resetBg.setColor(EMERALD_SOFT);
        resetBg.setCornerRadius(dp(18));
        resetBg.setStroke(dp(1), BORDER);

        reset.setBackground(resetBg);

        LinearLayout.LayoutParams resetParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(50)
                );

        resetParams.topMargin = dp(14);

        content.addView(
                reset,
                resetParams
        );

        reset.setOnClickListener(v -> resetCounter());

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        setContentView(root);
    }

    private LinearLayout buildHeader() {

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(
                dp(18),
                dp(14),
                dp(18),
                dp(14)
        );

        GradientDrawable bg =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                EMERALD_DARK,
                                EMERALD
                        }
                );

        header.setBackground(bg);

        TextView back = text(
                "‹",
                34,
                WHITE,
                false
        );

        back.setGravity(Gravity.CENTER);

        back.setOnClickListener(
                v -> finish()
        );

        header.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(48)
                )
        );

        LinearLayout titleBox =
                new LinearLayout(this);

        titleBox.setOrientation(
                LinearLayout.VERTICAL
        );

        titleBox.setGravity(
                Gravity.RIGHT
        );

        TextView title = text(
                "السبحة",
                23,
                WHITE,
                true
        );

        TextView subtitle = text(
                "واذكر ربك كثيرًا",
                11,
                Color.rgb(220, 239, 230),
                false
        );

        titleBox.addView(title);
        titleBox.addView(subtitle);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        titleParams.setMargins(
                dp(10),
                0,
                dp(10),
                0
        );

        header.addView(
                titleBox,
                titleParams
        );

        TextView icon = text(
                "✦",
                24,
                GOLD,
                true
        );

        icon.setGravity(Gravity.CENTER);

        header.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(48)
                )
        );

        return header;
    }

    private void addChoice(
            LinearLayout parent,
            String value
    ) {

        TextView choice = text(
                value,
                10,
                TEXT,
                true
        );

        choice.setGravity(Gravity.CENTER);

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                value.equals(dhikr)
                        ? EMERALD_SOFT
                        : WHITE
        );

        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(1), BORDER);

        choice.setBackground(bg);

        choice.setOnClickListener(v -> {

            dhikr = value;

            count = 0;

            prefs.edit()
                    .putString(DHIKR, dhikr)
                    .putInt(COUNT, count)
                    .apply();

            buildPage();
            updateUI();
        });

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        dp(45),
                        1f
                );

        params.setMargins(
                dp(3),
                0,
                dp(3),
                0
        );

        parent.addView(
                choice,
                params
        );
    }

    private void increment() {

        count++;

        if (count > target) {
            count = target;
        }

        prefs.edit()
                .putInt(COUNT, count)
                .apply();

        vibrate();

        updateUI();

        if (count == target) {
            // الجولة اكتملت، ونترك العداد ثابتًا
            vibrateLong();
        }
    }

    private void resetCounter() {

        count = 0;

        prefs.edit()
                .putInt(COUNT, 0)
                .apply();

        updateUI();
    }

    private void updateUI() {

        if (countView != null) {
            countView.setText(
                    String.valueOf(count)
            );
        }

        if (targetView != null) {
            targetView.setText(
                    "من " + target
            );
        }

        if (dhikrView != null) {
            dhikrView.setText(dhikr);
        }

        if (progressView != null) {

            int percent = target <= 0
                    ? 0
                    : Math.min(
                            100,
                            Math.round(
                                    count * 100f / target
                            )
                    );

            progressView.setText(
                    percent + "%"
            );

            LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams)
                            progressBar.getLayoutParams();

            params.width =
                    Math.max(
                            dp(2),
                            (int)(
                                    dp(300) *
                                    percent /
                                    100f
                            )
                    );

            progressBar.setLayoutParams(params);
        }
    }

    private void vibrate() {

        try {

            Vibrator vibrator =
                    (Vibrator)
                            getSystemService(
                                    Context.VIBRATOR_SERVICE
                            );

            if (vibrator == null) return;

            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                        android.os.VibrationEffect
                                .createOneShot(
                                        18,
                                        35
                                )
                );
            } else {
                vibrator.vibrate(18);
            }

        } catch (Exception ignored) {}
    }

    private void vibrateLong() {

        try {

            Vibrator vibrator =
                    (Vibrator)
                            getSystemService(
                                    Context.VIBRATOR_SERVICE
                            );

            if (vibrator == null) return;

            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                        android.os.VibrationEffect
                                .createOneShot(
                                        80,
                                        70
                                )
                );
            } else {
                vibrator.vibrate(80);
            }

        } catch (Exception ignored) {}
    }

    private TextView text(
            String value,
            float size,
            int color,
            boolean bold
    ) {

        TextView v = new TextView(this);

        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);

        v.setTypeface(
                Typeface.create(
                        "sans-serif",
                        bold
                                ? Typeface.BOLD
                                : Typeface.NORMAL
                )
        );

        v.setGravity(Gravity.RIGHT);
        v.setTextDirection(
                View.TEXT_DIRECTION_RTL
        );

        return v;
    }

    private LinearLayout.LayoutParams matchWrap() {

        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {

        return (int)(
                value *
                getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
