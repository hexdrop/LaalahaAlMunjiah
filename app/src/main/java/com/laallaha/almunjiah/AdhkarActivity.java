package com.laallaha.almunjiah;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdhkarActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private static final int BORDER = Color.rgb(228, 231, 226);

    private final List<Dhikr> currentAdhkar = new ArrayList<>();

    private String currentCategory = "";
    private int currentIndex = 0;
    private int currentCount = 0;

    private android.content.SharedPreferences adhkarPrefs;

    private static class Dhikr {
        String text;
        int count;
        String source;
        String grade;

        Dhikr(String text, int count) {
            this(text, count, "", "");
        }

        Dhikr(
                String text,
                int count,
                String source,
                String grade
        ) {
            this.text = text;
            this.count = count;
            this.source = source;
            this.grade = grade;
        }
    }

    private void add(String text, int count) {
        currentAdhkar.add(new Dhikr(text, count));
    }

    private void add(String text, int count, String source, String grade) {
        currentAdhkar.add(new Dhikr(text, count, source, grade));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adhkarPrefs = getSharedPreferences(
                "ADHKAR_PROGRESS",
                MODE_PRIVATE
        );

        buildHome();
    }

    // =========================================================
    // HOME
    // =========================================================

    private void buildHome() {

        // =====================================================
        // ROOT
        // =====================================================
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);

        // =====================================================
        // HEADER
        // =====================================================
        root.addView(
                buildHeader("الأذكار", true),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(78)
                )
        );

        // =====================================================
        // SCROLL
        // =====================================================
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setClipToPadding(false);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(30)
        );

        // =====================================================
        // PREMIUM HERO
        // =====================================================
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        hero.setPadding(
                dp(22),
                dp(18),
                dp(22),
                dp(18)
        );

        GradientDrawable heroBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(7, 67, 50),
                        EMERALD_DARK,
                        EMERALD
                }
        );

        heroBg.setCornerRadius(dp(30));

        hero.setBackground(heroBg);
        hero.setElevation(dp(6));

        // ornament
        TextView heroIcon = text(
                "۞",
                27,
                Color.rgb(232, 208, 145),
                true
        );

        heroIcon.setGravity(Gravity.RIGHT);

        hero.addView(
                heroIcon,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(30)
                )
        );

        // title
        TextView heroTitle = text(
                "اذكر الله في كل وقت",
                24,
                WHITE,
                true
        );

        heroTitle.setGravity(Gravity.RIGHT);
        heroTitle.setTextDirection(View.TEXT_DIRECTION_RTL);

        hero.addView(
                heroTitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(40)
                )
        );

        // ayah
        TextView heroAyah = text(
                "وَاذْكُرْ رَبَّكَ إِذَا نَسِيتَ",
                18,
                Color.rgb(242, 235, 211),
                true
        );

        heroAyah.setGravity(Gravity.RIGHT);
        heroAyah.setTextDirection(View.TEXT_DIRECTION_RTL);

        hero.addView(
                heroAyah,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(35)
                )
        );

        // subtitle
        TextView heroSub = text(
                "اختر وردك واجعل للذكر وقتًا من يومك",
                13,
                Color.rgb(216, 232, 224),
                false
        );

        heroSub.setGravity(Gravity.RIGHT);
        heroSub.setTextDirection(View.TEXT_DIRECTION_RTL);

        hero.addView(
                heroSub,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(29)
                )
        );

        content.addView(
                hero,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(174)
                )
        );

        // =====================================================
        // DAILY DASHBOARD
        // =====================================================
        LinearLayout dashboard = new LinearLayout(this);
        dashboard.setOrientation(LinearLayout.VERTICAL);
        dashboard.setPadding(
                dp(18),
                dp(17),
                dp(18),
                dp(15)
        );

        GradientDrawable dashboardBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        WHITE,
                        Color.rgb(245, 250, 247)
                }
        );

        dashboardBg.setCornerRadius(dp(27));
        dashboardBg.setStroke(dp(1), BORDER);

        dashboard.setBackground(dashboardBg);
        dashboard.setElevation(dp(4));

        LinearLayout.LayoutParams dashboardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(181)
                );

        dashboardParams.setMargins(
                0,
                dp(12),
                0,
                0
        );

        // dashboard top row
        LinearLayout dashTop = new LinearLayout(this);
        dashTop.setOrientation(LinearLayout.HORIZONTAL);
        dashTop.setGravity(Gravity.CENTER_VERTICAL);

        TextView dashTitle = text(
                "وردك اليوم",
                19,
                TEXT,
                true
        );

        dashTitle.setGravity(Gravity.RIGHT);

        dashTop.addView(
                dashTitle,
                new LinearLayout.LayoutParams(
                        0,
                        dp(32),
                        1f
                )
        );

        TextView dashBadge = text(
                "اليوم",
                11,
                EMERALD_DARK,
                true
        );

        dashBadge.setGravity(Gravity.CENTER);

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.rgb(232, 244, 237));
        badgeBg.setCornerRadius(dp(13));
        badgeBg.setStroke(
                dp(1),
                Color.rgb(211, 229, 218)
        );

        dashBadge.setBackground(badgeBg);

        dashTop.addView(
                dashBadge,
                new LinearLayout.LayoutParams(
                        dp(52),
                        dp(29)
                )
        );

        dashboard.addView(
                dashTop,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(34)
                )
        );

        // description
        TextView dashSubtitle = text(
                getDailyProgressMessage(),
                12,
                TEXT_SECONDARY,
                false
        );

        dashSubtitle.setGravity(Gravity.RIGHT);
        dashSubtitle.setTextDirection(View.TEXT_DIRECTION_RTL);

        dashboard.addView(
                dashSubtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(27)
                )
        );

        // progress area
        LinearLayout progressRow = new LinearLayout(this);
        progressRow.setOrientation(LinearLayout.HORIZONTAL);
        progressRow.setGravity(Gravity.CENTER_VERTICAL);

        int percent = getDailyProgressPercent();

        TextView progressPercent = text(
                percent + "%",
                27,
                EMERALD_DARK,
                true
        );

        progressPercent.setGravity(Gravity.CENTER);

        progressRow.addView(
                progressPercent,
                new LinearLayout.LayoutParams(
                        dp(65),
                        dp(58)
                )
        );

        LinearLayout progressHolder = new LinearLayout(this);
        progressHolder.setOrientation(LinearLayout.VERTICAL);
        progressHolder.setGravity(Gravity.CENTER_VERTICAL);

        // track
        GradientDrawable trackBg = new GradientDrawable();
        trackBg.setColor(Color.rgb(229, 236, 232));
        trackBg.setCornerRadius(dp(8));

        LinearLayout progressTrack = new LinearLayout(this);
        progressTrack.setBackground(trackBg);
        progressTrack.setClipChildren(true);

        GradientDrawable fillBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        EMERALD_DARK,
                        EMERALD,
                        GOLD
                }
        );

        fillBg.setCornerRadius(dp(8));

        View progressFill = new View(this);
        progressFill.setBackground(fillBg);

        int trackWidth = dp(225);
        int fillWidth = Math.max(
                dp(4),
                trackWidth * percent / 100
        );

        progressTrack.addView(
                progressFill,
                new LinearLayout.LayoutParams(
                        fillWidth,
                        dp(9)
                )
        );

        progressHolder.addView(
                progressTrack,
                new LinearLayout.LayoutParams(
                        trackWidth,
                        dp(9)
                )
        );

        TextView progressNumbers = text(
                getDailyProgressNumbers(),
                11,
                TEXT_SECONDARY,
                false
        );

        progressNumbers.setGravity(Gravity.RIGHT);
        progressNumbers.setTextDirection(View.TEXT_DIRECTION_RTL);

        progressHolder.addView(
                progressNumbers,
                new LinearLayout.LayoutParams(
                        trackWidth,
                        dp(25)
                )
        );

        progressRow.addView(
                progressHolder,
                new LinearLayout.LayoutParams(
                        0,
                        dp(58),
                        1f
                )
        );

        dashboard.addView(
                progressRow,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(63)
                )
        );

        // gold accent
        LinearLayout accentRow = new LinearLayout(this);
        accentRow.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        View goldLine = new View(this);
        goldLine.setBackgroundColor(GOLD);

        accentRow.addView(
                goldLine,
                new LinearLayout.LayoutParams(
                        dp(44),
                        dp(3)
                )
        );

        dashboard.addView(
                accentRow,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(12)
                )
        );

        content.addView(
                dashboard,
                dashboardParams
        );

        // =====================================================
        // SECTION HEADER
        // =====================================================
        LinearLayout sectionTitle = new LinearLayout(this);
        sectionTitle.setOrientation(LinearLayout.HORIZONTAL);
        sectionTitle.setGravity(Gravity.CENTER_VERTICAL);
        sectionTitle.setPadding(
                dp(4),
                dp(18),
                dp(4),
                dp(8)
        );

        TextView sectionName = text(
                "اختر وردك",
                20,
                TEXT,
                true
        );

        sectionName.setGravity(Gravity.RIGHT);
        sectionName.setTextDirection(View.TEXT_DIRECTION_RTL);

        sectionTitle.addView(
                sectionName,
                new LinearLayout.LayoutParams(
                        0,
                        dp(40),
                        1f
                )
        );

        TextView sectionHint = text(
                "٦ أقسام",
                11,
                TEXT_SECONDARY,
                false
        );

        sectionHint.setGravity(Gravity.CENTER);

        GradientDrawable sectionBadgeBg =
                new GradientDrawable();

        sectionBadgeBg.setColor(WHITE);
        sectionBadgeBg.setCornerRadius(dp(13));
        sectionBadgeBg.setStroke(dp(1), BORDER);

        sectionHint.setBackground(sectionBadgeBg);

        sectionTitle.addView(
                sectionHint,
                new LinearLayout.LayoutParams(
                        dp(65),
                        dp(30)
                )
        );

        content.addView(sectionTitle);

        // =====================================================
        // CATEGORY CARDS
        // =====================================================

        addPremiumCategory(
                content,
                "أذكار الصباح",
                "بداية يومك بذكر الله",
                "morning",
                "☀",
                Color.rgb(193, 151, 61)
        );

        addPremiumCategory(
                content,
                "أذكار المساء",
                "اختم يومك بذكر الله",
                "evening",
                "☾",
                Color.rgb(79, 105, 132)
        );

        addPremiumCategory(
                content,
                "أذكار بعد الصلاة",
                "أذكار ما بعد الصلوات",
                "after_prayer",
                "✦",
                EMERALD
        );

        addPremiumCategory(
                content,
                "أذكار النوم",
                "ما يقال قبل النوم",
                "sleep",
                "☽",
                Color.rgb(91, 83, 128)
        );

        addPremiumCategory(
                content,
                "أذكار الاستيقاظ",
                "ما يقال عند الاستيقاظ",
                "wake",
                "↑",
                Color.rgb(67, 126, 111)
        );

        addPremiumCategory(
                content,
                "أذكار متنوعة",
                "أذكار للحياة اليومية",
                "daily",
                "✧",
                GOLD
        );

        addPremiumCategory(
                content,
                "دعاء للمريض",
                "للدعاء بالشفاء",
                "patient",
                "✚",
                Color.rgb(64, 130, 105)
        );

        addPremiumCategory(
                content,
                "دخول المنزل",
                "ما يقال عند دخول المنزل",
                "home_enter",
                "⌂",
                Color.rgb(103, 124, 92)
        );

        addPremiumCategory(
                content,
                "الخروج من المنزل",
                "توكل على الله عند الخروج",
                "home_exit",
                "↗",
                Color.rgb(73, 119, 105)
        );

        addPremiumCategory(
                content,
                "دخول السوق",
                "ذكر دخول السوق",
                "market",
                "◆",
                Color.rgb(153, 118, 58)
        );

        addPremiumCategory(
                content,
                "ركوب السيارة",
                "ذكر الركوب",
                "ride",
                "⌁",
                Color.rgb(73, 111, 128)
        );

        addPremiumCategory(
                content,
                "دعاء السفر",
                "ما يقال عند السفر",
                "travel",
                "✈",
                Color.rgb(76, 104, 133)
        );

        addPremiumCategory(
                content,
                "الرزق",
                "دعاء طلب الكفاية والفضل",
                "rizq",
                "◈",
                Color.rgb(165, 129, 63)
        );

        addPremiumCategory(
                content,
                "الامتحان والتوفيق",
                "للتيسير وشرح الصدر",
                "exam",
                "✎",
                Color.rgb(84, 119, 104)
        );

        addPremiumCategory(
                content,
                "تسهيل الأمور",
                "دعاء التيسير",
                "ease",
                "✦",
                Color.rgb(112, 101, 137)
        );

        addPremiumCategory(
                content,
                "دعاء للميت",
                "الدعاء للميت بالرحمة والمغفرة",
                "deceased",
                "♡",
                Color.rgb(99, 105, 112)
        );

        addPremiumCategory(
                content,
                "يوم عرفة",
                "خير الدعاء في يوم عرفة",
                "arafah",
                "☁",
                Color.rgb(157, 126, 68)
        );

        addPremiumCategory(
                content,
                "مسافر لأهله",
                "دعاء الاستوداع",
                "traveler_family",
                "⇆",
                Color.rgb(77, 111, 126)
        );

        addPremiumCategory(
                content,
                "الكرب والهم",
                "عند الشدة والكرب",
                "distress",
                "♡",
                Color.rgb(119, 91, 112)
        );

        addPremiumCategory(
                content,
                "الحزن والهم",
                "دعاء تفريج الحزن والهم",
                "sadness",
                "☼",
                Color.rgb(100, 111, 130)
        );

        addPremiumCategory(
                content,
                "العين والحسد",
                "للتحصين من العين",
                "eye",
                "◉",
                Color.rgb(83, 119, 101)
        );

        addPremiumCategory(
                content,
                "دعوة ذي النون",
                "دعاء سيدنا يونس عليه السلام",
                "yunus",
                "۞",
                Color.rgb(76, 111, 126)
        );

        addPremiumCategory(
                content,
                "تحصين الأطفال",
                "دعاء التحصين للأطفال",
                "children",
                "♧",
                Color.rgb(102, 126, 91)
        );

        addPremiumCategory(
                content,
                "الاستخارة",
                "دعاء الاستخارة",
                "istikhara",
                "✧",
                Color.rgb(116, 97, 130)
        );

        addPremiumCategory(
                content,
                "لبس الثوب الجديد",
                "ما يقال عند لبس الجديد",
                "new_clothes",
                "◇",
                Color.rgb(144, 116, 63)
        );

        addPremiumCategory(
                content,
                "قبل الطعام",
                "ذكر الطعام",
                "food_before",
                "◌",
                Color.rgb(102, 123, 91)
        );

        addPremiumCategory(
                content,
                "بعد الطعام",
                "الحمد لله بعد الطعام",
                "food_after",
                "◌",
                Color.rgb(82, 117, 101)
        );

        addPremiumCategory(
                content,
                "دخول الخلاء",
                "ما يقال قبل الدخول",
                "toilet",
                "⌁",
                Color.rgb(102, 108, 118)
        );

        addPremiumCategory(
                content,
                "نزول المطر",
                "دعاء المطر",
                "rain",
                "☁",
                Color.rgb(76, 111, 135)
        );

        addPremiumCategory(
                content,
                "التوفيق والسداد",
                "دعاء الهداية والتقوى",
                "guidance",
                "✦",
                Color.rgb(76, 126, 104)
        );

        addPremiumCategory(
                content,
                "الزواج والزوج الصالح",
                "من أدعية القرآن",
                "family",
                "♡",
                Color.rgb(145, 99, 112)
        );

        addPremiumCategory(
                content,
                "دعاء الوالدين",
                "بر الوالدين بالدعاء",
                "parents",
                "♡",
                Color.rgb(132, 105, 76)
        );

        addPremiumCategory(
                content,
                "قضاء الدين",
                "دعاء قضاء الدين",
                "debt",
                "◇",
                Color.rgb(111, 117, 88)
        );

        // =====================================================
        // FOOTER
        // =====================================================
        TextView footer = text(
                "لعلها المنجيه  •  واذكر ربك إذا نسيت",
                11,
                TEXT_SECONDARY,
                false
        );

        footer.setGravity(Gravity.CENTER);
        footer.setTextDirection(View.TEXT_DIRECTION_RTL);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                );

        footerParams.setMargins(
                0,
                dp(14),
                0,
                0
        );

        content.addView(
                footer,
                footerParams
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

    // =========================================================
    // PREMIUM CATEGORY CARD
    // =========================================================

    private void addPremiumCategory(
            LinearLayout parent,
            String title,
            String subtitle,
            String category,
            String iconText,
            int accent
    ) {
        // =====================================================
        // EXISTING PROGRESS LOGIC — DO NOT CHANGE
        // =====================================================
        int[] stats = getCategoryProgressData(category);
        int total = stats[0];
        int percent = stats[1];
        boolean completed = percent >= 100;
        boolean started = percent > 0 && !completed;

        // =====================================================
        // PREMIUM CARD
        // =====================================================
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(10), dp(10), dp(10));

        int cardStroke = completed
                ? Color.rgb(178, 214, 198)
                : Color.rgb(225, 230, 226);

        int[] cardColors;

        if (completed) {
            cardColors = new int[]{
                    Color.rgb(242, 249, 245),
                    Color.WHITE
            };
        } else if (started) {
            cardColors = new int[]{
                    blendWithWhite(accent, 0.94f),
                    Color.WHITE
            };
        } else {
            cardColors = new int[]{
                    Color.WHITE,
                    Color.rgb(251, 252, 249)
            };
        }

        GradientDrawable cardBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                cardColors
        );

        cardBg.setCornerRadius(dp(22));
        cardBg.setStroke(dp(1), cardStroke);
        card.setBackground(cardBg);
        card.setElevation(dp(completed ? 5 : 3));

        // =====================================================
        // ICON
        // =====================================================
        LinearLayout iconHolder = new LinearLayout(this);
        iconHolder.setGravity(Gravity.CENTER);

        GradientDrawable iconBg = new GradientDrawable();

        if (completed) {
            iconBg.setColor(Color.rgb(220, 240, 230));
        } else if (started) {
            iconBg.setColor(blendWithWhite(accent, 0.86f));
        } else {
            iconBg.setColor(blendWithWhite(accent, 0.91f));
        }

        iconBg.setShape(GradientDrawable.OVAL);
        iconHolder.setBackground(iconBg);

        TextView icon = text(
                completed ? "✓" : iconText,
                completed ? 22 : 25,
                completed ? EMERALD : accent,
                true
        );

        icon.setGravity(Gravity.CENTER);
        iconHolder.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(58)
                )
        );

        card.addView(
                iconHolder,
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(62)
                )
        );

        // =====================================================
        // TEXT AREA
        // =====================================================
        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        texts.setPadding(dp(13), 0, dp(7), 0);

        // TITLE
        TextView titleView = text(
                title,
                17,
                TEXT,
                true
        );

        titleView.setGravity(
                Gravity.RIGHT | Gravity.CENTER_VERTICAL
        );

        texts.addView(
                titleView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(25)
                )
        );

        // SUBTITLE
        TextView subtitleView = text(
                subtitle,
                11,
                TEXT_SECONDARY,
                false
        );

        subtitleView.setGravity(
                Gravity.RIGHT | Gravity.CENTER_VERTICAL
        );

        texts.addView(
                subtitleView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(20)
                )
        );

        // =====================================================
        // STATUS + PERCENT
        // =====================================================
        LinearLayout statusRow = new LinearLayout(this);
        statusRow.setOrientation(LinearLayout.HORIZONTAL);
        statusRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView statusView;

        if (completed) {

            statusView = text(
                    "مكتمل ✓",
                    10,
                    EMERALD,
                    true
            );

        } else if (started) {

            statusView = text(
                    "قيد التقدم",
                    10,
                    accent,
                    true
            );

        } else {

            statusView = text(
                    "ابدأ الآن",
                    10,
                    TEXT_SECONDARY,
                    true
            );
        }

        statusView.setGravity(
                Gravity.RIGHT | Gravity.CENTER_VERTICAL
        );

        statusRow.addView(
                statusView,
                new LinearLayout.LayoutParams(
                        0,
                        dp(20),
                        1f
                )
        );

        TextView percentView = text(
                percent + "%",
                10,
                completed ? EMERALD : accent,
                true
        );

        percentView.setGravity(
                Gravity.LEFT | Gravity.CENTER_VERTICAL
        );

        statusRow.addView(
                percentView,
                new LinearLayout.LayoutParams(
                        dp(38),
                        dp(20)
                )
        );

        texts.addView(
                statusRow,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(20)
                )
        );

        // =====================================================
        // REAL PROGRESS BAR — SAME LOGIC
        // =====================================================
        android.widget.FrameLayout progressFrame =
                new android.widget.FrameLayout(this);

        GradientDrawable trackBg = new GradientDrawable();
        trackBg.setColor(Color.rgb(235, 239, 235));
        trackBg.setCornerRadius(dp(5));

        progressFrame.setBackground(trackBg);

        android.view.View progressFill =
                new android.view.View(this);

        GradientDrawable fillBg = new GradientDrawable();

        fillBg.setColor(
                completed
                        ? EMERALD
                        : accent
        );

        fillBg.setCornerRadius(dp(5));

        progressFill.setBackground(fillBg);

        int trackWidth = dp(150);

        int fillWidth;

        if (percent <= 0) {
            fillWidth = 0;
        } else {
            fillWidth = Math.max(
                    dp(4),
                    Math.round(
                            trackWidth * percent / 100f
                    )
            );
        }

        android.widget.FrameLayout.LayoutParams fillParams =
                new android.widget.FrameLayout.LayoutParams(
                        fillWidth,
                        dp(6)
                );

        fillParams.gravity =
                Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        progressFrame.addView(
                progressFill,
                fillParams
        );

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(10)
                );

        progressParams.setMargins(
                0,
                dp(1),
                0,
                0
        );

        texts.addView(
                progressFrame,
                progressParams
        );

        // =====================================================
        // ADD TEXT AREA
        // =====================================================
        card.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        dp(78),
                        1f
                )
        );

        // =====================================================
        // ARROW
        // =====================================================
        TextView arrow = text(
                completed ? "✓" : "‹",
                completed ? 18 : 29,
                completed ? EMERALD : accent,
                true
        );

        arrow.setGravity(
                Gravity.CENTER
        );

        if (!completed) {
            GradientDrawable arrowBg = new GradientDrawable();
            arrowBg.setColor(
                    blendWithWhite(accent, 0.93f)
            );
            arrowBg.setShape(GradientDrawable.OVAL);
            arrow.setBackground(arrowBg);
        }

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        dp(35),
                        dp(35)
                )
        );

        // =====================================================
        // CLICK — SAME LOGIC
        // =====================================================
        card.setOnClickListener(
                v -> openCategory(category)
        );

        // =====================================================
        // CARD MARGIN
        // =====================================================
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(100)
                );

        cardParams.setMargins(
                0,
                dp(5),
                0,
                dp(7)
        );

        parent.addView(
                card,
                cardParams
        );
    }

    // =========================================================
    // REAL CATEGORY PROGRESS
    // =========================================================

    // =========================================================
    // DAILY PROGRESS DASHBOARD
    // =========================================================

    private int getDailyProgressPercent() {

        int total = 0;
        int completed = 0;

        String[] categories = {
                "morning",
                "evening",
                "after_prayer",
                "sleep",
                "wake",
                "daily"
        };

        for (String category : categories) {

            currentAdhkar.clear();
            loadCategory(category);

            int size = currentAdhkar.size();

            if (size == 0) {
                continue;
            }

            total += size;

            int index =
                    adhkarPrefs.getInt(
                            category + "_INDEX",
                            0
                    );

            if (index >= size) {

                completed += size;

            } else if (index > 0) {

                completed += index;
            }

            // نحسب الجزء الذي أُنجز من الذكر الحالي
            if (index >= 0 && index < size) {

                int count =
                        adhkarPrefs.getInt(
                                category
                                        + "_COUNT_"
                                        + index,
                                0
                        );

                int target =
                        currentAdhkar
                                .get(index)
                                .count;

                if (target > 0 && count > 0) {

                    float fraction =
                            Math.min(
                                    1f,
                                    count / (float) target
                            );

                    completed += fraction;
                }
            }
        }

        if (total == 0) {
            return 0;
        }

        return Math.max(
                0,
                Math.min(
                        100,
                        Math.round(
                                (completed * 100f) / total
                        )
                )
        );
    }

    private String getDailyProgressNumbers() {

        int total = 0;
        int completed = 0;

        String[] categories = {
                "morning",
                "evening",
                "after_prayer",
                "sleep",
                "wake",
                "daily"
        };

        for (String category : categories) {

            currentAdhkar.clear();
            loadCategory(category);

            int size = currentAdhkar.size();

            if (size == 0) {
                continue;
            }

            total += size;

            int index =
                    adhkarPrefs.getInt(
                            category + "_INDEX",
                            0
                    );

            completed +=
                    Math.min(
                            index,
                            size
                    );
        }

        if (completed >= total && total > 0) {
            return total + " من " + total + " أذكار مكتملة";
        }

        return completed
                + " من "
                + total
                + " أذكار مكتملة";
    }

    private String getDailyProgressMessage() {

        int percent =
                getDailyProgressPercent();

        if (percent == 0) {

            return "ابدأ يومك بذكر الله، ولو بوردٍ واحد";

        } else if (percent < 25) {

            return "بداية طيبة، استمر وقرّب خطوتك إلى الله";

        } else if (percent < 50) {

            return "ما شاء الله، واصل وردك ولا تتوقف";

        } else if (percent < 75) {

            return "أحسنت، قطعت أكثر من نصف الطريق";

        } else if (percent < 100) {

            return "اقتربت من إكمال وردك، بارك الله فيك";

        } else {

            return "ما شاء الله، أتممت جميع الأذكار ✓";
        }
    }

    private int[] getCategoryProgressData(String category) {

        currentAdhkar.clear();
        loadCategory(category);

        int total = currentAdhkar.size();

        if (total == 0 || adhkarPrefs == null) {
            return new int[]{0, 0};
        }

        int index =
                adhkarPrefs.getInt(
                        category + "_INDEX",
                        0
                );

        if (index >= total) {
            return new int[]{total, 100};
        }

        if (index < 0) {
            index = 0;
        }

        float completed = index;

        int currentCount =
                adhkarPrefs.getInt(
                        category
                                + "_COUNT_"
                                + index,
                        0
                );

        if (index < total) {

            int target =
                    currentAdhkar
                            .get(index)
                            .count;

            if (target > 0) {

                float fraction =
                        Math.min(
                                1f,
                                currentCount
                                        / (float) target
                        );

                completed += fraction;
            }
        }

        int percent =
                Math.round(
                        (completed / total)
                                * 100f
                );

        percent =
                Math.max(
                        0,
                        Math.min(
                                100,
                                percent
                        )
                );

        return new int[]{
                total,
                percent
        };
    }

    private String getCategoryProgressText(String category) {

        if (adhkarPrefs == null) {
            return "ابدأ وردك الآن";
        }

        currentAdhkar.clear();
        loadCategory(category);

        int total = currentAdhkar.size();
        int index = adhkarPrefs.getInt(
                category + "_INDEX",
                0
        );

        int completed = index;

        if (index < total) {
            int count = adhkarPrefs.getInt(
                    category + "_COUNT_" + index,
                    0
            );

            if (count > 0) {
                completed = index;
            }
        }

        if (index >= total) {
            return "تم إكمال الورد ✓";
        }

        if (completed == 0) {
            return total + " أذكار • ابدأ الآن";
        }

        return "أكملت " + completed + " من " + total;
    }

    private int blendWithWhite(int color, float amount) {

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        r = (int)(r * (1f - amount) + 255f * amount);
        g = (int)(g * (1f - amount) + 255f * amount);
        b = (int)(b * (1f - amount) + 255f * amount);

        return Color.rgb(r, g, b);
    }

    private void addCategory(
            LinearLayout parent,
            String title,
            String subtitle,
            String category
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(
                dp(16),
                dp(12),
                dp(14),
                dp(12)
        );

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(WHITE);
        bg.setCornerRadius(dp(22));
        bg.setStroke(dp(1), BORDER);

        card.setBackground(bg);
        card.setElevation(dp(3));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setGravity(Gravity.RIGHT);

        TextView titleView = text(
                title,
                18,
                TEXT,
                true
        );
        titleView.setGravity(Gravity.RIGHT);

        TextView subtitleView = text(
                subtitle,
                12,
                TEXT_SECONDARY,
                false
        );
        subtitleView.setGravity(Gravity.RIGHT);

        texts.addView(titleView);
        texts.addView(subtitleView);

        card.addView(
                texts,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        TextView arrow = text(
                "‹",
                30,
                EMERALD,
                true
        );
        arrow.setGravity(Gravity.CENTER);

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        dp(40),
                        dp(55)
                )
        );

        card.setOnClickListener(v ->
                openCategory(category)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(82)
                );

        params.setMargins(
                0,
                0,
                0,
                dp(12)
        );

        parent.addView(card, params);
    }

    // =========================================================
    // HEADER
    // =========================================================

    private LinearLayout buildHeader(
            String titleText,
            boolean closeActivity
    ) {

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(
                dp(16),
                dp(10),
                dp(16),
                dp(10)
        );

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(WHITE);
        bg.setCornerRadius(dp(24));
        bg.setStroke(dp(1), BORDER);

        header.setBackground(bg);
        header.setElevation(dp(6));

        TextView back = text(
                "‹",
                34,
                EMERALD_DARK,
                true
        );

        back.setGravity(Gravity.CENTER);

        back.setOnClickListener(v -> {
            if (closeActivity) {
                finish();
            } else {
                buildHome();
            }
        });

        header.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(55)
                )
        );

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setGravity(Gravity.RIGHT);

        TextView title = text(
                titleText,
                22,
                TEXT,
                true
        );
        title.setGravity(Gravity.RIGHT);

        TextView subtitle = text(
                "اذكر الله تطمئن القلوب",
                12,
                TEXT_SECONDARY,
                false
        );
        subtitle.setGravity(Gravity.RIGHT);

        titleBox.addView(title);
        titleBox.addView(subtitle);

        header.addView(
                titleBox,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        TextView icon = text(
                "۞",
                27,
                GOLD,
                true
        );
        icon.setGravity(Gravity.CENTER);

        header.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(55)
                )
        );

        return header;
    }

    // =========================================================
    // OPEN CATEGORY
    // =========================================================

    private void openCategory(String category) {

        currentCategory = category;
        currentAdhkar.clear();

        loadCategory(category);

        currentIndex = adhkarPrefs.getInt(
                category + "_INDEX",
                0
        );

        currentCount = adhkarPrefs.getInt(
                category + "_COUNT_" + currentIndex,
                0
        );

        if (currentIndex >= currentAdhkar.size()) {
            currentIndex = 0;
            currentCount = 0;
        }

        showDhikr();
    }

    // =========================================================
    // DHIKR DATA
    // =========================================================

    private void loadCategory(String category) {
        if (category.equals("morning")) {
            loadMorning();
        } else if (category.equals("evening")) {
            loadEvening();
        } else if (category.equals("after_prayer")) {
            loadAfterPrayer();
        } else if (category.equals("sleep")) {
            loadSleep();
        } else if (category.equals("wake")) {
            loadWake();
        } else if (category.equals("daily")) {
            loadDaily();
        } else if (category.equals("patient")) {
            loadPatient();
        } else if (category.equals("home_enter")) {
            loadHomeEnter();
        } else if (category.equals("home_exit")) {
            loadHomeExit();
        } else if (category.equals("market")) {
            loadMarket();
        } else if (category.equals("ride")) {
            loadRide();
        } else if (category.equals("travel")) {
            loadTravel();
        } else if (category.equals("rizq")) {
            loadRizq();
        } else if (category.equals("exam")) {
            loadExam();
        } else if (category.equals("ease")) {
            loadEase();
        } else if (category.equals("deceased")) {
            loadDeceased();
        } else if (category.equals("arafah")) {
            loadArafah();
        } else if (category.equals("traveler_family")) {
            loadTravelerFamily();
        } else if (category.equals("distress")) {
            loadDistress();
        } else if (category.equals("sadness")) {
            loadSadness();
        } else if (category.equals("eye")) {
            loadEye();
        } else if (category.equals("yunus")) {
            loadYunus();
        } else if (category.equals("children")) {
            loadChildren();
        } else if (category.equals("istikhara")) {
            loadIstikhara();
        } else if (category.equals("new_clothes")) {
            loadNewClothes();
        } else if (category.equals("food_before")) {
            loadFoodBefore();
        } else if (category.equals("food_after")) {
            loadFoodAfter();
        } else if (category.equals("toilet")) {
            loadToilet();
        } else if (category.equals("rain")) {
            loadRain();
        } else if (category.equals("guidance")) {
            loadGuidance();
        } else if (category.equals("family")) {
            loadFamily();
        } else if (category.equals("parents")) {
            loadParents();
        } else if (category.equals("debt")) {
            loadDebt();
        }
    }

    private void loadMorning() {

        add(
                "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ "
                + "لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ "
                + "لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ "
                + "مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ "
                + "يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ "
                + "وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ "
                + "وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ "
                + "وَلَا يَئُودُهُ حِفْظُهُمَا ۚ "
                + "وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                1,
                "القرآن الكريم — سورة البقرة، الآية 255",
                "آية قرآنية"
        );

        add(
                "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ "
                + "اللَّهُ الصَّمَدُ ۝ "
                + "لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ "
                + "وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ",
                3,
                "سنن أبي داود وجامع الترمذي",
                "حسن صحيح"
        );

        add(
                "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ "
                + "مِنْ شَرِّ مَا خَلَقَ ۝ "
                + "وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ "
                + "وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ "
                + "وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ",
                3,
                "سنن أبي داود وجامع الترمذي",
                "حسن صحيح"
        );

        add(
                "قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ "
                + "مَلِكِ النَّاسِ ۝ "
                + "إِلَٰهِ النَّاسِ ۝ "
                + "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ "
                + "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ "
                + "مِنَ الْجِنَّةِ وَالنَّاسِ",
                3,
                "سنن أبي داود وجامع الترمذي",
                "حسن صحيح"
        );

        add(
                "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، "
                + "وَالْحَمْدُ لِلَّهِ، "
                + "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
                + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، "
                + "وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                1,
                "سنن أبي داود، حديث 5071",
                "صحيح"
        );

        add(
                "اللَّهُمَّ بِكَ أَصْبَحْنَا، "
                + "وَبِكَ أَمْسَيْنَا، "
                + "وَبِكَ نَحْيَا، "
                + "وَبِكَ نَمُوتُ، "
                + "وَإِلَيْكَ النُّشُورُ",
                1,
                "جامع الترمذي، حديث 3391",
                "حسن"
        );

        add(
                "رَضِيتُ بِاللَّهِ رَبًّا، "
                + "وَبِالْإِسْلَامِ دِينًا، "
                + "وَبِمُحَمَّدٍ ﷺ نَبِيًّا",
                3,
                "سنن أبي داود، حديث 5072",
        "مختلف في صحته — ضعفه الألباني"
        );

        add(
                "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، "
                + "خَلَقْتَنِي وَأَنَا عَبْدُكَ، "
                + "وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، "
                + "أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، "
                + "أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، "
                + "وَأَبُوءُ لَكَ بِذَنْبِي، "
                + "فَاغْفِرْ لِي، "
                + "فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                1,
                "صحيح البخاري، حديث 6306",
                "صحيح"
        );

        add(
                "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ "
                + "فِي الْأَرْضِ وَلَا فِي السَّمَاءِ، "
                + "وَهُوَ السَّمِيعُ الْعَلِيمُ",
                3,
                "جامع الترمذي، حديث 3388؛ سنن أبي داود، حديث 5088",
                "حسن صحيح"
        );

        add(
                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                100,
                "صحيح مسلم، حديث 2692",
                "صحيح"
        );

        add(
                "رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ "
                + "وَخَيْرَ مَا بَعْدَهُ، "
                + "وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ "
                + "وَشَرِّ مَا بَعْدَهُ، "
                + "رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ "
                + "وَمِنْ سُوءِ الْكِبَرِ، "
                + "وَرَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ "
                + "وَعَذَابٍ فِي الْقَبْرِ",
                1,
                "سنن أبي داود، حديث 5071",
                "صحيح"
        );

        add(
                "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، "
                + "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ "
                + "فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، "
                + "اللَّهُمَّ اسْتُرْ عَوْرَاتِي، وَآمِنْ رَوْعَاتِي، "
                + "اللَّهُمَّ احْفَظْنِي مِنْ بَيْنِ يَدَيَّ وَمِنْ خَلْفِي، "
                + "وَعَنْ يَمِينِي وَعَنْ شِمَالِي وَمِنْ فَوْقِي، "
                + "وَأَعُوذُ بِعَظَمَتِكَ أَنْ أُغْتَالَ مِنْ تَحْتِي",
                1,
                "سنن أبي داود، حديث 5074",
                "صحيح"
        );
    }

    private void loadEvening() {

        add(
                "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ "
                + "لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ "
                + "لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ "
                + "مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ "
                + "يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ "
                + "وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ "
                + "وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ "
                + "وَلَا يَئُودُهُ حِفْظُهُمَا ۚ "
                + "وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                1,
                "القرآن الكريم — سورة البقرة، الآية 255",
                "آية قرآنية"
        );

        add(
                "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ "
                + "اللَّهُ الصَّمَدُ ۝ "
                + "لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ "
                + "وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ",
                3,
                "سنن أبي داود وجامع الترمذي",
                "حسن صحيح"
        );

        add(
                "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ "
                + "مِنْ شَرِّ مَا خَلَقَ ۝ "
                + "وَمِنْ شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ "
                + "وَمِنْ شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ "
                + "وَمِنْ شَرِّ حَاسِدٍ إِذَا حَسَدَ",
                3,
                "سنن أبي داود وجامع الترمذي",
                "حسن صحيح"
        );

        add(
                "قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ "
                + "مَلِكِ النَّاسِ ۝ "
                + "إِلَٰهِ النَّاسِ ۝ "
                + "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ "
                + "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ "
                + "مِنَ الْجِنَّةِ وَالنَّاسِ",
                3,
                "سنن أبي داود وجامع الترمذي",
                "حسن صحيح"
        );

        add(
                "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، "
                + "وَالْحَمْدُ لِلَّهِ، "
                + "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
                + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، "
                + "وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                1,
                "سنن أبي داود، حديث 5071",
                "صحيح"
        );

        add(
                "اللَّهُمَّ بِكَ أَمْسَيْنَا، "
                + "وَبِكَ أَصْبَحْنَا، "
                + "وَبِكَ نَحْيَا، "
                + "وَبِكَ نَمُوتُ، "
                + "وَإِلَيْكَ الْمَصِيرُ",
                1,
                "جامع الترمذي، حديث 3391",
                "حسن"
        );

        add(
                "رَضِيتُ بِاللَّهِ رَبًّا، "
                + "وَبِالْإِسْلَامِ دِينًا، "
                + "وَبِمُحَمَّدٍ ﷺ نَبِيًّا",
                3,
                "جامع الترمذي، حديث 3389",
        "حسن غريب — قال الترمذي: حسن غريب"
        );

        add(
                "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، "
                + "خَلَقْتَنِي وَأَنَا عَبْدُكَ، "
                + "وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، "
                + "أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، "
                + "أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، "
                + "وَأَبُوءُ لَكَ بِذَنْبِي، "
                + "فَاغْفِرْ لِي، "
                + "فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                1,
                "صحيح البخاري، حديث 6306",
                "صحيح"
        );

        add(
                "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ "
                + "فِي الْأَرْضِ وَلَا فِي السَّمَاءِ، "
                + "وَهُوَ السَّمِيعُ الْعَلِيمُ",
                3,
                "جامع الترمذي، حديث 3388؛ سنن أبي داود، حديث 5088",
                "حسن صحيح"
        );

        add(
                "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ "
                + "مِنْ شَرِّ مَا خَلَقَ",
                3,
                "جامع الترمذي، حديث 3604b",
                "ورد في السنة — ثلاث مرات مساءً"
        );

        add(
                "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                100,
                "صحيح مسلم، حديث 2692",
                "صحيح"
        );

        add(
                "رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ "
                + "وَخَيْرَ مَا بَعْدَهَا، "
                + "وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ "
                + "وَشَرِّ مَا بَعْدَهَا، "
                + "رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ "
                + "وَمِنْ سُوءِ الْكِبَرِ، "
                + "وَرَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ "
                + "وَعَذَابٍ فِي الْقَبْرِ",
                1,
                "سنن أبي داود، حديث 5071",
                "صحيح"
        );

        add(
                "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، "
                + "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ "
                + "فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، "
                + "اللَّهُمَّ اسْتُرْ عَوْرَاتِي، وَآمِنْ رَوْعَاتِي، "
                + "اللَّهُمَّ احْفَظْنِي مِنْ بَيْنِ يَدَيَّ وَمِنْ خَلْفِي، "
                + "وَعَنْ يَمِينِي وَعَنْ شِمَالِي وَمِنْ فَوْقِي، "
                + "وَأَعُوذُ بِعَظَمَتِكَ أَنْ أُغْتَالَ مِنْ تَحْتِي",
                1,
                "سنن أبي داود، حديث 5074",
                "صحيح"
        );
    }

    private void loadAfterPrayer() {

        add(
                "أَسْتَغْفِرُ اللَّهَ",
                3,
                "صحيح مسلم، حديث 591",
                "صحيح"
        );

        add(
                "اللَّهُمَّ أَنْتَ السَّلَامُ، "
                + "وَمِنْكَ السَّلَامُ، "
                + "تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
                1,
                "صحيح مسلم، حديث 591",
                "صحيح"
        );

        add(
                "سُبْحَانَ اللَّهِ",
                33,
                "صحيح مسلم، حديث 595",
                "صحيح"
        );

        add(
                "الْحَمْدُ لِلَّهِ",
                33,
                "صحيح مسلم، حديث 595",
                "صحيح"
        );

        add(
                "اللَّهُ أَكْبَرُ",
                33,
                "صحيح مسلم، حديث 595",
                "صحيح"
        );

        add(
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
                + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، "
                + "وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                1,
                "صحيح مسلم، حديث 595",
                "صحيح"
        );

        add(
                "قُلْ هُوَ اللَّهُ أَحَدٌ\n"
                + "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ\n"
                + "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                1,
                "سنن أبي داود، حديث 1523",
                "صحيح — صححه الألباني"
        );

        add(
                "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ، "
                + "وَشُكْرِكَ، وَحُسْنِ عِبَادَتِكَ",
                1,
                "سنن أبي داود، حديث 1522",
                "صحيح — صححه الألباني"
        );

        add(
                "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
                + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، "
                + "وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، "
                + "اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، "
                + "وَلَا مُعْطِيَ لِمَا مَنَعْتَ، "
                + "وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
                1,
                "صحيح البخاري، حديث 844",
                "صحيح"
        );

        add(
                "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ، "
                + "لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ، "
                + "لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ، "
                + "مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ، "
                + "يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ، "
                + "وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ، "
                + "وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ، "
                + "وَلَا يَئُودُهُ حِفْظُهُمَا، "
                + "وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                1,
                "القرآن الكريم، سورة البقرة، آية 255",
                "آية قرآنية — وردت رواية في فضل قراءتها بعد الفريضة"
        );
    }

    private void loadSleep() {
    add(
        "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
        1,
        "صحيح البخاري، حديث 6324",
        "صحيح"
    );

    add(
        "سُبْحَانَ اللَّهِ",
        33,
        "صحيح البخاري، حديث 6318؛ وصحيح مسلم، حديث 2728",
        "صحيح — من أذكار النوم"
    );

    add(
        "الْحَمْدُ لِلَّهِ",
        33,
        "صحيح البخاري، حديث 6318؛ وصحيح مسلم، حديث 2728",
        "صحيح — من أذكار النوم"
    );

    add(
        "اللَّهُ أَكْبَرُ",
        34,
        "صحيح البخاري، حديث 3705؛ وصحيح مسلم، حديث 2728",
        "صحيح — من أذكار النوم"
    );

    add(
        "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
        3,
        "سنن أبي داود، حديث 5045",
        "صحيح أصل الحديث — وردت رواية بثلاث مرات، والألباني صحح الحديث دون لفظ العدد"
    );

    add(
        "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، "
        + "إِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، "
        + "وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
        1,
        "صحيح البخاري، حديث 6320",
        "صحيح"
    );

    add(
        "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ "
        + "لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ "
        + "لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ "
        + "مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ "
        + "يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ "
        + "وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ "
        + "وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ "
        + "وَلَا يَئُودُهُ حِفْظُهُمَا ۚ "
        + "وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        1,
        "صحيح البخاري، حديث 2311",
        "صحيح — ورد فضل قراءتها عند النوم"
    );

    add(
        "آمَنَ الرَّسُولُ بِمَا أُنْزِلَ إِلَيْهِ مِنْ رَبِّهِ وَالْمُؤْمِنُونَ ۚ "
        + "كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ "
        + "لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِنْ رُسُلِهِ ۚ "
        + "وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ ۝ "
        + "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ "
        + "لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ "
        + "رَبَّنَا لَا تُؤَاخِذْنَا إِنْ نَسِينَا أَوْ أَخْطَأْنَا ۚ "
        + "رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِنْ قَبْلِنَا ۚ "
        + "رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ "
        + "وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ "
        + "أَنْتَ مَوْلَانَا فَانْصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
        1,
        "صحيح البخاري، حديث 5040",
        "آيتان من آخر سورة البقرة — صحيح"
    );

    add(
        "قُلْ هُوَ اللَّهُ أَحَدٌ، قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ، "
        + "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
        3,
        "صحيح البخاري، حديث 6319؛ وصحيح مسلم",
        "صحيح — من أذكار النوم"
    );
}
    private void loadWake() {
        add(
            "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا "
            + "بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            1,
            "صحيح البخاري، حديث 6324",
            "صحيح"
        );

        add(
            "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
            + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، "
            + "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، "
            + "وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ، "
            + "وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ، "
            + "ثُمَّ قَالَ: رَبِّ اغْفِرْ لِي",
            1,
            "سنن أبي داود، حديث 5060",
            "صحيح — صححه الألباني"
        );

        add(
            "الْحَمْدُ لِلَّهِ الَّذِي عَافَانِي فِي جَسَدِي، "
            + "وَرَدَّ عَلَيَّ رُوحِي، وَأَذِنَ لِي بِذِكْرِهِ",
            1,
            "جامع الترمذي، حديث 3401",
            "حسن — قال الترمذي: حديث حسن"
        );
    }

    private void loadDaily() {
        add(
            "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            100,
            "صحيح البخاري وصحيح مسلم",
            "صحيح — العدد هنا للورد داخل التطبيق"
        );

        add(
            "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
            + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            100,
            "صحيح البخاري وصحيح مسلم",
            "صحيح — العدد هنا للورد داخل التطبيق"
        );

        add(
            "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            100,
            "صحيح البخاري وصحيح مسلم",
            "ثابت — العدد هنا للورد داخل التطبيق"
        );

        add(
            "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            100,
            "صحيح البخاري وصحيح مسلم",
            "ثابت — العدد هنا للورد داخل التطبيق"
        );

        add(
            "سُبْحَانَ اللَّهِ الْعَظِيمِ وَبِحَمْدِهِ",
            1,
            "صحيح البخاري وصحيح مسلم",
            "صحيح — ورد فضل الذكر، والعدد هنا للورد داخل التطبيق"
        );

        add(
            "سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ، "
            + "وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ",
            1,
            "صحيح مسلم، حديث 2137",
            "صحيح"
        );

        add(
            "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
            10,
            "صحيح مسلم، حديث 408؛ سنن النسائي، حديث 1296",
            "ثابت — العدد 10 وردًا مقترحًا داخل التطبيق وليس عددًا يوميًا مخصوصًا"
        );
    }


