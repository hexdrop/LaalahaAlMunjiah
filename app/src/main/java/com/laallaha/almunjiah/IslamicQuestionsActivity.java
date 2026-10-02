package com.laallaha.almunjiah;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IslamicQuestionsActivity extends Activity {

    private LinearLayout root;

    private TextView questionNumber;
    private TextView scoreText;
    private TextView surahText;
    private TextView currentAyahText;
    private TextView hiddenAyahText;
    private TextView resultText;
    private TextView revealButton;
    private TextView rememberedButton;
    private TextView forgotButton;

    private final List<MemorizationQuestion> questions =
            new ArrayList<>();

    private final Random random =
            new Random();
    private final List<String> allSurahNames = new ArrayList<>();
    private final java.util.LinkedHashSet<String> selectedSurahs =
            new java.util.LinkedHashSet<>();


    private int lastQuestionIndex = -1;

    private int rememberedCount = 0;
    private int totalReviewed = 0;

    private boolean revealed = false;

    private static final int BG =
            Color.rgb(5, 18, 28);

    private static final int CARD =
            Color.rgb(15, 34, 40);

    private static final int CARD_SOFT =
            Color.rgb(20, 42, 45);

    private static final int EMERALD =
            Color.rgb(35, 150, 112);

    private static final int GOLD =
            Color.rgb(218, 184, 105);

    private static final int WHITE =
            Color.WHITE;

    private static final int SECONDARY =
            Color.rgb(170, 185, 180);

    private static final int BORDER =
            Color.rgb(45, 70, 70);

    private static class MemorizationQuestion {

        String currentAyah;
        String nextAyah;
        String surahName;

        int currentAyahNumber;
        int nextAyahNumber;

        MemorizationQuestion(
                String currentAyah,
                String nextAyah,
                String surahName,
                int currentAyahNumber,
                int nextAyahNumber
        ) {
            this.currentAyah = currentAyah;
            this.nextAyah = nextAyah;
            this.surahName = surahName;
            this.currentAyahNumber = currentAyahNumber;
            this.nextAyahNumber = nextAyahNumber;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSurahNames();
        loadSavedSurahSelection();
        showSurahSelector();
    }

    /*
     * نبني أسئلة الحفظ من نفس quran.json
     * المستخدم داخل التطبيق.
     *
     * كل سؤال:
     * آية حالية -> الآية التي بعدها.
     */
    
    private void loadSurahNames() {
        allSurahNames.clear();

        try {
            InputStream inputStream =
                    getAssets().open("quran.json");

            byte[] data =
                    new byte[inputStream.available()];

            int offset = 0;
            int read;

            while (
                    offset < data.length
                            && (read = inputStream.read(
                                    data,
                                    offset,
                                    data.length - offset
                            )) > 0
            ) {
                offset += read;
            }

            inputStream.close();

            String json =
                    new String(
                            data,
                            StandardCharsets.UTF_8
                    );

            JSONArray surahs =
                    new JSONArray(json);

            for (int s = 0; s < surahs.length(); s++) {

                JSONObject surah =
                        surahs.getJSONObject(s);

                String name =
                        surah.optString(
                                "name",
                                "سورة " + (s + 1)
                        ).trim();

                if (!name.isEmpty()) {
                    allSurahNames.add(name);
                }
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "تعذر تحميل قائمة السور",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void loadSavedSurahSelection() {

        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        "QURAN_MEMORIZATION",
                        MODE_PRIVATE
                );

        String saved =
                prefs.getString(
                        "SELECTED_SURAHS",
                        ""
                );

        selectedSurahs.clear();

        if (!saved.trim().isEmpty()) {

            String[] parts =
                    saved.split("\\|");

            for (String name : parts) {

                if (allSurahNames.contains(name)) {
                    selectedSurahs.add(name);
                }
            }
        }

        if (
                selectedSurahs.isEmpty()
                        && !allSurahNames.isEmpty()
        ) {
            selectedSurahs.add(
                    allSurahNames.get(0)
            );
        }
    }

    private void saveSurahSelection() {

        StringBuilder value =
                new StringBuilder();

        for (String name : selectedSurahs) {

            if (value.length() > 0) {
                value.append("|");
            }

            value.append(name);
        }

        getSharedPreferences(
                "QURAN_MEMORIZATION",
                MODE_PRIVATE
        )
                .edit()
                .putString(
                        "SELECTED_SURAHS",
                        value.toString()
                )
                .apply();
    }

    private void showSurahSelector() {

        if (allSurahNames.isEmpty()) {

            Toast.makeText(
                    this,
                    "لا توجد سور متاحة للاختبار",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        final String[] names =
                allSurahNames.toArray(
                        new String[0]
                );

        final boolean[] checked =
                new boolean[names.length];

        for (int i = 0; i < names.length; i++) {
            checked[i] =
                    selectedSurahs.contains(names[i]);
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle(
                "اختر سور الاختبار"
        );

        builder.setMultiChoiceItems(
                names,
                checked,
                (dialog, which, isChecked) -> {

                    if (isChecked) {
                        selectedSurahs.add(names[which]);
                    } else {
                        selectedSurahs.remove(names[which]);
                    }
                }
        );

        builder.setNeutralButton(
                "تحديد الكل",
                null
        );

        builder.setNegativeButton(
                "إلغاء الكل",
                null
        );

        builder.setPositiveButton(
                "ابدأ الاختبار",
                null
        );

        AlertDialog dialog =
                builder.create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    AlertDialog.BUTTON_NEUTRAL
            ).setOnClickListener(v -> {

                selectedSurahs.clear();

                for (String name : names) {
                    selectedSurahs.add(name);
                }

                dialog.dismiss();
                showSurahSelector();
            });

            dialog.getButton(
                    AlertDialog.BUTTON_NEGATIVE
            ).setOnClickListener(v -> {

                selectedSurahs.clear();

                dialog.dismiss();
                showSurahSelector();
            });

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                if (selectedSurahs.isEmpty()) {

                    Toast.makeText(
                            this,
                            "اختر سورة واحدة على الأقل",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                saveSurahSelection();

                loadQuranQuestions();

                java.util.Iterator<MemorizationQuestion> iterator =
                        questions.iterator();

                while (iterator.hasNext()) {

                    MemorizationQuestion q =
                            iterator.next();

                    if (!selectedSurahs.contains(q.surahName)) {
                        iterator.remove();
                    }
                }

                rememberedCount = 0;
                totalReviewed = 0;
                lastQuestionIndex = -1;

                buildScreen();
                showRandomQuestion();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void loadQuranQuestions() {

        questions.clear();

        try {

            InputStream inputStream =
                    getAssets().open("quran.json");

            byte[] data =
                    new byte[inputStream.available()];

            int offset = 0;
            int read;

            while (
                    offset < data.length
                            && (read = inputStream.read(
                            data,
                            offset,
                            data.length - offset
                    )) > 0
            ) {
                offset += read;
            }

            inputStream.close();

            String json =
                    new String(
                            data,
                            StandardCharsets.UTF_8
                    );

            JSONArray surahs =
                    new JSONArray(json);

            for (
                    int s = 0;
                    s < surahs.length();
                    s++
            ) {

                JSONObject surah =
                        surahs.getJSONObject(s);

                String surahName =
                        surah.optString(
                                "name",
                                "سورة"
                        );

                JSONArray verses =
                        surah.optJSONArray(
                                "verses"
                        );

                if (verses == null) {
                    continue;
                }

                /*
                 * آخر آية في السورة ليس لها آية بعدها،
                 * لذلك لا ننشئ سؤالًا لها.
                 */
                for (
                        int a = 0;
                        a < verses.length() - 1;
                        a++
                ) {

                    JSONObject current =
                            verses.getJSONObject(a);

                    JSONObject next =
                            verses.getJSONObject(a + 1);

                    String currentText =
                            current.optString(
                                    "text",
                                    ""
                            ).trim();

                    String nextText =
                            next.optString(
                                    "text",
                                    ""
                            ).trim();

                    int currentNumber =
                            current.optInt(
                                    "id",
                                    a + 1
                            );

                    int nextNumber =
                            next.optInt(
                                    "id",
                                    a + 2
                            );

                    if (
                            currentText.length() == 0
                                    || nextText.length() == 0
                    ) {
                        continue;
                    }

                    questions.add(
                            new MemorizationQuestion(
                                    currentText,
                                    nextText,
                                    surahName,
                                    currentNumber,
                                    nextNumber
                            )
                    );
                }
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "تعذر تحميل بيانات القرآن",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void buildScreen() {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setBackgroundColor(BG);

        root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(30)
        );

        scroll.addView(root);

        TextView header =
                text(
                        "اختبار تحفيظ القرآن",
                        25,
                        WHITE,
                        true
                );

        header.setGravity(
                Gravity.CENTER
        );

        root.addView(
                header,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(55)
                )
        );

        TextView subtitle =
                text(
                        "اختبر حفظك بدون اختيارات",
                        13,
                        SECONDARY,
                        false
                );

        subtitle.setGravity(
                Gravity.CENTER
        );

        root.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(32)
                )
        );

        scoreText =
                text(
                        "راجعت: 0  •  تذكرت: 0",
                        13,
                        GOLD,
                        true
                );

        scoreText.setGravity(
                Gravity.CENTER
        );

        root.addView(
                scoreText,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(38)
                )
        );

        questionNumber =
                text(
                        "",
                        12,
                        SECONDARY,
                        true
                );

        questionNumber.setGravity(
                Gravity.CENTER
        );

        root.addView(
                questionNumber
        );

        surahText =
                text(
                        "",
                        15,
                        GOLD,
                        true
                );

        surahText.setGravity(
                Gravity.RIGHT
        );

        surahText.setPadding(
                0,
                dp(12),
                0,
                dp(7)
        );

        root.addView(
                surahText
        );

        TextView instruction =
                text(
                        "اقرأ الآية وحاول استرجاع الآية التالية من حفظك",
                        14,
                        SECONDARY,
                        false
                );

        instruction.setGravity(
                Gravity.RIGHT
        );

        instruction.setPadding(
                0,
                0,
                0,
                dp(8)
        );

        root.addView(
                instruction
        );

        currentAyahText =
                text(
                        "",
                        20,
                        WHITE,
                        true
                );

        currentAyahText.setGravity(
                Gravity.RIGHT
                        | Gravity.CENTER_VERTICAL
        );

        currentAyahText.setPadding(
                dp(20),
                dp(22),
                dp(20),
                dp(22)
        );

        currentAyahText.setTextIsSelectable(true);

        GradientDrawable currentBg =
                new GradientDrawable();

        currentBg.setColor(CARD);
        currentBg.setCornerRadius(
                dp(22)
        );

        currentBg.setStroke(
                dp(1),
                BORDER
        );

        currentAyahText.setBackground(
                currentBg
        );

        LinearLayout.LayoutParams currentParams =
                new LinearLayout.LayoutParams(
                        -1,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        currentAyahText.setMinHeight(dp(145));

        currentParams.setMargins(
                0,
                dp(5),
                0,
                dp(14)
        );

        root.addView(
                currentAyahText,
                currentParams
        );

        TextView nextLabel =
                text(
                        "الآية التالية",
                        15,
                        GOLD,
                        true
                );

        nextLabel.setGravity(
                Gravity.RIGHT
        );

        nextLabel.setPadding(
                0,
                dp(4),
                0,
                dp(7)
        );

        root.addView(
                nextLabel
        );

        hiddenAyahText =
                text(
                        "",
                        19,
                        WHITE,
                        true
                );

        hiddenAyahText.setGravity(
                Gravity.CENTER
        );

        hiddenAyahText.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        GradientDrawable hiddenBg =
                new GradientDrawable();

        hiddenBg.setColor(
                CARD_SOFT
        );

        hiddenBg.setCornerRadius(
                dp(22)
        );

        hiddenBg.setStroke(
                dp(1),
                GOLD
        );

        hiddenAyahText.setBackground(
                hiddenBg
        );

        LinearLayout.LayoutParams hiddenParams =
                new LinearLayout.LayoutParams(
                        -1,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        hiddenAyahText.setMinHeight(dp(135));

        root.addView(
                hiddenAyahText,
                hiddenParams
        );

        revealButton =
                text(
                        "اضغط لإظهار الآية",
                        16,
                        BG,
                        true
                );

        revealButton.setGravity(
                Gravity.CENTER
        );

        GradientDrawable revealBg =
                new GradientDrawable();

        revealBg.setColor(
                GOLD
        );

        revealBg.setCornerRadius(
                dp(18)
        );

        revealButton.setBackground(
                revealBg
        );

        LinearLayout.LayoutParams revealParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );

        revealParams.setMargins(
                0,
                dp(14),
                0,
                dp(12)
        );

        root.addView(
                revealButton,
                revealParams
        );

        revealButton.setOnClickListener(
                v -> revealAnswer()
        );

        resultText =
                text(
                        "",
                        14,
                        WHITE,
                        false
                );

        resultText.setGravity(
                Gravity.RIGHT
        );

        resultText.setPadding(
                dp(16),
                dp(8),
                dp(16),
                dp(8)
        );

        root.addView(
                resultText
        );

        LinearLayout evaluation =
                new LinearLayout(this);

        evaluation.setOrientation(
                LinearLayout.HORIZONTAL
        );

        evaluation.setGravity(
                Gravity.CENTER
        );

        rememberedButton =
                text(
                        "✓ تذكرتها",
                        15,
                        WHITE,
                        true
                );

        rememberedButton.setGravity(
                Gravity.CENTER
        );

        rememberedButton.setBackground(
                actionBackground(
                        EMERALD
                )
        );

        forgotButton =
                text(
                        "↻ لم أتذكرها",
                        15,
                        WHITE,
                        true
                );

        forgotButton.setGravity(
                Gravity.CENTER
        );

        forgotButton.setBackground(
                actionBackground(
                        Color.rgb(
                                90,
                                70,
                                50
                        )
                )
        );

        evaluation.addView(
                rememberedButton,
                actionParams()
        );

        evaluation.addView(
                forgotButton,
                actionParams()
        );

        root.addView(
                evaluation
        );

        rememberedButton.setVisibility(
                View.GONE
        );

        forgotButton.setVisibility(
                View.GONE
        );

        rememberedButton.setOnClickListener(
                v -> evaluate(true)
        );

        forgotButton.setOnClickListener(
                v -> evaluate(false)
        );

        setContentView(scroll);
    }

    private void showRandomQuestion() {

        if (questions.isEmpty()) {

            currentAyahText.setText(
                    "لا توجد بيانات قرآن متاحة."
            );

            return;
        }

        int index;

        do {

            index =
                    random.nextInt(
                            questions.size()
                    );

        } while (
                questions.size() > 1
                        && index == lastQuestionIndex
        );

        lastQuestionIndex =
                index;

        MemorizationQuestion q =
                questions.get(index);

        revealed = false;

        surahText.setText(
                "سورة " +
                        q.surahName +
                        "  •  الآية " +
                        q.currentAyahNumber
        );

        questionNumber.setText(
                "سؤال " +
                        (totalReviewed + 1)
        );

        currentAyahText.setText(
                "﴿" +
                        q.currentAyah +
                        "﴾"
        );

        /*
         * الآية التالية تكون مخفية تمامًا.
         */
        hiddenAyahText.setText(
                "🔒\n\nاضغط على الزر بالأسفل\nلإظهار الآية التالية"
        );

        hiddenAyahText.setTextColor(
                SECONDARY
        );

        revealButton.setText(
                "اضغط لإظهار الآية"
        );

        revealButton.setEnabled(
                true
        );

        resultText.setText("");

        rememberedButton.setVisibility(
                View.GONE
        );

        forgotButton.setVisibility(
                View.GONE
        );
    }

    private void revealAnswer() {

        if (revealed) {
            return;
        }

        if (questions.isEmpty()) {
            return;
        }

        MemorizationQuestion q =
                questions.get(
                        lastQuestionIndex
                );

        revealed = true;

        hiddenAyahText.setText(
                "﴿" +
                        q.nextAyah +
                        "﴾"
        );

        hiddenAyahText.setTextColor(
                WHITE
        );

        revealButton.setText(
                "تم إظهار الآية"
        );

        revealButton.setEnabled(
                false
        );

        rememberedButton.setVisibility(
                View.VISIBLE
        );

        forgotButton.setVisibility(
                View.VISIBLE
        );

        resultText.setText(
                "الآن قيّم حفظك بنفسك."
        );
    }

    private void evaluate(
            boolean remembered
    ) {

        if (!revealed) {
            return;
        }

        totalReviewed++;

        if (remembered) {

            rememberedCount++;

            resultText.setText(
                    "✓ ممتاز، واصل المراجعة."
            );

        } else {

            resultText.setText(
                    "لا بأس، أعد الآية مرة أخرى "
                            + "وحاول تذكرها في المرة القادمة."
            );
        }

        scoreText.setText(
                "راجعت: " +
                        totalReviewed +
                        "  •  تذكرت: " +
                        rememberedCount
        );

        rememberedButton.setVisibility(
                View.GONE
        );

        forgotButton.setVisibility(
                View.GONE
        );

        /*
         * بعد التقييم يظهر زر سؤال جديد
         * بدل تغيير السؤال مباشرة.
         */
        revealButton.setText(
                "سؤال جديد  ›"
        );

        revealButton.setEnabled(
                true
        );

        revealButton.setOnClickListener(
                v -> {

                    revealButton.setOnClickListener(
                            vv -> revealAnswer()
                    );

                    showRandomQuestion();

                    revealButton.setOnClickListener(
                            vv -> revealAnswer()
                    );
                }
        );
    }

    private GradientDrawable actionBackground(
            int color
    ) {

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(color);

        bg.setCornerRadius(
                dp(16)
        );

        return bg;
    }

    private LinearLayout.LayoutParams actionParams() {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        dp(52),
                        1f
                );

        params.setMargins(
                dp(4),
                dp(10),
                dp(4),
                dp(4)
        );

        return params;
    }

    private TextView text(
            String value,
            int size,
            int color,
            boolean bold
    ) {

        TextView view =
                new TextView(this);

        view.setText(value);

        view.setTextSize(size);

        view.setTextColor(color);

        view.setTypeface(
                Typeface.DEFAULT,
                bold
                        ? Typeface.BOLD
                        : Typeface.NORMAL
        );

        view.setIncludeFontPadding(true);

        return view;
    }

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
                        + 0.5f
        );
    }
}
