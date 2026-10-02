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
import android.content.Intent;

public class AudioEpisodesActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);

    private String type;
    private String sectionTitle;

    private static final String QURAN_BASE =
            "https://media.myhuda.com/download/files/media/4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/5-Qassas_Qurqn/";

    private static final String KHULAFAA_BASE =
            "https://media.myhuda.com/download/files/media/4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/4-Kholafaa/";

    private static final String CHARACTERS_BASE =
            "https://media.myhuda.com/download/files/media/4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/10-Shakhsiat/";

    private static final String RAWAE_BASE =
            "https://media.myhuda.com/download/files/media/4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/7-Rawaea_%20Qasas/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getStringExtra("type");
        sectionTitle = getIntent().getStringExtra("title");

        if (sectionTitle == null) {
            sectionTitle = "الصوتيات";
        }

        buildPage();
    }

    private void buildPage() {
        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);

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
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        String[] names = getNames();
        String[] urls = getUrls();

        for (int i = 0; i < names.length; i++) {
            addEpisode(
                    content,
                    i + 1,
                    names[i],
                    urls[i]
            );
        }

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1f
                )
        );

        setContentView(root);
    }

    private LinearLayout buildHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(18), dp(16), dp(18), dp(18));
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        EMERALD_DARK,
                        EMERALD
                }
        );
        header.setBackground(bg);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView back = text("‹", 38, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        back.setOnClickListener(v -> finish());

        top.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(55),
                        dp(55)
                )
        );

        TextView title = text(
                sectionTitle,
                23,
                Color.WHITE,
                true
        );
        title.setGravity(Gravity.CENTER);

        top.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        dp(55),
                        1f
                )
        );

        TextView star = text("✦", 25, GOLD, true);
        star.setGravity(Gravity.CENTER);

        top.addView(
                star,
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(55)
                )
        );

        header.addView(top);

        TextView subtitle = text(
                "اختر الحلقة واستمع إليها",
                13,
                Color.rgb(220, 235, 228),
                false
        );
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(7), 0, 0);

        header.addView(subtitle);

        return header;
    }

    private void addEpisode(
            LinearLayout parent,
            int number,
            String name,
            String url
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        card.setPadding(
                dp(13),
                dp(10),
                dp(13),
                dp(10)
        );

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.WHITE,
                        Color.rgb(238, 246, 241)
                }
        );

        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(1), Color.rgb(220, 228, 222));

        card.setBackground(bg);
        card.setElevation(dp(3));

        TextView numberView = text(
                String.valueOf(number),
                17,
                Color.WHITE,
                true
        );
        numberView.setGravity(Gravity.CENTER);

        GradientDrawable numberBg = new GradientDrawable();
        numberBg.setShape(GradientDrawable.OVAL);
        numberBg.setColor(EMERALD);
        numberBg.setStroke(
                dp(1),
                Color.argb(100, 190, 155, 75)
        );

        numberView.setBackground(numberBg);

        card.addView(
                numberView,
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(45)
                )
        );

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView title = text(
                name,
                15,
                EMERALD_DARK,
                true
        );
        title.setGravity(Gravity.RIGHT);

        texts.addView(title);

        TextView sub = text(
                "اضغط للاستماع • تشغيل وتنزيل أوفلاين",
                10,
                TEXT_SECONDARY,
                false
        );
        sub.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(-1, -2);

        subParams.topMargin = dp(4);

        texts.addView(sub, subParams);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1f
                );

        textParams.setMargins(
                dp(12),
                0,
                dp(10),
                0
        );

        card.addView(texts, textParams);

        TextView play = text("▶", 20, GOLD, true);
        play.setGravity(Gravity.CENTER);

        card.addView(
                play,
                new LinearLayout.LayoutParams(
                        dp(38),
                        dp(50)
                )
        );

        card.setOnClickListener(v ->
                openEpisode(
                        name,
                        url,
                        number
                )
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(10)
        );

        parent.addView(card, cardParams);
    }

    private void openEpisode(
            String name,
            String url,
            int number
    ) {
        Intent intent =
                new Intent(
                        this,
                        StoryDetailActivity.class
                );

        intent.putExtra(
                "name",
                name
        );

        intent.putExtra(
                "subtitle",
                sectionTitle
        );

        intent.putExtra(
                "description",
                "تسجيل صوتي من قسم " + sectionTitle
        );

        intent.putExtra(
                "audioUrl",
                url
        );

        intent.putExtra(
                "audioFileName",
                makeFileName(number)
        );

        startActivity(intent);
    }

    private String makeFileName(int number) {
        String safe = sectionTitle
                .replaceAll("[^\\p{L}\\p{N}]+", "_");

        return safe + "_" + number + ".mp3";
    }

    private String[] getNames() {
        if ("quran".equals(type)) {
            return new String[]{
                    "قصة بقرة بني إسرائيل وقصة هاروت وماروت",
                    "قصة طالوت وجالوت وقصة العزير",
                    "قصة ابني آدم وقصة أصحاب السبت",
                    "قصة بلعام بن باعوراء وأصحاب الكهف وقصة أصحاب الجنتين",
                    "قصة موسى والخضر وقصة ذي القرنين",
                    "قصة يأجوج ومأجوج وقصة قارون",
                    "قصة لقمان وقصة سبأ وقصة أصحاب القرية",
                    "قصة مؤمن آل فرعون وقصة أصحاب الجنة وأصحاب الأخدود وأصحاب الفيل"
            };
        }

        if ("khulafaa".equals(type)) {
            return new String[]{
                    "أبو بكر الصديق - سلسلة الخلفاء الراشدون",
                    "عمر بن الخطاب - سلسلة الخلفاء الراشدون",
                    "عثمان بن عفان - سلسلة الخلفاء الراشدون",
                    "علي بن أبي طالب - سلسلة الخلفاء الراشدون"
            };
        }

        if ("characters".equals(type)) {
            return new String[]{
                    "الحلقة الأولى",
                    "الحلقة الثانية",
                    "الحلقة الثالثة",
                    "الحلقة الرابعة",
                    "الحلقة الخامسة",
                    "الحلقة السادسة",
                    "الحلقة السابعة",
                    "الحلقة الثامنة",
                    "الحلقة التاسعة",
                    "الحلقة العاشرة",
                    "الحلقة الحادية عشرة",
                    "الحلقة الثانية عشرة",
                    "الحلقة الثالثة عشرة",
                    "الحلقة الرابعة عشرة",
                    "الحلقة الخامسة عشرة",
                    "الحلقة السادسة عشرة",
                    "الحلقة السابعة عشرة",
                    "الحلقة الثامنة عشرة",
                    "الحلقة التاسعة عشرة",
                    "الحلقة العشرون",
                    "الحلقة الحادية والعشرون",
                    "الحلقة الثانية والعشرون",
                    "الحلقة الثالثة والعشرون",
                    "الحلقة الرابعة والعشرون",
                    "الحلقة الخامسة والعشرون",
                    "الحلقة السادسة والعشرون",
                    "الحلقة السابعة والعشرون",
                    "الحلقة الثامنة والعشرون",
                    "الحلقة التاسعة والعشرون",
                    "الحلقة الثلاثون"
            };
        }

        return new String[]{
                "ابن الملك في دمشق - قصة إسلام عمر بن الخطاب",
                "الحلقة الثانية",
                "الحلقة الثالثة",
                "الحلقة الرابعة",
                "الحلقة الخامسة",
                "الحلقة السادسة",
                "الحلقة السابعة",
                "الحلقة الأخيرة"
        };
    }

    private String[] getUrls() {
        if ("quran".equals(type)) {
            return new String[]{
                    QURAN_BASE + "01.mp3",
                    QURAN_BASE + "02.mp3",
                    QURAN_BASE + "03.mp3",
                    QURAN_BASE + "04.mp3",
                    QURAN_BASE + "05.mp3",
                    QURAN_BASE + "06.mp3",
                    QURAN_BASE + "07.mp3",
                    QURAN_BASE + "08.mp3"
            };
        }

        if ("khulafaa".equals(type)) {
            return new String[]{
                    KHULAFAA_BASE + "1.mp3",
                    KHULAFAA_BASE + "2.mp3",
                    KHULAFAA_BASE + "3.mp3",
                    KHULAFAA_BASE + "4.mp3"
            };
        }

        if ("characters".equals(type)) {
            String[] urls = new String[30];

            for (int i = 0; i < 30; i++) {
                urls[i] =
                        CHARACTERS_BASE +
                        String.format(
                                java.util.Locale.US,
                                "%02d.mp3",
                                i + 1
                        );
            }

            return urls;
        }

        return new String[]{
                RAWAE_BASE + "01.mp3",
                RAWAE_BASE + "02.mp3",
                RAWAE_BASE + "03.mp3",
                RAWAE_BASE + "04.mp3",
                RAWAE_BASE + "05.mp3",
                RAWAE_BASE + "06.mp3",
                RAWAE_BASE + "07.mp3",
                RAWAE_BASE + "08.mp3"
        };
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
                        .density +
                0.5f
        );
    }
}