private void loadPatient() {
        add(
                "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَأْسَ، اشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
                1,
                "صحيح البخاري وصحيح مسلم",
                "صحيح"
        );
    }

    private void loadHomeEnter() {
    add(
        "اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ الْمَوْلِجِ، "
        + "وَخَيْرَ الْمَخْرَجِ، بِسْمِ اللَّهِ وَلَجْنَا، "
        + "وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا، "
        + "ثُمَّ لِيُسَلِّمْ عَلَى أَهْلِهِ",
        1,
        "سنن أبي داود، حديث 5096",
        "مختلف في صحته — حسنه ابن حجر وابن مفلح، وضعفه الألباني"
    );
}

    private void loadHomeExit() {
        add(
                "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، "
                + "وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                1,
                "سنن أبي داود، حديث 5095",
                "صحيح"
        );

        add(
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ أَنْ أَضِلَّ أَوْ أُضَلَّ، "
                + "أَوْ أَزِلَّ أَوْ أُزَلَّ، "
                + "أَوْ أَظْلِمَ أَوْ أُظْلَمَ، "
                + "أَوْ أَجْهَلَ أَوْ يُجْهَلَ عَلَيَّ",
                1,
                "سنن أبي داود، حديث 5094",
                "صحيح — صححه الألباني"
        );
    }

    private void loadMarket() {
    add(
        "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
        + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِي وَيُمِيتُ "
        + "وَهُوَ حَيٌّ لَا يَمُوتُ بِيَدِهِ الْخَيْرُ "
        + "وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        1,
        "جامع الترمذي، حديث 3428؛ سنن ابن ماجه، حديث 2235",
        "حسن عند الألباني — مع وجود خلاف في تصحيحه"
    );
}

    private void loadRide() {
        add(
                "لِتَسْتَوُوا عَلَىٰ ظُهُورِهِ ثُمَّ تَذْكُرُوا نِعْمَةَ رَبِّكُمْ إِذَا اسْتَوَيْتُمْ عَلَيْهِ "
                + "وَتَقُولُوا سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ "
                + "وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
                1,
                "القرآن الكريم — سورة الزخرف، الآيتان 13-14",
                "آيات قرآنية"
        );
    }

    private void loadTravel() {
        add(
                "اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ، "
                + "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ "
                + "وَإِنَّا إِلَى رَبِّنَا لَمُنقَلِبُونَ، "
                + "اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى، "
                + "وَمِنَ الْعَمَلِ مَا تَرْضَى، "
                + "اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَذَا وَاطْوِ عَنَّا بُعْدَهُ، "
                + "اللَّهُمَّ أَنْتَ الصَّاحِبُ فِي السَّفَرِ، وَالْخَلِيفَةُ فِي الْأَهْلِ، "
                + "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ وَعْثَاءِ السَّفَرِ، "
                + "وَكَآبَةِ الْمَنْظَرِ، وَسُوءِ الْمُنقَلَبِ فِي الْمَالِ وَالْأَهْلِ",
                1,
                "صحيح مسلم، حديث 1342",
                "صحيح"
        );

        add(
                "آيِبُونَ تَائِبُونَ عَابِدُونَ لِرَبِّنَا حَامِدُونَ",
                1,
                "صحيح مسلم، حديث 1342",
                "صحيح"
        );
    }

    private void loadRizq() {
        add(
                "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
                1,
                "جامع الترمذي، حديث 3563",
                "حسن غريب — قال الترمذي: حسن غريب"
        );

        add(
                "رَبِّ إِنِّي لِمَا أَنْزَلْتَ إِلَيَّ مِنْ خَيْرٍ فَقِيرٌ",
                1,
                "القرآن الكريم — سورة القصص، الآية 24",
                "آية قرآنية"
        );
    }

    private void loadExam() {
        add(
                "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِنْ لِسَانِي يَفْقَهُوا قَوْلِي",
                1,
                "القرآن الكريم — سورة طه، الآيات 25-28",
                "آيات قرآنية"
        );

        add(
                "رَبِّ زِدْنِي عِلْمًا",
                1,
                "القرآن الكريم — سورة طه، الآية 114",
                "آية قرآنية"
        );
    }

    private void loadEase() {
    add(
        "اللَّهُمَّ لَا سَهْلَ إِلَّا مَا جَعَلْتَهُ سَهْلًا، "
        + "وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلًا",
        1,
        "صحيح ابن حبان، حديث 974",
        "صحيح — صححه الألباني، وقال ابن حبان: أخرجه في صحيحه"
    );

    add(
        "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
        1,
        "القرآن الكريم — سورة الشرح، الآيتان 5-6",
        "آيات قرآنية"
    );
}

    private void loadDeceased() {
        add(
                "اللَّهُمَّ اغْفِرْ لَهُ وَارْحَمْهُ وَعَافِهِ وَاعْفُ عَنْهُ، "
                + "وَأَكْرِمْ نُزُلَهُ وَوَسِّعْ مُدْخَلَهُ، "
                + "وَاغْسِلْهُ بِالْمَاءِ وَالثَّلْجِ وَالْبَرَدِ، "
                + "وَنَقِّهِ مِنَ الْخَطَايَا كَمَا نَقَّيْتَ الثَّوْبَ الْأَبْيَضَ مِنَ الدَّنَسِ، "
                + "اللَّهُمَّ أَبْدِلْهُ دَارًا خَيْرًا مِنْ دَارِهِ، "
                + "وَأَهْلًا خَيْرًا مِنْ أَهْلِهِ، وَزَوْجًا خَيْرًا مِنْ زَوْجِهِ، "
                + "وَأَدْخِلْهُ الْجَنَّةَ، وَأَعِذْهُ مِنْ عَذَابِ الْقَبْرِ وَمِنْ عَذَابِ النَّارِ",
                1,
                "صحيح مسلم، حديث 963",
                "صحيح"
        );

        add(
                "اللَّهُمَّ اغْفِرْ لِحَيِّنَا وَمَيِّتِنَا، وَشَاهِدِنَا وَغَائِبِنَا، "
                + "وَصَغِيرِنَا وَكَبِيرِنَا، وَذَكَرِنَا وَأُنْثَانَا، "
                + "اللَّهُمَّ مَنْ أَحْيَيْتَهُ مِنَّا فَأَحْيِهِ عَلَى الْإِيمَانِ، "
                + "وَمَنْ تَوَفَّيْتَهُ مِنَّا فَتَوَفَّهُ عَلَى الْإِسْلَامِ، "
                + "اللَّهُمَّ لَا تَحْرِمْنَا أَجْرَهُ وَلَا تُضِلَّنَا بَعْدَهُ",
                1,
                "سنن أبي داود، حديث 3201؛ ورواه الترمذي بعد حديث 1024",
                "صحيح — صححه الألباني"
        );
    }

    private void loadArafah() {
    add(
        "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، "
        + "لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        1,
        "جامع الترمذي، حديث 3585؛ ومسند أحمد، حديث 6961",
        "حسن بالشواهد عند عدد من أهل العلم — وأصل رواية الترمذي غريب"
    );
}

    private void loadTravelerFamily() {
    add(
        "أَسْتَوْدِعُكُمُ اللَّهَ الَّذِي لَا تَضِيعُ وَدَائِعُهُ",
        1,
        "سنن ابن ماجه، حديث 2825؛ السنن الكبرى للنسائي، حديث 10342",
        "صحيح — صححه الألباني في صحيح ابن ماجه"
    );
}

    private void loadDistress() {
        add(
                "لَا إِلَهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ السَّمَاوَاتِ وَرَبُّ الْأَرْضِ وَرَبُّ الْعَرْشِ الْكَرِيمِ",
                1,
                "صحيح البخاري وصحيح مسلم",
                "صحيح"
        );
    }

    private void loadSadness() {
        add(
            "اللَّهُمَّ إِنِّي عَبْدُكَ ابْنُ عَبْدِكَ ابْنُ أَمَتِكَ، "
            + "نَاصِيَتِي بِيَدِكَ، مَاضٍ فِيَّ حُكْمُكَ، عَدْلٌ فِيَّ قَضَاؤُكَ، "
            + "أَسْأَلُكَ بِكُلِّ اسْمٍ هُوَ لَكَ سَمَّيْتَ بِهِ نَفْسَكَ "
            + "أَوْ أَنْزَلْتَهُ فِي كِتَابِكَ أَوْ عَلَّمْتَهُ أَحَدًا مِنْ خَلْقِكَ "
            + "أَوِ اسْتَأْثَرْتَ بِهِ فِي عِلْمِ الْغَيْبِ عِنْدَكَ "
            + "أَنْ تَجْعَلَ الْقُرْآنَ رَبِيعَ قَلْبِي وَنُورَ صَدْرِي "
            + "وَجَلَاءَ حُزْنِي وَذَهَابَ هَمِّي",
            1,
            "مسند أحمد، حديث 3712؛ صحيح ابن حبان، حديث 972",
            "صحيح — صححه الألباني وشعيب الأرناؤوط"
        );
    }

    private void loadEye() {
        add(
                "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّةِ مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ وَمِنْ كُلِّ عَيْنٍ لَامَّةٍ",
                1,
                "صحيح البخاري، حديث 3371",
                "صحيح"
        );
    }

    private void loadYunus() {
        add(
                "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                1,
                "القرآن الكريم — سورة الأنبياء، الآية 87",
                "آية قرآنية"
        );
    }

    private void loadChildren() {
        add(
                "أُعِيذُكُمَا بِكَلِمَاتِ اللَّهِ التَّامَّةِ، مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ، وَمِنْ كُلِّ عَيْنٍ لَامَّةٍ",
                1,
                "صحيح البخاري، حديث 3371",
                "صحيح"
        );
    }

    private void loadIstikhara() {
        add(
                "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ. اللَّهُمَّ إِنْ كُنْتَ تَعْلَمُ أَنَّ هَذَا الْأَمْرَ خَيْرٌ لِي فِي دِينِي وَمَعَاشِي وَعَاقِبَةِ أَمْرِي فَاقْدُرْهُ لِي وَيَسِّرْهُ لِي ثُمَّ بَارِكْ لِي فِيهِ، وَإِنْ كُنْتَ تَعْلَمُ أَنَّ هَذَا الْأَمْرَ شَرٌّ لِي فِي دِينِي وَمَعَاشِي وَعَاقِبَةِ أَمْرِي فَاصْرِفْهُ عَنِّي وَاصْرِفْنِي عَنْهُ وَاقْدُرْ لِيَ الْخَيْرَ حَيْثُ كَانَ ثُمَّ أَرْضِنِي بِهِ",
                1,
                "صحيح البخاري، حديث 1166",
                "صحيح"
        );
    }

    private void loadNewClothes() {
        add(
            "اللَّهُمَّ لَكَ الْحَمْدُ أَنْتَ كَسَوْتَنِيهِ، "
            + "أَسْأَلُكَ مِنْ خَيْرِهِ وَخَيْرِ مَا صُنِعَ لَهُ، "
            + "وَأَعُوذُ بِكَ مِنْ شَرِّهِ وَشَرِّ مَا صُنِعَ لَهُ",
            1,
            "سنن أبي داود، حديث 4020",
            "صحيح — صححه الألباني"
        );
    }

    private void loadFoodBefore() {
        add(
            "بِسْمِ اللَّهِ",
            1,
            "صحيح البخاري، حديث 5376؛ وصحيح مسلم، حديث 2022 — التسمية عند الطعام",
            "صحيح"
        );

        add(
            "بِسْمِ اللَّهِ فِي أَوَّلِهِ وَآخِرِهِ",
            1,
            "سنن أبي داود، حديث 3767",
            "صحيح — صححه الألباني"
        );
    }

    private void loadFoodAfter() {
        add(
                "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا الطَّعَامَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                1,
                "سنن أبي داود، حديث 4023",
                "حسن — دون زيادة: وما تأخر"
        );
    }

    private void loadToilet() {
        add(
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
                1,
                "صحيح البخاري وصحيح مسلم",
                "صحيح"
        );
    }

    private void loadRain() {
        add(
                "اللَّهُمَّ صَيِّبًا نَافِعًا",
                1,
                "صحيح البخاري، حديث 1032",
                "صحيح"
        );
    }

    private void loadGuidance() {
        add(
                "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْهُدَى وَالتُّقَى وَالْعَفَافَ وَالْغِنَى",
                1,
                "صحيح مسلم، حديث 2721",
                "صحيح"
        );
    }

    private void loadFamily() {
        add(
                "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
                1,
                "القرآن الكريم — سورة الفرقان، الآية 74",
                "آية قرآنية"
        );
    }

    private void loadParents() {
        add(
                "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
                1,
                "القرآن الكريم — سورة الإسراء، الآية 24",
                "آية قرآنية"
        );
    }

    private void loadDebt() {
        add(
                "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
                1,
                "جامع الترمذي، حديث 3563",
                "حسن غريب"
        );
    }

    private void showDhikr() {
        // =====================================================
        // ROOT
        // =====================================================
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);

        root.addView(
                buildHeader(
                        getCategoryTitle(currentCategory),
                        false
                ),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(78)
                )
        );

        // =====================================================
        // SCROLL
        // =====================================================
        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, 0, 0, dp(8));

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(
                dp(18),
                dp(10),
                dp(18),
                dp(42)
        );

        // =====================================================
        // CURRENT ITEM
        // =====================================================
        Dhikr item = currentAdhkar.get(currentIndex);

        // =====================================================
        // PROGRESS HEADER
        // =====================================================
        LinearLayout progressHeader = new LinearLayout(this);
        progressHeader.setOrientation(LinearLayout.HORIZONTAL);
        progressHeader.setGravity(Gravity.CENTER_VERTICAL);

        GradientDrawable progressHeaderBg = new GradientDrawable();
        progressHeaderBg.setColor(Color.rgb(238, 246, 241));
        progressHeaderBg.setCornerRadius(dp(16));
        progressHeaderBg.setStroke(dp(1), Color.rgb(219, 232, 224));

        progressHeader.setBackground(progressHeaderBg);
        progressHeader.setPadding(
                dp(12),
                dp(5),
                dp(12),
                dp(5)
        );

        TextView progressIcon = text(
                "۞",
                15,
                GOLD,
                true
        );

        progressIcon.setGravity(Gravity.CENTER);

        progressHeader.addView(
                progressIcon,
                new LinearLayout.LayoutParams(
                        dp(28),
                        dp(28)
                )
        );

        TextView progressTitle = text(
                "الذكر " + (currentIndex + 1)
                        + " من " + currentAdhkar.size(),
                13,
                EMERALD_DARK,
                true
        );

        progressTitle.setGravity(
                Gravity.CENTER_VERTICAL | Gravity.RIGHT
        );

        progressHeader.addView(
                progressTitle,
                new LinearLayout.LayoutParams(
                        0,
                        dp(28),
                        1f
                )
        );

        body.addView(
                progressHeader,
                new LinearLayout.LayoutParams(
                        dp(190),
                        dp(40)
                )
        );

        // =====================================================
        // DHIKR CARD
        // =====================================================
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);

        card.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        GradientDrawable cardBg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        WHITE,
                        Color.rgb(251, 250, 246)
                }
        );

        cardBg.setCornerRadius(dp(30));
        cardBg.setStroke(
                dp(1),
                Color.rgb(225, 230, 226)
        );

        card.setBackground(cardBg);
        card.setElevation(dp(6));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                dp(14),
                0,
                0
        );

        // =====================================================
        // TOP ORNAMENT
        // =====================================================
        LinearLayout ornamentRow = new LinearLayout(this);
        ornamentRow.setGravity(Gravity.CENTER);
        ornamentRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView ornamentLeft = text(
                "────────",
                8,
                Color.rgb(218, 202, 157),
                false
        );

        ornamentLeft.setGravity(Gravity.CENTER);

        ornamentRow.addView(
                ornamentLeft,
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(25)
                )
        );

        TextView ornament = text(
                "۞",
                25,
                GOLD,
                true
        );

        ornament.setGravity(Gravity.CENTER);

        ornamentRow.addView(
                ornament,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(30)
                )
        );

        TextView ornamentRight = text(
                "────────",
                8,
                Color.rgb(218, 202, 157),
                false
        );

        ornamentRight.setGravity(Gravity.CENTER);

        ornamentRow.addView(
                ornamentRight,
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(25)
                )
        );

        card.addView(
                ornamentRow,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(30)
                )
        );

        // =====================================================
        // DHIKR TEXT
        // =====================================================
        TextView dhikrText = text(
                item.text,
                21,
                TEXT,
                true
        );

        dhikrText.setGravity(Gravity.CENTER);
        dhikrText.setTextDirection(View.TEXT_DIRECTION_RTL);
        dhikrText.setIncludeFontPadding(true);
        dhikrText.setLineSpacing(dp(6), 1.20f);

        LinearLayout.LayoutParams dhikrParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        dhikrParams.setMargins(
                0,
                dp(7),
                0,
                0
        );

        card.addView(
                dhikrText,
                dhikrParams
        );

        // =====================================================
        // SOURCE / GRADE
        // =====================================================
        if (item.source != null && !item.source.trim().isEmpty()) {

            LinearLayout sourceBox = new LinearLayout(this);
            sourceBox.setOrientation(LinearLayout.VERTICAL);
            sourceBox.setGravity(Gravity.CENTER);
            sourceBox.setPadding(
                    dp(14),
                    dp(10),
                    dp(14),
                    dp(10)
            );

            GradientDrawable sourceBg = new GradientDrawable();
            sourceBg.setColor(Color.rgb(248, 245, 232));
            sourceBg.setCornerRadius(dp(16));
            sourceBg.setStroke(
                    dp(1),
                    Color.rgb(232, 220, 185)
            );
            sourceBox.setBackground(sourceBg);

            TextView sourceText = text(
                    "المصدر: " + item.source,
                    12,
                    TEXT_SECONDARY,
                    false
            );

            sourceText.setGravity(Gravity.CENTER);
            sourceText.setTextDirection(
                    View.TEXT_DIRECTION_RTL
            );

            sourceBox.addView(
                    sourceText,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            if (item.grade != null &&
                    !item.grade.trim().isEmpty()) {

                TextView gradeText = text(
                        "الدرجة: " + item.grade,
                        12,
                        EMERALD_DARK,
                        true
                );

                gradeText.setGravity(Gravity.CENTER);
                gradeText.setTextDirection(
                        View.TEXT_DIRECTION_RTL
                );

                LinearLayout.LayoutParams gradeParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                gradeParams.setMargins(
                        0,
                        dp(4),
                        0,
                        0
                );

                sourceBox.addView(
                        gradeText,
                        gradeParams
                );
            }

            LinearLayout.LayoutParams sourceParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            sourceParams.setMargins(
                    0,
                    dp(12),
                    0,
                    0
            );

            card.addView(
                    sourceBox,
                    sourceParams
            );
        }

        // =====================================================
        // TARGET BADGE
        // =====================================================
        LinearLayout targetBadge = new LinearLayout(this);
        targetBadge.setGravity(Gravity.CENTER);
        targetBadge.setPadding(
                dp(14),
                0,
                dp(14),
                0
        );

        GradientDrawable targetBg = new GradientDrawable();
        targetBg.setColor(Color.rgb(241, 246, 243));
        targetBg.setCornerRadius(dp(14));
        targetBg.setStroke(
                dp(1),
                Color.rgb(222, 232, 226)
        );

        targetBadge.setBackground(targetBg);

        TextView target = text(
                "التكرار المطلوب  •  " + item.count,
                12,
                EMERALD_DARK,
                true
        );

        target.setGravity(Gravity.CENTER);

        targetBadge.addView(
                target,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(32)
                )
        );

        LinearLayout.LayoutParams targetParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(34)
                );

        targetParams.setMargins(
                0,
                dp(16),
                0,
                0
        );

        card.addView(
                targetBadge,
                targetParams
        );

        body.addView(card, cardParams);

        // =====================================================
        // COUNTER SECTION
        // =====================================================
        final ProgressCircle progressCircle =
                new ProgressCircle(this);

        progressCircle.setCount(
                currentCount,
                item.count
        );

        LinearLayout.LayoutParams circleParams =
                new LinearLayout.LayoutParams(
                        dp(190),
                        dp(190)
                );

        circleParams.setMargins(
                0,
                dp(20),
                0,
                dp(4)
        );

        body.addView(
                progressCircle,
                circleParams
        );

        // =====================================================
        // HINT
        // =====================================================
        TextView hint = text(
                currentCount == 0
                        ? "اضغط على الدائرة للتسبيح"
                        : currentCount >= item.count
                                ? "تم إكمال الذكر ✓"
                                : "متبقي " + (item.count - currentCount) + " مرة",
                13,
                currentCount >= item.count
                        ? EMERALD
                        : TEXT_SECONDARY,
                true
        );

        hint.setGravity(Gravity.CENTER);
        hint.setIncludeFontPadding(true);

        body.addView(
                hint,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(34)
                )
        );

        // =====================================================
        // COUNTER CLICK
        // SAME LOGIC
        // =====================================================
        progressCircle.setOnClickListener(v -> {

            if (currentCount >= item.count) {
                return;
            }

            currentCount++;

            saveCurrentProgress();

            progressCircle.setCount(
                    currentCount,
                    item.count
            );

            progressCircle.performHapticFeedback(
                    android.view.HapticFeedbackConstants.VIRTUAL_KEY
            );

            if (currentCount >= item.count) {

                hint.setText("تم إكمال الذكر ✓");
                hint.setTextColor(EMERALD);

            } else {

                hint.setText(
                        "متبقي "
                                + (item.count - currentCount)
                                + " مرة"
                );

                hint.setTextColor(TEXT_SECONDARY);
            }
        });

        // =====================================================
        // ACTIONS
        // =====================================================
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);

        // =====================================================
        // RESET
        // =====================================================
        TextView reset = text(
                "↻  إعادة",
                14,
                TEXT_SECONDARY,
                true
        );

        reset.setGravity(Gravity.CENTER);

        GradientDrawable resetBg = new GradientDrawable();
        resetBg.setColor(WHITE);
        resetBg.setCornerRadius(dp(18));
        resetBg.setStroke(
                dp(1),
                BORDER
        );

        reset.setBackground(resetBg);
        reset.setElevation(dp(2));

        reset.setOnClickListener(v -> {

            currentCount = 0;

            saveCurrentProgress();

            showDhikr();
        });

        actions.addView(
                reset,
                new LinearLayout.LayoutParams(
                        dp(125),
                        dp(50)
                )
        );

        // =====================================================
        // NEXT
        // =====================================================
        TextView next = text(
                currentIndex < currentAdhkar.size() - 1
                        ? "التالي  ›"
                        : "إنهاء  ✓",
                15,
                WHITE,
                true
        );

        next.setGravity(Gravity.CENTER);

        GradientDrawable nextBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                EMERALD_DARK,
                                EMERALD
                        }
                );

        nextBg.setCornerRadius(dp(18));

        next.setBackground(nextBg);
        next.setElevation(dp(3));

        next.setOnClickListener(v -> {

            if (currentIndex < currentAdhkar.size() - 1) {

                currentIndex++;

                currentCount =
                        adhkarPrefs.getInt(
                                currentCategory
                                        + "_COUNT_"
                                        + currentIndex,
                                0
                        );

                saveCurrentProgress();

                showDhikr();

            } else {

                resetCategoryProgress();

                showCompletionScreen();
            }
        });

        LinearLayout.LayoutParams nextParams =
                new LinearLayout.LayoutParams(
                        dp(125),
                        dp(50)
                );

        nextParams.setMargins(
                dp(10),
                0,
                0,
                0
        );

        actions.addView(
                next,
                nextParams
        );

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(55)
                );

        actionsParams.setMargins(
                0,
                dp(12),
                0,
                0
        );

        body.addView(
                actions,
                actionsParams
        );

        // =====================================================
        // FOOTER
        // =====================================================
        TextView footer = text(
                "لعلها المنجيه  •  واذكر ربك إذا نسيت",
                11,
                TEXT_SECONDARY,
                false
        );

        footer.setGravity(Gravity.CENTER);
        footer.setIncludeFontPadding(true);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                );

        footerParams.setMargins(
                0,
                dp(14),
                0,
                dp(8)
        );

        body.addView(
                footer,
                footerParams
        );

        // =====================================================
        // BOTTOM SAFETY SPACE
        // =====================================================
        View bottomSpace = new View(this);

        body.addView(
                bottomSpace,
                new LinearLayout.LayoutParams(
                        1,
                        dp(28)
                )
        );

        scroll.addView(body);

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

    // =========================================================
    // دائرة التقدم
    // =========================================================

    private class ProgressCircle extends View {

        private android.graphics.Paint paint;
        private int count = 0;
        private int total = 1;

        public ProgressCircle(android.content.Context context) {
            super(context);

            paint = new android.graphics.Paint(
                    android.graphics.Paint.ANTI_ALIAS_FLAG
            );

            setClickable(true);
        }

        public void setCount(int count, int total) {
            this.count = count;
            this.total = Math.max(1, total);
            invalidate();
        }

        @Override
        protected void onDraw(
                android.graphics.Canvas canvas
        ) {

            super.onDraw(canvas);

            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;

            float radius =
                    Math.min(
                            getWidth(),
                            getHeight()
                    ) / 2f - dp(14);

            // دائرة الخلفية
            paint.setStyle(
                    android.graphics.Paint.Style.STROKE
            );

            paint.setStrokeWidth(dp(10));
            paint.setStrokeCap(
                    android.graphics.Paint.Cap.ROUND
            );

            paint.setColor(
                    Color.rgb(229, 235, 231)
            );

            canvas.drawCircle(
                    cx,
                    cy,
                    radius,
                    paint
            );

            // نسبة التقدم
            float progress =
                    Math.min(
                            1f,
                            count / (float) total
                    );

            paint.setColor(
                    progress >= 1f
                            ? GOLD
                            : EMERALD
            );

            canvas.drawArc(
                    cx - radius,
                    cy - radius,
                    cx + radius,
                    cy + radius,
                    -90,
                    progress * 360f,
                    false,
                    paint
            );

            // العدد
            paint.setStyle(
                    android.graphics.Paint.Style.FILL
            );

            paint.setColor(
                    progress >= 1f
                            ? EMERALD_DARK
                            : TEXT
            );

            paint.setTextAlign(
                    android.graphics.Paint.Align.CENTER
            );

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );

            paint.setTextSize(dp(38));

            canvas.drawText(
                    String.valueOf(count),
                    cx,
                    cy + dp(12),
                    paint
            );

            // الإجمالي
            paint.setColor(TEXT_SECONDARY);
            paint.setTextSize(dp(12));

            canvas.drawText(
                    "من " + total,
                    cx,
                    cy + dp(36),
                    paint
            );

            // علامة الاكتمال
            if (count >= total) {

                paint.setColor(GOLD);
                paint.setTextSize(dp(17));

                canvas.drawText(
                        "✓ مكتمل",
                        cx,
                        cy - dp(36),
                        paint
                );
            }
        }
    }

    private void saveCurrentProgress() {

        if (adhkarPrefs == null) {
            return;
        }

        adhkarPrefs.edit()
                .putInt(
                        currentCategory + "_INDEX",
                        currentIndex
                )
                .putInt(
                        currentCategory
                                + "_COUNT_"
                                + currentIndex,
                        currentCount
                )
                .apply();
    }

    private void resetCategoryProgress() {

        if (adhkarPrefs == null) {
            return;
        }

        android.content.SharedPreferences.Editor editor =
                adhkarPrefs.edit();

        editor.putInt(
                currentCategory + "_INDEX",
                0
        );

        for (int i = 0; i < currentAdhkar.size(); i++) {
            editor.remove(
                    currentCategory
                            + "_COUNT_"
                            + i
            );
        }

        editor.apply();
    }

    private void showCompletionScreen() {

        // =====================================================
        // ROOT
        // =====================================================
        LinearLayout root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setBackgroundColor(CREAM);

        // =====================================================
        // HEADER
        // =====================================================
        root.addView(
                buildHeader(
                        getCategoryTitle(currentCategory),
                        false
                ),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(78)
                )
        );

        // =====================================================
        // CENTER AREA
        // =====================================================
        LinearLayout center = new LinearLayout(this);

        center.setOrientation(
                LinearLayout.VERTICAL
        );

        center.setGravity(
                Gravity.CENTER
        );

        center.setPadding(
                dp(22),
                dp(18),
                dp(22),
                dp(28)
        );

        // =====================================================
        // SUCCESS CARD
        // =====================================================
        LinearLayout box = new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        GradientDrawable boxBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{
                                WHITE,
                                Color.rgb(246, 250, 247)
                        }
                );

        boxBg.setCornerRadius(dp(30));

        boxBg.setStroke(
                dp(1),
                Color.rgb(220, 231, 224)
        );

        box.setBackground(boxBg);
        box.setElevation(dp(6));

        box.setPadding(
                dp(22),
                dp(25),
                dp(22),
                dp(25)
        );

        // =====================================================
        // GOLD ORNAMENT
        // =====================================================
        TextView ornamentTop = text(
                "۞",
                25,
                GOLD,
                true
        );

        ornamentTop.setGravity(
                Gravity.CENTER
        );

        box.addView(
                ornamentTop,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(38)
                )
        );

        // =====================================================
        // SUCCESS CIRCLE
        // =====================================================
        TextView icon = text(
                "✓",
                42,
                EMERALD,
                true
        );

        icon.setGravity(
                Gravity.CENTER
        );

        GradientDrawable iconBg =
                new GradientDrawable();

        iconBg.setShape(
                GradientDrawable.OVAL
        );

        iconBg.setColor(
                Color.rgb(222, 241, 230)
        );

        iconBg.setStroke(
                dp(1),
                Color.rgb(190, 220, 202)
        );

        icon.setBackground(iconBg);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(92),
                        dp(92)
                );

        iconParams.setMargins(
                0,
                dp(6),
                0,
                dp(15)
        );

        box.addView(
                icon,
                iconParams
        );

        // =====================================================
        // TITLE
        // =====================================================
        TextView title = text(
                "أحسنت، تم إكمال الأذكار",
                22,
                TEXT,
                true
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setTextDirection(
                View.TEXT_DIRECTION_RTL
        );

        box.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(48)
                )
        );

        // =====================================================
        // CATEGORY NAME
        // =====================================================
        TextView categoryName = text(
                getCategoryTitle(currentCategory),
                14,
                EMERALD_DARK,
                true
        );

        categoryName.setGravity(
                Gravity.CENTER
        );

        GradientDrawable categoryBg =
                new GradientDrawable();

        categoryBg.setColor(
                Color.rgb(237, 246, 241)
        );

        categoryBg.setCornerRadius(
                dp(14)
        );

        categoryBg.setStroke(
                dp(1),
                Color.rgb(220, 233, 224)
        );

        categoryName.setBackground(
                categoryBg
        );

        LinearLayout.LayoutParams categoryParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(34)
                );

        categoryParams.setMargins(
                0,
                dp(4),
                0,
                dp(13)
        );

        box.addView(
                categoryName,
                categoryParams
        );

        // =====================================================
        // SUBTITLE
        // =====================================================
        TextView subtitle = text(
                "تقبّل الله منك وكتب لك الأجر",
                15,
                TEXT_SECONDARY,
                false
        );

        subtitle.setGravity(
                Gravity.CENTER
        );

        subtitle.setTextDirection(
                View.TEXT_DIRECTION_RTL
        );

        box.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                )
        );

        // =====================================================
        // DIVIDER
        // =====================================================
        View divider = new View(this);

        GradientDrawable dividerBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                Color.TRANSPARENT,
                                Color.rgb(218, 226, 220),
                                Color.TRANSPARENT
                        }
                );

        divider.setBackground(dividerBg);

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerParams.setMargins(
                dp(25),
                dp(8),
                dp(25),
                dp(10)
        );

        box.addView(
                divider,
                dividerParams
        );

        // =====================================================
        // MOTIVATION
        // =====================================================
        TextView motivation = text(
                "اجعل لسانك عامرًا بذكر الله",
                13,
                TEXT_SECONDARY,
                false
        );

        motivation.setGravity(
                Gravity.CENTER
        );

        motivation.setTextDirection(
                View.TEXT_DIRECTION_RTL
        );

        box.addView(
                motivation,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(32)
                )
        );

        // =====================================================
        // BACK BUTTON
        // =====================================================
        TextView back = text(
                "العودة إلى الأذكار",
                16,
                WHITE,
                true
        );

        back.setGravity(
                Gravity.CENTER
        );

        GradientDrawable backBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                EMERALD_DARK,
                                EMERALD
                        }
                );

        backBg.setCornerRadius(
                dp(19)
        );

        back.setBackground(
                backBg
        );

        back.setElevation(
                dp(3)
        );

        back.setOnClickListener(v ->
                buildHome()
        );

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(
                        dp(220),
                        dp(54)
                );

        backParams.setMargins(
                0,
                dp(20),
                0,
                0
        );

        box.addView(
                back,
                backParams
        );

        // =====================================================
        // ADD CARD
        // =====================================================
        LinearLayout.LayoutParams boxParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        boxParams.setMargins(
                0,
                dp(8),
                0,
                dp(8)
        );

        center.addView(
                box,
                boxParams
        );

        // =====================================================
        // FOOTER
        // =====================================================
        TextView footer = text(
                "لعلها المنجيه  •  واذكر ربك إذا نسيت",
                11,
                TEXT_SECONDARY,
                false
        );

        footer.setGravity(
                Gravity.CENTER
        );

        center.addView(
                footer,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                )
        );

        // =====================================================
        // ADD CENTER
        // =====================================================
        root.addView(
                center,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        setContentView(root);
    }

    private String getCategoryTitle(String category) {
        if (category.equals("morning"))
            return "أذكار الصباح";

        if (category.equals("evening"))
            return "أذكار المساء";

        if (category.equals("after_prayer"))
            return "أذكار بعد الصلاة";

        if (category.equals("sleep"))
            return "أذكار النوم";

        if (category.equals("wake"))
            return "أذكار الاستيقاظ";

        if (category.equals("daily"))
            return "أذكار متنوعة";

        if (category.equals("patient"))
            return "دعاء للمريض";

        if (category.equals("home_enter"))
            return "دخول المنزل";

        if (category.equals("home_exit"))
            return "الخروج من المنزل";

        if (category.equals("market"))
            return "دخول السوق";

        if (category.equals("ride"))
            return "ركوب السيارة";

        if (category.equals("travel"))
            return "دعاء السفر";

        if (category.equals("rizq"))
            return "الرزق";

        if (category.equals("exam"))
            return "الامتحان والتوفيق";

        if (category.equals("ease"))
            return "تسهيل الأمور";

        if (category.equals("deceased"))
            return "دعاء للميت";

        if (category.equals("arafah"))
            return "يوم عرفة";

        if (category.equals("traveler_family"))
            return "مسافر لأهله";

        if (category.equals("distress"))
            return "الكرب والهم";

        if (category.equals("sadness"))
            return "الحزن والهم";

        if (category.equals("eye"))
            return "العين والحسد";

        if (category.equals("yunus"))
            return "دعوة ذي النون";

        if (category.equals("children"))
            return "تحصين الأطفال";

        if (category.equals("istikhara"))
            return "الاستخارة";

        if (category.equals("new_clothes"))
            return "لبس الثوب الجديد";

        if (category.equals("food_before"))
            return "قبل الطعام";

        if (category.equals("food_after"))
            return "بعد الطعام";

        if (category.equals("toilet"))
            return "دخول الخلاء";

        if (category.equals("rain"))
            return "نزول المطر";

        if (category.equals("guidance"))
            return "التوفيق والسداد";

        if (category.equals("family"))
            return "الزواج والزوج الصالح";

        if (category.equals("parents"))
            return "دعاء الوالدين";

        if (category.equals("debt"))
            return "قضاء الدين";

        return "أذكار متنوعة";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private TextView text(
            String value,
            float size,
            int color,
            boolean bold
    ) {

        TextView view = new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        view.setIncludeFontPadding(true);

        return view;
    }

    private int dp(int value) {
        return Math.round(
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
