package com.laallaha.almunjiah;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AudioLibraryActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);

        buildPage();
    }

    private void buildPage() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        root.addView(buildHeader());

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(18), dp(16), dp(30));

        TextView intro = text(
                "مجموعة مختارة من أجمل التسجيلات الصوتية",
                15,
                TEXT_SECONDARY,
                false
        );
        intro.setGravity(Gravity.RIGHT);
        intro.setPadding(dp(4), 0, dp(4), dp(18));
        content.addView(intro);

        addCategory(
                content,
                "📖",
                "قصص القرآن الكريم",
                "8 حلقات • قصص من القرآن الكريم",
                8,
                "quran"
        );

        addCategory(
                content,
                "🌿",
                "قصص الأنبياء",
                "قصص الأنبياء عليهم السلام",
                0,
                "prophets"
        );

        addCategory(
                content,
                "🕋",
                "السيرة النبوية",
                "27 جزءًا • سيرة خير البشر ﷺ",
                27,
                "seerah"
        );

        addCategory(
                content,
                "🕌",
                "الخلفاء الراشدون",
                "4 حلقات • أبو بكر وعمر وعثمان وعلي رضي الله عنهم",
                4,
                "khulafaa"
        );

        addCategory(
                content,
                "👤",
                "شخصيات وعِبر",
                "30 حلقة • مواقف وشخصيات وعبر",
                30,
                "characters"
        );

        addCategory(
                content,
                "✨",
                "روائع القصص",
                "8 حلقات • قصص مميزة",
                8,
                "rawae"
        );

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
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{EMERALD_DARK, EMERALD}
        );
        header.setBackground(bg);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("‹", 38, WHITE, false);
        back.setGravity(Gravity.CENTER);
        back.setOnClickListener(v -> finish());

        top.addView(
                back,
                new LinearLayout.LayoutParams(dp(55), dp(55))
        );

        TextView title = text(
                "🎧 الصوتيات",
                25,
                WHITE,
                true
        );
        title.setGravity(Gravity.CENTER);

        top.addView(
                title,
                new LinearLayout.LayoutParams(0, dp(55), 1f)
        );

        TextView star = text("✦", 25, GOLD, true);
        star.setGravity(Gravity.CENTER);

        top.addView(
                star,
                new LinearLayout.LayoutParams(dp(45), dp(55))
        );

        header.addView(top);

        TextView subtitle = text(
                "استمع إلى القصص والسيرة وأجمل المواعظ",
                14,
                Color.rgb(220, 235, 228),
                false
        );
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(8), 0, 0);

        header.addView(subtitle);

        return header;
    }

    private void addCategory(
            LinearLayout parent,
            String icon,
            String title,
            String subtitle,
            int count,
            String type
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(15), dp(13), dp(15), dp(13));
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(255, 255, 255),
                        Color.rgb(238, 246, 241)
                }
        );
        bg.setCornerRadius(dp(20));
        bg.setStroke(dp(1), Color.rgb(220, 228, 222));
        card.setBackground(bg);
        card.setElevation(dp(4));

        TextView iconView = text(icon, 28, EMERALD_DARK, false);
        iconView.setGravity(Gravity.CENTER);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(Color.rgb(231, 244, 238));
        iconBg.setShape(GradientDrawable.OVAL);

        iconView.setBackground(iconBg);

        card.addView(
                iconView,
                new LinearLayout.LayoutParams(dp(58), dp(58))
        );

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView titleView = text(
                title,
                18,
                EMERALD_DARK,
                true
        );
        titleView.setGravity(Gravity.RIGHT);

        texts.addView(titleView);

        TextView subtitleView = text(
                subtitle,
                12,
                TEXT_SECONDARY,
                false
        );
        subtitleView.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subParams.topMargin = dp(5);

        texts.addView(subtitleView, subParams);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        textParams.setMargins(dp(12), 0, dp(10), 0);

        card.addView(texts, textParams);

        TextView arrow = text(
                "‹",
                30,
                GOLD,
                true
        );
        arrow.setGravity(Gravity.CENTER);

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(dp(30), dp(55))
        );

        card.setOnClickListener(v -> openCategory(type));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, dp(12));

        parent.addView(card, cardParams);
    }

    private void openCategory(String type) {
        if ("prophets".equals(type)) {
            startActivity(
                    new android.content.Intent(
                            this,
                            StoriesActivity.class
                    )
            );
            return;
        }

        if ("seerah".equals(type)) {
            startActivity(
                    new android.content.Intent(
                            this,
                            SeerahActivity.class
                    )
            );
            return;
        }

        if ("quran".equals(type)
                || "khulafaa".equals(type)
                || "characters".equals(type)
                || "rawae".equals(type)) {

            android.content.Intent intent =
                    new android.content.Intent(
                            this,
                            AudioEpisodesActivity.class
                    );

            intent.putExtra("type", type);
            intent.putExtra(
                    "title",
                    getCategoryTitle(type)
            );

            startActivity(intent);
            return;
        }

        ToastMessage("القسم غير متاح حاليًا");
    }

    private String getCategoryTitle(String type) {
        if ("quran".equals(type)) {
            return "قصص القرآن الكريم";
        }

        if ("khulafaa".equals(type)) {
            return "الخلفاء الراشدون";
        }

        if ("characters".equals(type)) {
            return "شخصيات وعِبر";
        }

        if ("rawae".equals(type)) {
            return "روائع القصص";
        }

        return "الصوتيات";
    }

    private void ToastMessage(String message) {
        android.widget.Toast.makeText(
                this,
                message,
                android.widget.Toast.LENGTH_SHORT
        ).show();
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

        if (bold) {
            v.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );
        }

        return v;
    }

    private int dp(int value) {
        return (int)(
                value *
                getResources()
                        .getDisplayMetrics()
                        .density
                + 0.5f
        );
    }
}
