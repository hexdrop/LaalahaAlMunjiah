package com.laallaha.almunjiah;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QuranActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private static final int BORDER = Color.rgb(228, 231, 226);
    private static final int GOLD = Color.rgb(190, 155, 75);

    private LinearLayout content;
    private EditText search;
    private JSONArray surahs;

    private boolean nightMode = false;
    private boolean readingFullScreen = false;

    // مشغل تلاوة القرآن
    private android.media.MediaPlayer quranPlayer;
    private android.widget.SeekBar quranSeekBar;
    private android.widget.TextView quranPlayButton;
    private android.widget.TextView quranAudioTime;
    private android.widget.TextView audioReciterHolder;
private android.widget.TextView quranDownloadButton;
private android.view.View quranMainRoot;
    private android.widget.TextView quranSkipBackButton;
    private android.widget.TextView quranSkipForwardButton;
    private android.os.Handler audioHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable audioProgressRunnable;
    private int currentAudioSurahIndex = -1;
    private boolean audioPrepared = false;
    // Mini Player — مشغل التلاوة المصغر
    private android.widget.LinearLayout quranMiniPlayer;
    private android.widget.TextView quranMiniPlayButton;
    private android.widget.TextView quranMiniTitle;
    private android.widget.TextView quranMiniSubtitle;
    private android.widget.TextView quranMiniTime;
    private android.widget.SeekBar quranMiniSeekBar;

private volatile boolean audioDownloadInProgress = false;
private Thread audioDownloadThread;
private final java.util.HashSet<String> downloadedAudioFiles =
        new java.util.HashSet<>();



    private static final String PREF_READING = "quran_reading";
    private static final String KEY_SURAH_INDEX = "surah_index";
    private static final String KEY_AYAH_INDEX = "ayah_index";
    private static final String KEY_AUDIO_POSITION_PREFIX = "audio_position_";
private static final String PREF_AUDIO = "quran_audio";
private static final String KEY_SELECTED_RECITER = "selected_reciter";
private static final String DEFAULT_RECITER_ID = "ajm";
private static final String KEY_RECITERS_CACHE = "reciters_cache_json";
private static final String KEY_SELECTED_RECITER_NAME = "selected_reciter_name";
private static final String KEY_SELECTED_RECITER_SERVER = "selected_reciter_server";
private static final String KEY_SELECTED_RECITER_MOSHAF = "selected_reciter_moshaf";


    private static class Reciter {
        final String id;
        final String name;
        final String baseUrl;
        final String moshafName;
        final java.util.HashSet<Integer> availableSurahs;

        Reciter(
                String id,
                String name,
                String baseUrl,
                String moshafName,
                java.util.HashSet<Integer> availableSurahs
        ) {
            this.id = id;
            this.name = name;
            this.baseUrl = baseUrl;
            this.moshafName = moshafName;
            this.availableSurahs = availableSurahs;
        }
    }

    private final java.util.ArrayList<Reciter> reciters =
            new java.util.ArrayList<>();

    private volatile boolean recitersLoading = false;

    private void ensureFallbackReciter() {
        if (!reciters.isEmpty()) {
            return;
        }

        /*
         * حاول أولاً استعادة قائمة القراء المحفوظة
         * حتى تعمل قائمة القراء بدون إنترنت.
         */
        if (loadRecitersFromCache()) {
            return;
        }

        java.util.HashSet<Integer> allSurahs =
                new java.util.HashSet<>();

        for (int i = 1; i <= 114; i++) {
            allSurahs.add(i);
        }

        reciters.add(
                new Reciter(
                        "ajm",
                        "الشيخ أحمد العجمي",
                        "https://server10.mp3quran.net/ajm/",
                        "رواية حفص عن عاصم",
                        allSurahs
                )
        );
    }

    private java.util.ArrayList<Reciter> getReciters() {
        ensureFallbackReciter();
        return reciters;
    }


    private Reciter getSelectedReciter() {
        String selectedId = getSelectedReciterId();

        for (Reciter reciter : getReciters()) {
            if (reciter.id.equals(selectedId)) {
                return reciter;
            }
        }

        return getReciters().get(0);
    }

    private boolean isSurahAvailableForSelectedReciter(
            int surahIndex
    ) {
        Reciter reciter = getSelectedReciter();

        return reciter.availableSurahs.contains(
                surahIndex + 1
        );
    }





    private static final String PREF_BOOKMARKS = "quran_bookmarks";
    private static final String KEY_BOOKMARKS = "bookmarked_ayahs";
    private static final String KEY_SURAH_NAME = "surah_name";

    private static final int NIGHT_BG = Color.rgb(24, 28, 27);
    private static final int NIGHT_CARD = Color.rgb(34, 39, 37);
    private static final int NIGHT_TEXT = Color.rgb(232, 229, 216);
    private static final int NIGHT_SECONDARY = Color.rgb(174, 181, 176);

    // موضع القراءة الحالي
    private int currentReadingAyahIndex = -1;
    private int currentReadingSurahIndex = -1;
    private JSONObject currentReadingSurah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadQuran();

        nightMode = getSharedPreferences(
                "app_settings",
                MODE_PRIVATE
        ).getBoolean(
                "quran_night_mode",
                false
        );

        buildScreen();

        int openSurahIndex =
                getIntent().getIntExtra(
                        "open_surah_index",
                        -1
                );

        int openAyahIndex =
                getIntent().getIntExtra(
                        "open_ayah_index",
                        -1
                );

        if (openSurahIndex >= 0
                && openSurahIndex < surahs.length()) {

            openSurah(
                    openSurahIndex,
                    openAyahIndex
            );
        }
    }

    private void loadQuran() {
        try {
            InputStream input =
                    getAssets().open("quran.json");

            byte[] data = new byte[input.available()];
            input.read(data);
            input.close();

            String json =
                    new String(data, StandardCharsets.UTF_8);

            surahs = new JSONArray(json);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "تعذر تحميل القرآن",
                    Toast.LENGTH_LONG
            ).show();

            surahs = new JSONArray();
        }
    }

    private void buildScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(nightMode ? NIGHT_BG : CREAM);

        root.addView(
                buildHeader(),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(92)
                )
        );

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(18)
        );

        body.addView(
                buildIntro(),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        search = new EditText(this);
        search.setHint("ابحث في القرآن أو عن سورة...");
        search.setTextSize(15);
        search.setTextColor(TEXT);
        search.setHintTextColor(TEXT_SECONDARY);
        search.setSingleLine(true);
        search.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        search.setPadding(
                dp(16),
                0,
                dp(16),
                0
        );

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(WHITE);
        searchBg.setCornerRadius(dp(18));
        searchBg.setStroke(dp(1), BORDER);
        search.setBackground(searchBg);

        LinearLayout.LayoutParams searchLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );

        searchLp.topMargin = dp(14);

        body.addView(search, searchLp);

        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(14), 0, dp(20));

        scroll.addView(content);

        body.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        root.addView(
                body,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        quranMainRoot = root;
        setContentView(quranMainRoot);

        showSurahList("");

        search.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {}

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        showSearchResults(s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {}
                }
        );
    }


    private void showBookmarksCenter() {
        ArrayList<String> savedKeys = new ArrayList<>();

        android.content.SharedPreferences prefs =
                getSharedPreferences(PREF_BOOKMARKS, MODE_PRIVATE);

        java.util.Set<String> saved =
                prefs.getStringSet(KEY_BOOKMARKS, null);

        if (saved != null) {
            savedKeys.addAll(saved);
        }

        java.util.Collections.sort(
                savedKeys,
                (a, b) -> {
                    String[] aa = a.split(":");
                    String[] bb = b.split(":");

                    int sa = Integer.parseInt(aa[0]);
                    int sb = Integer.parseInt(bb[0]);

                    if (sa != sb) return Integer.compare(sa, sb);

                    return Integer.compare(
                            Integer.parseInt(aa[1]),
                            Integer.parseInt(bb[1])
                    );
                }
        );

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);

        /*
         * HEADER
         */
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(
                dp(12),
                dp(10),
                dp(16),
                dp(10)
        );

        GradientDrawable headerBg = new GradientDrawable();
        headerBg.setColor(EMERALD);
        headerBg.setCornerRadii(new float[]{
                0,0, 0,0, 0,0, 0,0
        });
        header.setBackground(headerBg);

        ImageButton back = new ImageButton(this);
        back.setImageResource(
                android.R.drawable.ic_menu_revert
        );
        back.setColorFilter(WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setPadding(
                dp(10),
                dp(10),
                dp(10),
                dp(10)
        );
        back.setOnClickListener(v -> {
            if (quranMainRoot != null) {
                setContentView(quranMainRoot);
                showSurahList("");
            }
        });

        header.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                )
        );

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setGravity(Gravity.RIGHT);

        TextView title = text(
                "المحفوظات",
                21,
                WHITE,
                true
        );
        title.setGravity(Gravity.RIGHT);

        TextView subtitle = text(
                savedKeys.size() + " آية محفوظة",
                12,
                Color.rgb(225,240,234),
                false
        );
        subtitle.setGravity(Gravity.RIGHT);

        titleBox.addView(title);
        titleBox.addView(subtitle);

        LinearLayout.LayoutParams titleLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        header.addView(titleBox, titleLp);

        TextView mark = text(
                "🔖",
                25,
                GOLD,
                false
        );
        mark.setGravity(Gravity.CENTER);

        header.addView(
                mark,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(48)
                )
        );

        root.addView(header);

        /*
         * CONTENT
         */
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout contentBox = new LinearLayout(this);
        contentBox.setOrientation(LinearLayout.VERTICAL);
        contentBox.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(24)
        );

        if (savedKeys.isEmpty()) {

            LinearLayout emptyCard =
                    new LinearLayout(this);

            emptyCard.setOrientation(
                    LinearLayout.VERTICAL
            );
            emptyCard.setGravity(Gravity.CENTER);
            emptyCard.setPadding(
                    dp(24),
                    dp(42),
                    dp(24),
                    dp(42)
            );

            GradientDrawable emptyBg =
                    new GradientDrawable();

            emptyBg.setColor(WHITE);
            emptyBg.setCornerRadius(dp(24));
            emptyBg.setStroke(dp(1), BORDER);

            emptyCard.setBackground(emptyBg);

            TextView icon = text(
                    "🔖",
                    42,
                    GOLD,
                    false
            );
            icon.setGravity(Gravity.CENTER);

            TextView emptyTitle = text(
                    "لا توجد آيات محفوظة",
                    18,
                    TEXT,
                    true
            );
            emptyTitle.setGravity(Gravity.CENTER);

            TextView emptySubtitle = text(
                    "اضغط على أي آية أثناء القراءة\nواحفظها هنا للرجوع إليها لاحقًا",
                    13,
                    TEXT_SECONDARY,
                    false
            );
            emptySubtitle.setGravity(Gravity.CENTER);

            emptyCard.addView(icon);
            emptyCard.addView(emptyTitle);

            LinearLayout.LayoutParams emptySubLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            emptySubLp.topMargin = dp(8);
            emptyCard.addView(
                    emptySubtitle,
                    emptySubLp
            );

            contentBox.addView(
                    emptyCard,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

        } else {

            TextView section = text(
                    "آياتك المحفوظة",
                    14,
                    TEXT_SECONDARY,
                    true
            );
            section.setGravity(Gravity.RIGHT);

            contentBox.addView(
                    section,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(34)
                    )
            );

            for (String key : savedKeys) {

                String[] parts = key.split(":");

                if (parts.length != 2) continue;

                int surahIndex;
                int ayahIndex;

                try {
                    surahIndex = Integer.parseInt(parts[0]);
                    ayahIndex = Integer.parseInt(parts[1]);
                } catch (Exception ignored) {
                    continue;
                }

                try {
                    JSONObject surah =
                            surahs.getJSONObject(surahIndex);

                    JSONArray verses =
                            surah.getJSONArray("verses");

                    if (ayahIndex < 0 ||
                            ayahIndex >= verses.length()) {
                        continue;
                    }

                    JSONObject verse =
                            verses.getJSONObject(ayahIndex);

                    String surahName =
                            surah.optString(
                                    "name",
                                    "سورة"
                            );

                    String ayahText =
                            verse.optString(
                                    "text",
                                    ""
                            );

                    LinearLayout card =
                            new LinearLayout(this);

                    card.setOrientation(
                            LinearLayout.VERTICAL
                    );
                    card.setGravity(Gravity.RIGHT);
                    card.setPadding(
                            dp(16),
                            dp(14),
                            dp(16),
                            dp(14)
                    );

                    GradientDrawable cardBg =
                            new GradientDrawable();

                    cardBg.setColor(WHITE);
                    cardBg.setCornerRadius(dp(20));
                    cardBg.setStroke(
                            dp(2),
                            GOLD
                    );

                    card.setBackground(cardBg);
                    card.setClickable(true);

                    TextView meta = text(
                            "سورة " +
                            surahName +
                            " • الآية " +
                            (ayahIndex + 1),
                            12,
                            GOLD,
                            true
                    );
                    meta.setGravity(Gravity.RIGHT);

                    TextView ayah = text(
                            ayahText,
                            18,
                            TEXT,
                            false
                    );
                    ayah.setGravity(Gravity.RIGHT);
                    ayah.setLineSpacing(
                            dp(5),
                            1.0f
                    );

                    TextView open = text(
                            "فتح الآية  〈",
                            12,
                            EMERALD_DARK,
                            true
                    );
                    open.setGravity(Gravity.RIGHT);

                    card.addView(meta);

                    LinearLayout.LayoutParams ayahLp =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                    ayahLp.topMargin = dp(8);

                    card.addView(
                            ayah,
                            ayahLp
                    );

                    LinearLayout.LayoutParams openLp =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                    openLp.topMargin = dp(10);

                    card.addView(
                            open,
                            openLp
                    );

                    final int savedSurahIndex =
                            surahIndex;

                    final int savedAyahIndex =
                            ayahIndex;

                    final String savedAyahText =
                            ayahText;

                    card.setOnClickListener(v ->
                            showAyahActions(
                                    surah,
                                    savedSurahIndex,
                                    savedAyahIndex,
                                    savedAyahText
                            )
                    );

                    LinearLayout.LayoutParams cardLp =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                    cardLp.bottomMargin = dp(12);

                    contentBox.addView(
                            card,
                            cardLp
                    );

                } catch (Exception ignored) {
                }
            }
        }

        scroll.addView(contentBox);

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

    private View buildHeader() {
        LinearLayout header =
                new LinearLayout(this);
        header.setOrientation(
                LinearLayout.HORIZONTAL
        );
        header.setGravity(
                Gravity.CENTER_VERTICAL
        );
        header.setPadding(
                dp(10),
                dp(9),
                dp(12),
                dp(9)
        );

        GradientDrawable bg =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                EMERALD_DARK,
                                EMERALD
                        }
                );
        bg.setCornerRadii(new float[]{
                0,0,
                0,0,
                0,0,
                0,0
        });
        header.setBackground(bg);

        ImageButton back =
                new ImageButton(this);
        back.setImageResource(
                android.R.drawable.ic_menu_revert
        );
        back.setColorFilter(WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setPadding(
                dp(8),
                dp(8),
                dp(8),
                dp(8)
        );
        back.setOnClickListener(
                v -> finish()
        );

        header.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(46),
                        dp(46)
                )
        );

        LinearLayout titles =
                new LinearLayout(this);
        titles.setOrientation(
                LinearLayout.VERTICAL
        );
        titles.setGravity(Gravity.RIGHT);
        titles.setPadding(
                dp(8),
                0,
                dp(4),
                0
        );

        TextView title =
                text(
                        "القرآن الكريم",
                        22,
                        WHITE,
                        true
                );
        title.setGravity(Gravity.RIGHT);

        TextView subtitle =
                text(
                        "كتاب الله • نور وهدى",
                        12,
                        Color.rgb(225,240,234),
                        false
                );
        subtitle.setGravity(Gravity.RIGHT);

        titles.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(30)
                )
        );

        titles.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(22)
                )
        );

        header.addView(
                titles,
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1f
                )
        );

        // مركز المحفوظات
        LinearLayout bookmarksButton =
                new LinearLayout(this);
        bookmarksButton.setOrientation(
                LinearLayout.VERTICAL
        );
        bookmarksButton.setGravity(Gravity.CENTER);
        bookmarksButton.setPadding(
                dp(5),
                dp(2),
                dp(5),
                dp(2)
        );
        bookmarksButton.setClickable(true);

        GradientDrawable bookmarkBg =
                new GradientDrawable();
        bookmarkBg.setColor(
                Color.argb(32, 255, 255, 255)
        );
        bookmarkBg.setCornerRadius(dp(15));
        bookmarkBg.setStroke(
                dp(1),
                Color.argb(55, 255, 255, 255)
        );
        bookmarksButton.setBackground(bookmarkBg);

        TextView bookmarkIcon =
                text(
                        "🔖",
                        21,
                        GOLD,
                        false
                );
        bookmarkIcon.setGravity(Gravity.CENTER);

        android.content.SharedPreferences bookmarkPrefs =
                getSharedPreferences(
                        PREF_BOOKMARKS,
                        MODE_PRIVATE
                );

        java.util.Set<String> savedBookmarks =
                bookmarkPrefs.getStringSet(
                        KEY_BOOKMARKS,
                        null
                );

        int bookmarkCount =
                savedBookmarks == null
                        ? 0
                        : savedBookmarks.size();

        TextView bookmarkCountText =
                text(
                        String.valueOf(bookmarkCount),
                        10,
                        WHITE,
                        true
                );
        bookmarkCountText.setGravity(Gravity.CENTER);

        bookmarksButton.addView(
                bookmarkIcon,
                new LinearLayout.LayoutParams(
                        dp(28),
                        dp(27)
                )
        );

        bookmarksButton.addView(
                bookmarkCountText,
                new LinearLayout.LayoutParams(
                        dp(28),
                        dp(18)
                )
        );

        bookmarksButton.setOnClickListener(
                v -> showBookmarksCenter()
        );

        header.addView(
                bookmarksButton,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(46)
                )
        );

        return header;
    }

    private View buildIntro() {
        LinearLayout card =
                new LinearLayout(this);
        card.setOrientation(
                LinearLayout.HORIZONTAL
        );
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(
                dp(16),
                dp(15),
                dp(16),
                dp(15)
        );

        GradientDrawable bg =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.rgb(250,252,250),
                                Color.rgb(239,247,242)
                        }
                );
        bg.setCornerRadius(dp(22));
        bg.setStroke(
                dp(1),
                Color.rgb(218,230,222)
        );
        card.setBackground(bg);
        card.setElevation(dp(2));

        LinearLayout textBox =
                new LinearLayout(this);
        textBox.setOrientation(
                LinearLayout.VERTICAL
        );
        textBox.setGravity(Gravity.RIGHT);

        TextView arabic =
                text(
                        "﴿ وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا ﴾",
                        20,
                        EMERALD_DARK,
                        true
                );
        arabic.setGravity(Gravity.RIGHT);

        TextView info =
                text(
                        "القرآن الكريم كاملًا • 114 سورة",
                        12,
                        TEXT_SECONDARY,
                        false
                );
        info.setGravity(Gravity.RIGHT);

        TextView hint =
                text(
                        "اقرأ، استمع، واحفظ موضعك بسهولة",
                        11,
                        EMERALD,
                        false
                );
        hint.setGravity(Gravity.RIGHT);

        textBox.addView(
                arabic,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(30)
                )
        );

        textBox.addView(
                info,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(22)
                )
        );

        textBox.addView(
                hint,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(20)
                )
        );

        card.addView(
                textBox,
                new LinearLayout.LayoutParams(
                        0,
                        dp(72),
                        1f
                )
        );

        TextView icon =
                text(
                        "☾",
                        34,
                        GOLD,
                        true
                );
        icon.setGravity(Gravity.CENTER);

        GradientDrawable iconBg =
                new GradientDrawable();
        iconBg.setColor(
                Color.rgb(248,241,220)
        );
        iconBg.setShape(
                GradientDrawable.OVAL
        );
        iconBg.setStroke(
                dp(1),
                Color.rgb(232,216,171)
        );
        icon.setBackground(iconBg);

        LinearLayout.LayoutParams iconLp =
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(58)
                );
        iconLp.leftMargin = dp(12);

        card.addView(
                icon,
                iconLp
        );

        return card;
    }

    private String normalizeArabicForSearch(String value) {
        if (value == null) return "";

        String normalized = Normalizer.normalize(
                value.toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );

        // إزالة التشكيل للبحث فقط، مع إبقاء نص القرآن الأصلي كما هو.
        normalized = normalized.replaceAll("\\p{M}+", "");

        // توحيد بعض أشكال الحروف العربية الشائعة.
        normalized = normalized
                .replace("ٱ", "ا")
                .replace("أ", "ا")
                .replace("إ", "ا")
                .replace("آ", "ا")
                .replace("ؤ", "و")
                .replace("ئ", "ي")
                .replace("ى", "ي")
                .replace("ـ", "");

        return normalized.trim().replaceAll("\\s+", " ");
    }

    private boolean matchesArabicSearch(
            String text,
            String normalizedQuery
    ) {
        if (normalizedQuery == null || normalizedQuery.isEmpty()) {
            return false;
        }

        return normalizeArabicForSearch(text)
                .contains(normalizedQuery);
    }

    private void showSearchResults(String query) {
        if (query == null || query.trim().isEmpty()) {
            showSurahList("");
            return;
        }

        content.removeAllViews();

        String normalizedQuery =
                normalizeArabicForSearch(query);

        ArrayList<Integer> matchingSurahs =
                new ArrayList<>();

        // --------------------------------------------------------
        // البحث في أسماء السور وأرقامها
        // --------------------------------------------------------
        if (surahs != null) {
            for (int i = 0; i < surahs.length(); i++) {
                try {
                    JSONObject surah =
                            surahs.getJSONObject(i);

                    String name =
                            surah.optString("name", "");

                    String english =
                            surah.optString(
                                    "transliteration",
                                    ""
                            );

                    boolean numberMatch =
                            String.valueOf(i + 1)
                                    .equals(query.trim());

                    boolean nameMatch =
                            matchesArabicSearch(
                                    name,
                                    normalizedQuery
                            );

                    boolean englishMatch =
                            normalizeArabicForSearch(
                                    english
                            ).contains(normalizedQuery);

                    if (numberMatch
                            || nameMatch
                            || englishMatch) {

                        matchingSurahs.add(i);
                    }

                } catch (Exception ignored) {}
            }
        }

        if (!matchingSurahs.isEmpty()) {

            TextView title = text(
                    "السور",
                    15,
                    EMERALD_DARK,
                    true
            );

            title.setGravity(Gravity.RIGHT);

            title.setPadding(
                    dp(4),
                    dp(4),
                    dp(4),
                    dp(8)
            );

            content.addView(
                    title,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            for (Integer index : matchingSurahs) {
                try {
                    content.addView(
                            buildSurahCard(
                                    surahs.getJSONObject(index),
                                    index
                            )
                    );
                } catch (Exception ignored) {}
            }
        }

        // --------------------------------------------------------
        // البحث داخل جميع الآيات
        // --------------------------------------------------------
        final int MAX_AYAH_RESULTS = 40;

        ArrayList<JSONObject> matchingAyahs =
                new ArrayList<>();

        ArrayList<Integer> matchingSurahIndexes =
                new ArrayList<>();

        ArrayList<Integer> matchingAyahIndexes =
                new ArrayList<>();

        if (surahs != null) {

            outer:
            for (int i = 0;
                 i < surahs.length();
                 i++) {

                try {
                    JSONObject surah =
                            surahs.getJSONObject(i);

                    JSONArray verses =
                            surah.optJSONArray("verses");

                    if (verses == null) continue;

                    for (int j = 0;
                         j < verses.length();
                         j++) {

                        JSONObject verse =
                                verses.getJSONObject(j);

                        String verseText =
                                verse.optString(
                                        "text",
                                        ""
                                );

                        if (matchesArabicSearch(
                                verseText,
                                normalizedQuery
                        )) {

                            matchingAyahs.add(verse);
                            matchingSurahIndexes.add(i);
                            matchingAyahIndexes.add(j);

                            if (matchingAyahs.size()
                                    >= MAX_AYAH_RESULTS) {
                                break outer;
                            }
                        }
                    }

                } catch (Exception ignored) {}
            }
        }

        if (!matchingAyahs.isEmpty()) {

            TextView title = text(
                    matchingSurahs.isEmpty()
                            ? "نتائج من القرآن"
                            : "آيات من القرآن",
                    15,
                    EMERALD_DARK,
                    true
            );

            title.setGravity(Gravity.RIGHT);

            title.setPadding(
                    dp(4),
                    matchingSurahs.isEmpty()
                            ? dp(4)
                            : dp(18),
                    dp(4),
                    dp(8)
            );

            content.addView(
                    title,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            for (int i = 0;
                 i < matchingAyahs.size();
                 i++) {

                try {
                    content.addView(
                            buildAyahSearchCard(
                                    matchingAyahs.get(i),
                                    matchingSurahIndexes.get(i),
                                    matchingAyahIndexes.get(i)
                            )
                    );
                } catch (Exception ignored) {}
            }

            if (matchingAyahs.size()
                    >= MAX_AYAH_RESULTS) {

                TextView more = text(
                        "تم عرض أول "
                                + MAX_AYAH_RESULTS
                                + " نتيجة",
                        12,
                        TEXT_SECONDARY,
                        false
                );

                more.setGravity(Gravity.CENTER);

                more.setPadding(
                        dp(8),
                        dp(10),
                        dp(8),
                        dp(8)
                );

                content.addView(
                        more,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                );
            }
        }

        // --------------------------------------------------------
        // لا توجد نتائج
        // --------------------------------------------------------
        if (matchingSurahs.isEmpty()
                && matchingAyahs.isEmpty()) {

            LinearLayout emptyBox =
                    new LinearLayout(this);

            emptyBox.setOrientation(
                    LinearLayout.VERTICAL
            );

            emptyBox.setGravity(Gravity.CENTER);

            emptyBox.setPadding(
                    dp(20),
                    dp(32),
                    dp(20),
                    dp(32)
            );

            GradientDrawable emptyBg =
                    new GradientDrawable();

            emptyBg.setColor(WHITE);
            emptyBg.setCornerRadius(dp(20));
            emptyBg.setStroke(
                    dp(1),
                    BORDER
            );

            emptyBox.setBackground(emptyBg);

            TextView icon = text(
                    "⌕",
                    34,
                    GOLD,
                    true
            );

            icon.setGravity(Gravity.CENTER);

            TextView title = text(
                    "لا توجد نتائج",
                    17,
                    TEXT,
                    true
            );

            title.setGravity(Gravity.CENTER);

            TextView subtitle = text(
                    "جرّب كلمة أخرى أو ابحث باسم السورة",
                    13,
                    TEXT_SECONDARY,
                    false
            );

            subtitle.setGravity(Gravity.CENTER);

            subtitle.setPadding(
                    0,
                    dp(6),
                    0,
                    0
            );

            emptyBox.addView(icon);
            emptyBox.addView(title);
            emptyBox.addView(subtitle);

            content.addView(
                    emptyBox,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );
        }
    }

    private View buildAyahSearchCard(
            JSONObject verse,
            int surahIndex,
            int ayahIndex
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(Gravity.RIGHT);

        card.setPadding(
                dp(16),
                dp(15),
                dp(16),
                dp(15)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(WHITE);
        bg.setCornerRadius(dp(20));
        bg.setStroke(dp(1), BORDER);

        card.setBackground(bg);
        card.setElevation(dp(2));

        JSONObject surah = null;

        try {
            surah = surahs.getJSONObject(
                    surahIndex
            );
        } catch (Exception ignored) {}

        String surahName =
                surah != null
                        ? surah.optString(
                                "name",
                                ""
                        )
                        : "";

        TextView meta = text(
                "سورة "
                        + surahName
                        + "  •  الآية "
                        + (ayahIndex + 1),
                12,
                EMERALD,
                true
        );

        meta.setGravity(Gravity.RIGHT);

        TextView verseText = text(
                verse.optString(
                        "text",
                        ""
                ),
                19,
                TEXT,
                false
        );

        verseText.setGravity(Gravity.RIGHT);

        verseText.setLineSpacing(
                dp(4),
                1.12f
        );

        verseText.setPadding(
                0,
                dp(9),
                0,
                dp(7)
        );

        TextView action = text(
                "فتح الآية  〈",
                12,
                GOLD,
                true
        );

        action.setGravity(Gravity.RIGHT);

        card.addView(
                meta,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        card.addView(
                verseText,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        card.addView(
                action,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout.LayoutParams cardLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardLp.setMargins(
                0,
                0,
                0,
                dp(10)
        );

        card.setLayoutParams(cardLp);

        final int finalSurahIndex =
                surahIndex;

        final int finalAyahIndex =
                ayahIndex;

        card.setOnClickListener(v ->
                openSurah(
                        finalSurahIndex,
                        finalAyahIndex
                )
        );

        return card;
    }

    private void showSurahList(String query) {

        content.removeAllViews();

        if (surahs == null) {
            return;
        }

        String q = query.toLowerCase();

        int found = 0;

        for (int i = 0; i < surahs.length(); i++) {

            try {

                JSONObject surah =
                        surahs.getJSONObject(i);

                String name =
                        surah.optString("name", "");

                String english =
                        surah.optString("transliteration", "");

                if (!q.isEmpty()
                        && !name.toLowerCase().contains(q)
                        && !english.toLowerCase().contains(q)
                        && !String.valueOf(i + 1).equals(q)) {
                    continue;
                }

                content.addView(
                        buildSurahCard(
                                surah,
                                i
                        )
                );

                found++;

            } catch (Exception ignored) {
            }
        }

        if (found == 0) {

            TextView empty =
                    text(
                            "لم يتم العثور على سورة",
                            15,
                            TEXT_SECONDARY,
                            false
                    );

            empty.setGravity(Gravity.CENTER);

            content.addView(
                    empty,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(80)
                    )
            );
        }
    }

    private View buildSurahCard(
            JSONObject surah,
            int index
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
                dp(13),
                dp(10),
                dp(13),
                dp(10)
        );

        GradientDrawable bg =
                new GradientDrawable();
        bg.setColor(WHITE);
        bg.setCornerRadius(dp(20));
        bg.setStroke(
                dp(1),
                Color.rgb(224,231,226)
        );
        card.setBackground(bg);
        card.setElevation(dp(1));

        TextView number =
                text(
                        String.valueOf(index + 1),
                        12,
                        EMERALD_DARK,
                        true
                );
        number.setGravity(Gravity.CENTER);

        GradientDrawable numberBg =
                new GradientDrawable();
        numberBg.setColor(
                Color.rgb(231,244,238)
        );
        numberBg.setShape(
                GradientDrawable.OVAL
        );
        numberBg.setStroke(
                dp(1),
                Color.rgb(207,226,214)
        );
        number.setBackground(numberBg);

        card.addView(
                number,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                )
        );

        LinearLayout names =
                new LinearLayout(this);
        names.setOrientation(
                LinearLayout.VERTICAL
        );
        names.setGravity(Gravity.RIGHT);

        String name =
                surah.optString(
                        "name",
                        "سورة"
                );

        String english =
                surah.optString(
                        "transliteration",
                        ""
                );

        int totalVerses =
                surah.optInt(
                        "total_verses",
                        0
                );

        TextView arabic =
                text(
                        "سورة " + name,
                        18,
                        TEXT,
                        true
                );
        arabic.setGravity(Gravity.RIGHT);

        TextView details =
                text(
                        english
                                + (
                                totalVerses > 0
                                        ? "  •  "
                                        + totalVerses
                                        + " آية"
                                        : ""
                        ),
                        11,
                        TEXT_SECONDARY,
                        false
                );
        details.setGravity(Gravity.RIGHT);

        names.addView(
                arabic,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(29)
                )
        );

        names.addView(
                details,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(20)
                )
        );

        LinearLayout.LayoutParams namesLp =
                new LinearLayout.LayoutParams(
                        0,
                        dp(50),
                        1f
                );

        namesLp.setMargins(
                dp(12),
                0,
                dp(10),
                0
        );

        card.addView(
                names,
                namesLp
        );

        TextView arrow =
                text(
                        "‹",
                        24,
                        Color.rgb(155,166,159),
                        false
                );
        arrow.setGravity(Gravity.CENTER);

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        dp(22),
                        dp(42)
                )
        );

        LinearLayout.LayoutParams cardLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(66)
                );

        cardLp.bottomMargin = dp(9);

        card.setOnClickListener(
                v -> openSurah(index)
        );

        return card;
    }

    private void openSurah(int index) {
        final boolean compactReadingMode =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                ).getBoolean(
                        "compact_quran_reading",
                        true
                );

        if (compactReadingMode) {
            android.content.SharedPreferences prefs =
                    getSharedPreferences(
                            PREF_READING,
                            MODE_PRIVATE
                    );

            int savedSurahIndex =
                    prefs.getInt(
                            KEY_SURAH_INDEX,
                            -1
                    );

            int savedAyahIndex =
                    prefs.getInt(
                            KEY_AYAH_INDEX,
                            -1
                    );

            if (savedSurahIndex == index
                    && savedAyahIndex >= 0) {
                openSurah(
                        index,
                        savedAyahIndex
                );
                return;
            }
        }

        openSurah(index, -1);
    }

    private void openSurah(int index, int ayahIndex) {
        try {
            JSONObject surah =
                    surahs.getJSONObject(index);

            JSONArray verses =
                    surah.optJSONArray("verses");

            if (verses == null) {
                Toast.makeText(
                        this,
                        "صيغة بيانات القرآن مختلفة",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            showReadingScreen(
                    surah,
                    verses,
                    index,
                    ayahIndex
            );

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "تعذر فتح السورة",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void saveReadingPosition(
            JSONObject surah,
            int surahIndex,
            int ayahIndex
    ) {
        getSharedPreferences(PREF_READING, MODE_PRIVATE)
                .edit()
                .putInt(KEY_SURAH_INDEX, surahIndex)
                .putInt(KEY_AYAH_INDEX, ayahIndex)
                .putString(
                        KEY_SURAH_NAME,
                        surah.optString("name", "")
                )
                .apply();

        Toast.makeText(
                this,
                "تم حفظ موضع القراءة ✓",
                Toast.LENGTH_SHORT
        ).show();
    }

    private String getBookmarkKey(
            int surahIndex,
            int ayahIndex
    ) {
        return surahIndex + ":" + ayahIndex;
    }

    private boolean isBookmarkedAyah(
            int surahIndex,
            int ayahIndex
    ) {
        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        PREF_BOOKMARKS,
                        MODE_PRIVATE
                );

        java.util.Set<String> saved =
                prefs.getStringSet(
                        KEY_BOOKMARKS,
                        new java.util.HashSet<>()
                );

        return saved.contains(
                getBookmarkKey(surahIndex, ayahIndex)
        );
    }

    private void toggleAyahBookmark(
            int surahIndex,
            int ayahIndex
    ) {
        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        PREF_BOOKMARKS,
                        MODE_PRIVATE
                );

        java.util.Set<String> saved =
                new java.util.HashSet<>(
                        prefs.getStringSet(
                                KEY_BOOKMARKS,
                                new java.util.HashSet<>()
                        )
                );

        String key =
                getBookmarkKey(
                        surahIndex,
                        ayahIndex
                );

        boolean added;

        if (saved.contains(key)) {
            saved.remove(key);
            added = false;
        } else {
            saved.add(key);
            added = true;
        }

        prefs.edit()
                .putStringSet(
                        KEY_BOOKMARKS,
                        saved
                )
                .apply();

        Toast.makeText(
                this,
                added
                        ? "تم حفظ الآية 🔖"
                        : "تم إلغاء حفظ الآية",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void copyAyahText(String ayahText) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager)
                        getSystemService(
                                CLIPBOARD_SERVICE
                        );

        if (clipboard != null) {
            clipboard.setPrimaryClip(
                    android.content.ClipData.newPlainText(
                            "آية من القرآن",
                            ayahText
                    )
            );

            Toast.makeText(
                    this,
                    "تم نسخ الآية ✓",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void shareAyahText(
            String surahName,
            int ayahNumber,
            String ayahText
    ) {
        android.content.Intent shareIntent =
                new android.content.Intent(
                        android.content.Intent.ACTION_SEND
                );

        shareIntent.setType("text/plain");

        shareIntent.putExtra(
                android.content.Intent.EXTRA_TEXT,
                ayahText
                        + "\n\n"
                        + "سورة "
                        + surahName
                        + " • الآية "
                        + ayahNumber
                        + "\n"
                        + "لعلها المنجيه"
        );

        startActivity(
                android.content.Intent.createChooser(
                        shareIntent,
                        "مشاركة الآية"
                )
        );
    }

    private void showAyahActions(
            JSONObject surah,
            int surahIndex,
            int ayahIndex,
            String ayahText
    ) {
        final android.app.Dialog dialog =
                new android.app.Dialog(this);

        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setPadding(
                dp(20),
                dp(10),
                dp(20),
                dp(20)
        );

        GradientDrawable boxBg =
                new GradientDrawable();

        boxBg.setColor(
                nightMode
                        ? NIGHT_CARD
                        : WHITE
        );

        boxBg.setCornerRadius(
                dp(28)
        );

        box.setBackground(boxBg);

        TextView handle = text(
                "━━━━",
                14,
                nightMode
                        ? NIGHT_SECONDARY
                        : TEXT_SECONDARY,
                true
        );

        handle.setGravity(
                Gravity.CENTER
        );

        box.addView(
                handle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(28)
                )
        );

        TextView title = text(
                "آية من سورة "
                        + surah.optString(
                                "name",
                                ""
                        ),
                16,
                nightMode
                        ? NIGHT_TEXT
                        : TEXT,
                true
        );

        title.setGravity(
                Gravity.RIGHT
        );

        title.setPadding(
                0,
                dp(4),
                0,
                dp(12)
        );

        box.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        boolean bookmarked =
                isBookmarkedAyah(
                        surahIndex,
                        ayahIndex
                );

        TextView bookmark = text(
                bookmarked
                        ? "🔖  إلغاء حفظ الآية"
                        : "🔖  حفظ الآية",
                15,
                bookmarked
                        ? EMERALD
                        : (nightMode
                                ? NIGHT_TEXT
                                : TEXT),
                true
        );

        TextView copy = text(
                "📋  نسخ الآية",
                15,
                nightMode
                        ? NIGHT_TEXT
                        : TEXT,
                true
        );

        TextView share = text(
                "↗  مشاركة الآية",
                15,
                nightMode
                        ? NIGHT_TEXT
                        : TEXT,
                true
        );

        TextView play = text(
                "▶  تشغيل السورة",
                15,
                nightMode
                        ? NIGHT_TEXT
                        : TEXT,
                true
        );

        final boolean compactReadingMode =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                ).getBoolean(
                        "compact_quran_reading",
                        true
                );

        TextView savePosition = null;

        if (compactReadingMode) {
            boolean savedHere = isSavedPosition(
                    surahIndex,
                    ayahIndex
            );

            savePosition = text(
                    savedHere
                            ? "📍  موضع القراءة الحالي"
                            : "📍  حفظ موضع القراءة",
                    15,
                    savedHere
                            ? EMERALD
                            : (nightMode
                                    ? NIGHT_TEXT
                                    : TEXT),
                    true
            );
        }

        TextView[] actions;

        if (savePosition != null) {
            actions = new TextView[]{
                    bookmark,
                    savePosition,
                    copy,
                    share,
                    play
            };
        } else {
            actions = new TextView[]{
                    bookmark,
                    copy,
                    share,
                    play
            };
        }

        for (TextView action : actions) {
            action.setGravity(
                    Gravity.RIGHT
                        | Gravity.CENTER_VERTICAL
            );

            action.setPadding(
                    dp(14),
                    0,
                    dp(14),
                    0
            );

            GradientDrawable actionBg =
                    new GradientDrawable();

            actionBg.setColor(
                    nightMode
                            ? NIGHT_BG
                            : CREAM
            );

            actionBg.setCornerRadius(
                    dp(16)
            );

            action.setBackground(
                    actionBg
            );

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(52)
                    );

            lp.setMargins(
                    0,
                    dp(4),
                    0,
                    dp(4)
            );

            box.addView(
                    action,
                    lp
            );
        }

        bookmark.setOnClickListener(v -> {
            toggleAyahBookmark(
                    surahIndex,
                    ayahIndex
            );

            dialog.dismiss();

            openSurah(
                    surahIndex,
                    ayahIndex
            );
        });

        copy.setOnClickListener(v -> {
            copyAyahText(ayahText);
            dialog.dismiss();
        });

        share.setOnClickListener(v -> {
            shareAyahText(
                    surah.optString(
                            "name",
                            ""
                    ),
                    ayahIndex + 1,
                    ayahText
            );

            dialog.dismiss();
        });

        play.setOnClickListener(v -> {
            dialog.dismiss();
            toggleQuranAudio(surahIndex);
        });

        if (savePosition != null) {
            savePosition.setOnClickListener(v -> {
                saveReadingPosition(
                        surah,
                        surahIndex,
                        ayahIndex
                );

                dialog.dismiss();

                openSurah(
                        surahIndex,
                        ayahIndex
                );
            });
        }

        dialog.setContentView(box);
        dialog.show();

        android.view.Window window =
                dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            window.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            window.setGravity(
                    Gravity.BOTTOM
            );

            android.view.WindowManager.LayoutParams params =
                    window.getAttributes();

            params.width =
                    LinearLayout.LayoutParams.MATCH_PARENT;

            params.height =
                    LinearLayout.LayoutParams.WRAP_CONTENT;

            params.dimAmount = 0.35f;

            window.setAttributes(params);

            window.addFlags(
                    android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );
        }
    }

    private boolean isSavedPosition(
            int surahIndex,
            int ayahIndex
    ) {
        android.content.SharedPreferences prefs =
                getSharedPreferences(PREF_READING, MODE_PRIVATE);

        return prefs.getInt(KEY_SURAH_INDEX, -1) == surahIndex
                && prefs.getInt(KEY_AYAH_INDEX, -1) == ayahIndex;
    }

    private void toggleNightMode() {
        nightMode = !nightMode;

        getSharedPreferences(
                "app_settings",
                MODE_PRIVATE
        )
                .edit()
                .putBoolean(
                        "quran_night_mode",
                        nightMode
                )
                .apply();

        Toast.makeText(
                this,
                nightMode ? "تم تفعيل القراءة الليلية" : "تم إيقاف القراءة الليلية",
                Toast.LENGTH_SHORT
        ).show();
    }

    private View buildContinueReadingBar(
            JSONObject currentSurah,
            int currentSurahIndex,
            JSONArray currentVerses
    ) {
        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        PREF_READING,
                        MODE_PRIVATE
                );

        boolean resumeEnabled =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                ).getBoolean(
                        "reading_resume_enabled",
                        true
                );

        if (!resumeEnabled) {
            return null;
        }

        int savedSurahIndex =
                prefs.getInt(KEY_SURAH_INDEX, -1);

        int savedAyahIndex =
                prefs.getInt(KEY_AYAH_INDEX, -1);

        if (savedSurahIndex < 0
                || savedAyahIndex < 0
                || savedSurahIndex >= surahs.length()) {
            return null;
        }

        if (savedSurahIndex == currentSurahIndex
                && savedAyahIndex < currentVerses.length()) {
            return null;
        }

        JSONObject savedSurah;

        try {
            savedSurah =
                    surahs.getJSONObject(
                            savedSurahIndex
                    );
        } catch (Exception e) {
            return null;
        }

        String savedName =
                savedSurah.optString(
                        "name",
                        ""
                );

        LinearLayout bar =
                new LinearLayout(this);

        bar.setOrientation(
                LinearLayout.HORIZONTAL
        );

        bar.setGravity(
                Gravity.CENTER_VERTICAL
        );

        bar.setPadding(
                dp(14),
                dp(10),
                dp(14),
                dp(10)
        );

        GradientDrawable barBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                Color.rgb(247, 250, 247),
                                Color.rgb(237, 246, 240)
                        }
                );

        barBg.setCornerRadius(dp(16));

        barBg.setStroke(
                dp(1),
                Color.rgb(218, 230, 222)
        );

        bar.setBackground(barBg);
        bar.setElevation(dp(2));

        TextView icon =
                text(
                        "↩",
                        20,
                        EMERALD_DARK,
                        true
                );

        icon.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams iconLp =
                new LinearLayout.LayoutParams(
                        dp(36),
                        dp(36)
                );

        bar.addView(
                icon,
                iconLp
        );

        LinearLayout textBox =
                new LinearLayout(this);

        textBox.setOrientation(
                LinearLayout.VERTICAL
        );

        textBox.setGravity(
                Gravity.RIGHT
        );

        TextView title =
                text(
                        "متابعة القراءة",
                        14,
                        EMERALD_DARK,
                        true
                );

        title.setGravity(
                Gravity.RIGHT
        );

        TextView subtitle =
                text(
                        "سورة "
                                + savedName
                                + "  •  الآية "
                                + (savedAyahIndex + 1),
                        11,
                        TEXT_SECONDARY,
                        false
                );

        subtitle.setGravity(
                Gravity.RIGHT
        );

        textBox.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(23)
                )
        );

        textBox.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(19)
                )
        );

        LinearLayout.LayoutParams textLp =
                new LinearLayout.LayoutParams(
                        0,
                        dp(44),
                        1f
                );

        textLp.setMargins(
                dp(10),
                0,
                dp(10),
                0
        );

        bar.addView(
                textBox,
                textLp
        );

        TextView open =
                text(
                        "فتح",
                        12,
                        EMERALD,
                        true
                );

        open.setGravity(
                Gravity.CENTER
        );

        GradientDrawable openBg =
                new GradientDrawable();

        openBg.setColor(
                Color.rgb(231, 244, 238)
        );

        openBg.setCornerRadius(dp(11));

        openBg.setStroke(
                dp(1),
                Color.rgb(207, 226, 214)
        );

        open.setBackground(openBg);

        bar.addView(
                open,
                new LinearLayout.LayoutParams(
                        dp(52),
                        dp(36)
                )
        );

        bar.setOnClickListener(v -> {
            openSurah(
                    savedSurahIndex,
                    savedAyahIndex
            );
        });

        return bar;
    }

    private void showReadingScreen(
            JSONObject surah,
            JSONArray verses,
            int index
    ) {
        showReadingScreen(
                surah,
                verses,
                index,
                -1
        );
    }

    private void showReadingScreen(
            JSONObject surah,
            JSONArray verses,
            int index,
            int targetAyahIndex
    ) {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setBackgroundColor(
                nightMode ? NIGHT_BG : CREAM
        );

        LinearLayout header =
                new LinearLayout(this);

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        header.setPadding(
                dp(10),
                dp(7),
                dp(12),
                dp(7)
        );
        header.setElevation(dp(4));

        GradientDrawable headerBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                EMERALD_DARK,
                                EMERALD
                        }
                );

        headerBg.setCornerRadii(
                new float[]{
                        0, 0,
                        0, 0,
                        0, 0,
                        0, 0
                }
        );

        header.setBackground(headerBg);

        ImageButton back =
                new ImageButton(this);

        back.setImageResource(
                android.R.drawable.ic_menu_revert
        );

        back.setColorFilter(WHITE);
        back.setBackgroundColor(Color.TRANSPARENT);

        back.setOnClickListener(
                v -> buildScreen()
        );

        header.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                )
        );

        TextView title =
                text(
                        "سورة " +
                        surah.optString(
                                "name",
                                ""
                        ),
                        21,
                        WHITE,
                        true
                );

        title.setGravity(Gravity.RIGHT);

        header.addView(
                title,
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1f
                )
        );

        TextView fullScreenButton = text(
                readingFullScreen ? "⛶" : "⛶",
                24,
                WHITE,
                false
        );
        fullScreenButton.setGravity(Gravity.CENTER);
        fullScreenButton.setContentDescription("ملء الشاشة");
        fullScreenButton.setOnClickListener(v -> {
            readingFullScreen = !readingFullScreen;
            showReadingScreen(surah, verses, index, targetAyahIndex);
        });

        TextView nightButton = text(
                nightMode ? "☀  نهاري" : "☾  ليلي",
                15,
                WHITE,
                false
        );
        nightButton.setGravity(Gravity.CENTER);
        nightButton.setPadding(dp(8), 0, dp(8), 0);
        nightButton.setOnClickListener(v -> {
            toggleNightMode();
            showReadingScreen(surah, verses, index, targetAyahIndex);
        });

        header.addView(
                fullScreenButton,
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                )
        );

        header.addView(
                nightButton,
                new LinearLayout.LayoutParams(
                        dp(82),
                        dp(48)
                )
        );

        root.addView(
                header,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(68)
                )
        );

        // بطاقة معلومات السورة
        LinearLayout surahInfoCard =
                new LinearLayout(this);

        surahInfoCard.setOrientation(
                LinearLayout.HORIZONTAL
        );

        surahInfoCard.setGravity(
                Gravity.CENTER_VERTICAL
        );

        surahInfoCard.setPadding(
                dp(17),
                dp(14),
                dp(17),
                dp(14)
        );

        GradientDrawable surahInfoBg =
                new GradientDrawable();

        surahInfoBg.setColor(
                nightMode
                        ? NIGHT_CARD
                        : Color.rgb(247, 250, 247)
        );

        surahInfoBg.setCornerRadius(
                dp(20)
        );

        surahInfoBg.setStroke(
                dp(1),
                nightMode
                        ? EMERALD_DARK
                        : Color.rgb(215, 229, 220)
        );

        surahInfoCard.setBackground(
                surahInfoBg
        );
        surahInfoCard.setElevation(dp(2));

        TextView surahBadge =
                text(
                        "۞",
                        28,
                        GOLD,
                        true
                );

        surahBadge.setGravity(
                Gravity.CENTER
        );

        surahInfoCard.addView(
                surahBadge,
                new LinearLayout.LayoutParams(
                        dp(46),
                        dp(46)
                )
        );

        LinearLayout surahInfoText =
                new LinearLayout(this);

        surahInfoText.setOrientation(
                LinearLayout.VERTICAL
        );

        surahInfoText.setGravity(
                Gravity.RIGHT
        );

        TextView surahInfoTitle =
                text(
                        "سورة " +
                        surah.optString(
                                "name",
                                ""
                        ),
                        17,
                        nightMode
                                ? NIGHT_TEXT
                                : TEXT,
                        true
                );

        surahInfoTitle.setGravity(
                Gravity.RIGHT
        );

        String surahType =
                surah.optString(
                        "type",
                        ""
                );

        String typeArabic;

        if ("Meccan".equalsIgnoreCase(surahType)
                || "مكية".equals(surahType)) {
            typeArabic = "مكية";
        } else if ("Medinan".equalsIgnoreCase(surahType)
                || "مدنية".equals(surahType)) {
            typeArabic = "مدنية";
        } else {
            typeArabic = surahType;
        }

        int totalVerses =
                surah.optInt(
                        "total_verses",
                        verses.length()
                );

        TextView surahInfoSubtitle =
                text(
                        typeArabic +
                        "  •  " +
                        totalVerses +
                        " آية",
                        12,
                        nightMode
                                ? NIGHT_SECONDARY
                                : TEXT_SECONDARY,
                        false
                );

        surahInfoSubtitle.setGravity(
                Gravity.RIGHT
        );

        surahInfoText.addView(
                surahInfoTitle
        );

        LinearLayout.LayoutParams infoSubLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        infoSubLp.topMargin = dp(3);

        surahInfoText.addView(
                surahInfoSubtitle,
                infoSubLp
        );

        LinearLayout.LayoutParams infoTextLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        infoTextLp.setMargins(
                dp(12),
                0,
                0,
                0
        );

        surahInfoCard.addView(
                surahInfoText,
                infoTextLp
        );

        // فاصل بصري بين معلومات السورة ومؤشر التقدم
        View progressDivider = new View(this);

        progressDivider.setBackgroundColor(
                nightMode
                        ? Color.rgb(70, 74, 68)
                        : Color.rgb(226, 230, 224)
        );

        LinearLayout.LayoutParams progressDividerLp =
                new LinearLayout.LayoutParams(
                        dp(1),
                        dp(34)
                );

        progressDividerLp.gravity =
                Gravity.CENTER_VERTICAL;

        progressDividerLp.setMargins(
                dp(10),
                0,
                dp(10),
                0
        );

        surahInfoCard.addView(
                progressDivider,
                progressDividerLp
        );

        // مؤشر تقدم القراءة داخل السورة
        LinearLayout progressBox =
                new LinearLayout(this);

        progressBox.setOrientation(
                LinearLayout.VERTICAL
        );

        progressBox.setGravity(Gravity.RIGHT);

        LinearLayout progressHeader =
                new LinearLayout(this);
        progressHeader.setOrientation(
                LinearLayout.HORIZONTAL
        );
        progressHeader.setGravity(
                Gravity.CENTER_VERTICAL
        );

        int progressPercent = 0;

        if (targetAyahIndex >= 0
                && totalVerses > 0) {
            progressPercent = Math.round(
                    ((targetAyahIndex + 1) * 100f)
                            / totalVerses
            );

            progressPercent = Math.max(
                    0,
                    Math.min(
                            100,
                            progressPercent
                    )
            );
        }

        TextView progressLabel =
                text(
                        targetAyahIndex >= 0
                                ? "الآية " +
                                  (targetAyahIndex + 1) +
                                  " من " +
                                  totalVerses
                                : "ابدأ القراءة",
                        11,
                        nightMode
                                ? NIGHT_SECONDARY
                                : TEXT_SECONDARY,
                        false
                );

        progressLabel.setGravity(
                Gravity.RIGHT
        );

        TextView progressPercentText =
                text(
                        progressPercent + "%",
                        11,
                        nightMode
                                ? NIGHT_SECONDARY
                                : TEXT_SECONDARY,
                        true
                );

        progressPercentText.setGravity(
                Gravity.LEFT
        );

        progressHeader.addView(
                progressPercentText,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(24)
                )
        );

        LinearLayout.LayoutParams progressHeaderTextLp =
                new LinearLayout.LayoutParams(
                        0,
                        dp(24),
                        1f
                );

        progressHeaderTextLp.setMargins(
                dp(8),
                0,
                0,
                0
        );

        progressHeader.addView(
                progressLabel,
                progressHeaderTextLp
        );

        progressBox.addView(
                progressHeader
        );

        android.widget.ProgressBar progressBar =
                new android.widget.ProgressBar(
                        this,
                        null,
                        android.R.attr.progressBarStyleHorizontal
                );

        GradientDrawable progressTrack =
                new GradientDrawable();

        progressTrack.setColor(
                nightMode
                        ? Color.rgb(58, 62, 56)
                        : Color.rgb(232, 235, 229)
        );

        progressTrack.setCornerRadius(
                dp(8)
        );

        GradientDrawable progressFill =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                EMERALD_DARK,
                                EMERALD
                        }
                );

        progressFill.setCornerRadius(
                dp(8)
        );

        android.graphics.drawable.LayerDrawable progressDrawable =
                new android.graphics.drawable.LayerDrawable(
                        new android.graphics.drawable.Drawable[]{
                                progressTrack,
                                progressFill
                        }
                );

        progressDrawable.setId(
                0,
                android.R.id.background
        );

        progressDrawable.setId(
                1,
                android.R.id.progress
        );

        progressBar.setProgressDrawable(
                progressDrawable
        );

        progressBar.setMax(
                Math.max(1, totalVerses)
        );

        progressBar.setProgress(
                targetAyahIndex >= 0
                        ? Math.min(
                                totalVerses,
                                targetAyahIndex + 1
                        )
                        : 0
        );

        progressBox.addView(
                progressBar,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(4)
                )
        );

        LinearLayout.LayoutParams progressLp =
                new LinearLayout.LayoutParams(
                        dp(110),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        progressLp.gravity =
                Gravity.CENTER_VERTICAL;

        progressLp.setMargins(
                dp(12),
                0,
                0,
                0
        );

        surahInfoCard.addView(
                progressBox,
                progressLp
        );

        LinearLayout.LayoutParams surahInfoLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        surahInfoLp.setMargins(
                dp(12),
                dp(10),
                dp(12),
                0
        );

        root.addView(
                surahInfoCard,
                surahInfoLp
        );

        // كارت تلاوة القرآن
        LinearLayout audioCard = new LinearLayout(this);
        audioCard.setOrientation(LinearLayout.VERTICAL);
        audioCard.setPadding(
                dp(18),
                dp(17),
                dp(18),
                dp(17)
        );
        if (readingFullScreen) {
            header.setVisibility(View.GONE);
            surahInfoCard.setVisibility(View.GONE);
            audioCard.setVisibility(View.GONE);
        }


        GradientDrawable audioBg = new GradientDrawable();
        audioBg.setColor(
                nightMode
                        ? NIGHT_CARD
                        : Color.WHITE
        );
        audioBg.setCornerRadius(dp(24));
        audioBg.setStroke(
                dp(1),
                nightMode
                        ? EMERALD_DARK
                        : Color.rgb(222, 231, 225)
        );
        audioCard.setBackground(audioBg);
        audioCard.setElevation(dp(3));

        TextView audioTitle = text(
                "تلاوة القرآن",
                19,
                nightMode ? NIGHT_TEXT : TEXT,
                true
        );

        audioReciterHolder = text(
                getSelectedReciterName(),
                13,
                nightMode ? NIGHT_SECONDARY : TEXT_SECONDARY,
                false
        );

        LinearLayout audioTop = new LinearLayout(this);
        audioTop.setOrientation(
                LinearLayout.HORIZONTAL
        );
        audioTop.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout audioTexts = new LinearLayout(this);
        audioTexts.setOrientation(
                LinearLayout.VERTICAL
        );

        audioTexts.addView(
                audioTitle,
                new LinearLayout.LayoutParams(
                        0,
                        dp(30),
                        1f
                )
        );

        audioTexts.addView(
                audioReciterHolder,
                new LinearLayout.LayoutParams(
                        0,
                        dp(24),
                        1f
                )
        );

        audioTop.addView(
                audioTexts,
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1f
                )
        );

        TextView reciterButton = text(
                "تغيير القارئ",
                12,
                nightMode
                        ? Color.rgb(220,190,112)
                        : EMERALD_DARK,
                true
        );

        reciterButton.setGravity(Gravity.CENTER);

        GradientDrawable reciterButtonBg =
                new GradientDrawable();

        reciterButtonBg.setColor(
                nightMode
                        ? Color.rgb(48,50,42)
                        : Color.rgb(239,247,242)
        );

        reciterButtonBg.setCornerRadius(dp(14));

        reciterButtonBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(105,90,54)
                        : Color.rgb(211,228,217)
        );

        reciterButton.setBackground(
                reciterButtonBg
        );

        reciterButton.setOnClickListener(
                v -> showReciterChooser()
        );

        audioTop.addView(
                reciterButton,
                new LinearLayout.LayoutParams(
                        dp(118), dp(40)
                )
        );


        quranPlayButton = text(
                "▶",
                24,
                WHITE,
                true
        );
        quranPlayButton.setGravity(Gravity.CENTER);

        GradientDrawable playBg = new GradientDrawable();
        playBg.setColor(EMERALD_DARK);
        playBg.setShape(
                GradientDrawable.OVAL
        );
        playBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(142, 119, 61)
                        : Color.rgb(210, 188, 120)
        );
        quranPlayButton.setBackground(playBg);
        quranPlayButton.setElevation(dp(4));

        quranSkipBackButton = text(
                "−10",
                13,
                WHITE,
                true
        );
        quranSkipBackButton.setGravity(Gravity.CENTER);
        quranSkipBackButton.setEnabled(false);

        GradientDrawable skipBackBg = new GradientDrawable();
        skipBackBg.setColor(EMERALD_DARK);
        skipBackBg.setShape(GradientDrawable.OVAL);
        skipBackBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(111, 96, 57)
                        : Color.rgb(222, 207, 163)
        );
        quranSkipBackButton.setBackground(skipBackBg);
        quranSkipBackButton.setElevation(dp(2));

        quranSkipForwardButton = text(
                "+10",
                13,
                WHITE,
                true
        );
        quranSkipForwardButton.setGravity(Gravity.CENTER);
        quranSkipForwardButton.setEnabled(false);

        GradientDrawable skipForwardBg = new GradientDrawable();
        skipForwardBg.setColor(EMERALD_DARK);
        skipForwardBg.setShape(GradientDrawable.OVAL);
        skipForwardBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(111, 96, 57)
                        : Color.rgb(222, 207, 163)
        );
        quranSkipForwardButton.setBackground(skipForwardBg);
        quranSkipForwardButton.setElevation(dp(2));

        quranSkipBackButton.setOnClickListener(v -> {
            if (quranPlayer != null && audioPrepared) {
                int target = Math.max(
                        0,
                        quranPlayer.getCurrentPosition() - 10000
                );
                quranPlayer.seekTo(target);
                updateAudioTime();
            }
        });

        quranSkipForwardButton.setOnClickListener(v -> {
            if (quranPlayer != null && audioPrepared) {
                int target = Math.min(
                        quranPlayer.getDuration(),
                        quranPlayer.getCurrentPosition() + 10000
                );
                quranPlayer.seekTo(target);
                updateAudioTime();
            }
        });

        audioTop.addView(
                quranSkipBackButton,
                new LinearLayout.LayoutParams(
                        dp(46), dp(46)
                )
        );

        audioTop.addView(
                quranPlayButton,
                new LinearLayout.LayoutParams(
                        dp(52),
                        dp(52)
                )
        );

        audioTop.addView(
                quranSkipForwardButton,
                new LinearLayout.LayoutParams(
                        dp(46), dp(46)
                )
        );

        audioCard.addView(
                audioTop,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                )
        );

        quranSeekBar = new android.widget.SeekBar(this);
        quranSeekBar.setMax(1000);
        quranSeekBar.setProgress(0);

        GradientDrawable seekTrack =
                new GradientDrawable();

        seekTrack.setColor(
                nightMode
                        ? Color.rgb(55, 60, 54)
                        : Color.rgb(232, 235, 229)
        );

        seekTrack.setCornerRadius(
                dp(6)
        );

        GradientDrawable seekProgress =
                new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{
                                EMERALD_DARK,
                                EMERALD
                        }
                );

        seekProgress.setCornerRadius(
                dp(6)
        );

        android.graphics.drawable.LayerDrawable seekDrawable =
                new android.graphics.drawable.LayerDrawable(
                        new android.graphics.drawable.Drawable[]{
                                seekTrack,
                                seekProgress
                        }
                );

        seekDrawable.setId(
                0,
                android.R.id.background
        );

        seekDrawable.setId(
                1,
                android.R.id.progress
        );

        quranSeekBar.setProgressDrawable(
                seekDrawable
        );

        GradientDrawable seekThumb =
                new GradientDrawable();

        seekThumb.setShape(
                GradientDrawable.OVAL
        );

        seekThumb.setColor(
                nightMode
                        ? Color.rgb(220, 190, 112)
                        : GOLD
        );

        seekThumb.setSize(
                dp(14),
                dp(14)
        );

        quranSeekBar.setThumb(seekThumb);
        quranSeekBar.setThumbOffset(dp(7));

        audioCard.addView(
                quranSeekBar,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                )
        );

        quranAudioTime = text(
                "00:00 / 00:00",
                13,
                nightMode ? NIGHT_SECONDARY : TEXT_SECONDARY,
                false
        );
        quranAudioTime.setGravity(
                Gravity.CENTER
        );

        audioCard.addView(
                quranAudioTime,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(22)
                )
        );

        quranDownloadButton = text(
                "⬇ تحميل السورة",
                13,
                nightMode ? NIGHT_TEXT : TEXT,
                true
        );

        quranDownloadButton.setGravity(Gravity.CENTER);
        quranDownloadButton.setPadding(
                dp(16),
                dp(10),
                dp(16),
                dp(10)
        );

        GradientDrawable downloadBg = new GradientDrawable();
        downloadBg.setColor(
                nightMode
                        ? Color.rgb(39, 52, 45)
                        : Color.rgb(238, 247, 241)
        );
        downloadBg.setCornerRadius(dp(18));
        downloadBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(65, 80, 70)
                        : Color.rgb(205, 224, 213)
        );

        quranDownloadButton.setBackground(downloadBg);

        updateQuranDownloadButton(index);

        quranDownloadButton.setOnClickListener(v -> {
            if (isAudioDownloaded(index)) {
                deleteSurahAudio(index);
            } else {
                downloadSurahAudio(index);
            }
        });

        audioCard.addView(
                quranDownloadButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(46)
                )
        );

        LinearLayout.LayoutParams audioParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        audioParams.setMargins(
                dp(12), dp(12), dp(12), dp(12)
        );

        root.addView(
                audioCard,
                audioParams
        );

        final int audioIndex = index;

        quranPlayButton.setOnClickListener(v -> {
            toggleQuranAudio(audioIndex);
        });

        quranSeekBar.setOnSeekBarChangeListener(
                new android.widget.SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            android.widget.SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        if (fromUser
                                && quranPlayer != null
                                && audioPrepared) {

                            quranPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            android.widget.SeekBar seekBar
                    ) {
                        stopAudioProgressUpdates();
                    }

                    @Override
                    public void onStopTrackingTouch(
                            android.widget.SeekBar seekBar
                    ) {
                        if (quranPlayer != null && audioPrepared) {
                            startAudioProgressUpdates();
                        }
                    }
                }
        );

        View continueReadingBar =
                buildContinueReadingBar(
                        surah,
                        index,
                        verses
                );

        if (continueReadingBar != null) {
            LinearLayout.LayoutParams continueLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            continueLp.setMargins(
                    dp(16),
                    dp(10),
                    dp(16),
                    dp(4)
            );

            root.addView(
                    continueReadingBar,
                    continueLp
            );
        }

        ScrollView scroll =
                new ScrollView(this);

        final View[] targetAyahView =
                new View[1];

        final ArrayList<View> readingAyahViews =
                new ArrayList<>();

        scroll.setVerticalScrollBarEnabled(false);

        LinearLayout versesBox =
                new LinearLayout(this);

        versesBox.setOrientation(
                LinearLayout.VERTICAL
        );

        versesBox.setBackgroundColor(
                nightMode ? NIGHT_BG : CREAM
        );

        versesBox.setPadding(
                        dp(16),
                        dp(16),
                        dp(16),
                        dp(36)
                );

        TextView basmala =
                text(
                        "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                        23,
                        EMERALD_DARK,
                        true
                );

        basmala.setGravity(Gravity.CENTER);

        basmala.setPadding(
                dp(16),
                dp(10),
                dp(16),
                dp(10)
        );

        GradientDrawable basmalaBg =
                new GradientDrawable();

        basmalaBg.setColor(
                nightMode
                        ? Color.rgb(45, 43, 35)
                        : Color.rgb(247, 245, 235)
        );

        basmalaBg.setCornerRadius(dp(20));

        basmalaBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(94, 82, 49)
                        : Color.rgb(235, 225, 190)
        );

        basmala.setBackground(basmalaBg);

        LinearLayout.LayoutParams basmalaLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(82)
                );

        basmalaLp.bottomMargin = dp(16);

        versesBox.addView(
                basmala,
                basmalaLp
        );

        final android.content.SharedPreferences readingPrefs =
                getSharedPreferences("quran_reading", MODE_PRIVATE);

        final boolean compactReading =
                readingPrefs.getBoolean(
                        "compact_quran_reading",
                        false
                );

            /*
         * 11.7-D — Authentic Mushaf Mode
         *
         * Compact mode uses ONE continuous TextView for the entire
         * surah, so ayahs naturally flow from one line to the next
         * exactly like a printed Mushaf.
         */
        if (compactReading) {

            LinearLayout mushafPage =
                    new LinearLayout(this);

            mushafPage.setOrientation(
                    LinearLayout.VERTICAL
            );

            mushafPage.setGravity(
                    Gravity.RIGHT
            );

            mushafPage.setPadding(
                    dp(18),
                    dp(18),
                    dp(18),
                    dp(24)
            );

            GradientDrawable mushafBg =
                    new GradientDrawable();

            mushafBg.setColor(
                    nightMode
                            ? Color.rgb(28, 31, 29)
                            : Color.rgb(255, 253, 247)
            );

            mushafBg.setCornerRadius(
                    dp(16)
            );

            mushafBg.setStroke(
                    dp(1),
                    nightMode
                            ? Color.rgb(67, 71, 63)
                            : Color.rgb(226, 218, 198)
            );

            mushafPage.setBackground(
                    mushafBg
            );

            mushafPage.setElevation(
                    dp(2)
            );

            /*
             * Surah name
             */
            TextView mushafTitle =
                    text(
                            surah.optString(
                                    "name",
                                    ""
                            ),
                            21,
                            nightMode
                                    ? Color.rgb(221, 199, 145)
                                    : EMERALD,
                            true
                    );

            mushafTitle.setGravity(
                    Gravity.CENTER
            );

            mushafTitle.setPadding(
                    0,
                    0,
                    0,
                    dp(8)
            );

            mushafPage.addView(
                    mushafTitle,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            /*
             * Small elegant ornament
             */
            TextView ornament =
                    text(
                            "۞",
                            18,
                            nightMode
                                    ? Color.rgb(205, 179, 119)
                                    : GOLD,
                            true
                    );

            ornament.setGravity(
                    Gravity.CENTER
            );

            LinearLayout.LayoutParams ornamentLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(26)
                    );

            ornamentLp.bottomMargin =
                    dp(6);

            mushafPage.addView(
                    ornament,
                    ornamentLp
            );

            /*
             * Bismillah
             */
            if (index != 8) {

                TextView bismillah =
                        text(
                                "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                20,
                                nightMode
                                        ? Color.rgb(222, 210, 181)
                                        : Color.rgb(67, 76, 69),
                                true
                        );

                bismillah.setGravity(
                        Gravity.CENTER
                );

                bismillah.setIncludeFontPadding(
                        true
                );

                bismillah.setPadding(
                        0,
                        0,
                        0,
                        dp(14)
                );

                mushafPage.addView(
                        bismillah,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                );
            }

            /*
             * ONE continuous Quran TextView.
             */
            TextView mushafText =
                    text(
                            "",
                            22,
                            nightMode
                                    ? NIGHT_TEXT
                                    : TEXT,
                            false
                    );

            mushafText.setGravity(
                    Gravity.RIGHT
            );

            mushafText.setTextDirection(
                    View.TEXT_DIRECTION_RTL
            );

            mushafText.setTextAlignment(
                    View.TEXT_ALIGNMENT_VIEW_END
            );

            mushafText.setIncludeFontPadding(
                    true
            );

            mushafText.setLineSpacing(
                    dp(6),
                    1.18f
            );

            mushafText.setPadding(
                    dp(2),
                    dp(2),
                    dp(2),
                    dp(8)
            );

            mushafText.setMovementMethod(
                    android.text.method.LinkMovementMethod.getInstance()
            );

            mushafText.setHighlightColor(
                    Color.TRANSPARENT
            );

            readingAyahViews.add(
                    mushafText
            );

            targetAyahView[0] =
                    mushafText;

            StringBuilder fullText =
                    new StringBuilder();

            java.util.ArrayList<Integer> ayahStarts =
                    new java.util.ArrayList<>();

            java.util.ArrayList<Integer> ayahEnds =
                    new java.util.ArrayList<>();

            for (int i = 0; i < verses.length(); i++) {

                try {

                    JSONObject verse =
                            verses.getJSONObject(i);

                    String verseValue =
                            verse.optString(
                                    "text",
                                    ""
                            ).trim();

                    if (verseValue.length() == 0) {
                        continue;
                    }

                    if (fullText.length() > 0) {
                        fullText.append(" ");
                    }

                    int ayahStart =
                            fullText.length();

                    fullText.append(
                            verseValue
                    );

                    /*
                     * Quran-style verse marker using Arabic-Indic digits.
                     */
                    fullText.append("  ۝");

                    String number =
                            String.valueOf(i + 1);

                    for (int n = 0; n < number.length(); n++) {
                        char digit =
                                number.charAt(n);

                        if (digit >= '0' &&
                                digit <= '9') {

                            fullText.append(
                                    (char) (
                                            '\u0660' +
                                            (digit - '0')
                                    )
                            );

                        } else {
                            fullText.append(
                                    digit
                            );
                        }
                    }

                    int ayahEnd =
                            fullText.length();

                    ayahStarts.add(
                            ayahStart
                    );

                    ayahEnds.add(
                            ayahEnd
                    );

                } catch (Exception ignored) {
                }
            }

            android.text.SpannableString mushafSpannable =
                    new android.text.SpannableString(
                            fullText.toString()
                    );

            for (int i = 0;
                    i < ayahStarts.size();
                    i++) {

                final int ayahIndex =
                        i;

                int spanStart =
                        ayahStarts.get(i);

                int spanEnd =
                        ayahEnds.get(i);

                boolean current =
                        i == targetAyahIndex;

                boolean bookmarked =
                        isBookmarkedAyah(
                                index,
                                i
                        );

                /*
                 * Extremely subtle highlighting.
                 * The Quran remains visually dominant.
                 */
                if (current) {

                    mushafSpannable.setSpan(
                            new android.text.style.BackgroundColorSpan(
                                    nightMode
                                            ? Color.rgb(42, 65, 55)
                                            : Color.rgb(239, 247, 242)
                            ),
                            spanStart,
                            spanEnd,
                            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );

                } else if (bookmarked) {

                    mushafSpannable.setSpan(
                            new android.text.style.BackgroundColorSpan(
                                    nightMode
                                            ? Color.rgb(51, 49, 39)
                                            : Color.rgb(250, 246, 231)
                            ),
                            spanStart,
                            spanEnd,
                            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }

                /*
                 * Every ayah remains independently clickable.
                 */
                mushafSpannable.setSpan(
                        new android.text.style.ClickableSpan() {

                            @Override
                            public void onClick(
                                    View widget
                            ) {

                                try {

                                    JSONObject clickedVerse =
                                            verses.getJSONObject(
                                                    ayahIndex
                                            );

                                    String clickedText =
                                            clickedVerse.optString(
                                                    "text",
                                                    ""
                                            );

                                    showAyahActions(
                                            surah,
                                            index,
                                            ayahIndex,
                                            clickedText
                                    );

                                } catch (Exception ignored) {
                                }
                            }

                            @Override
                            public void updateDrawState(
                                    android.text.TextPaint ds
                            ) {

                                ds.setUnderlineText(
                                        false
                                );

                                ds.setColor(
                                        nightMode
                                                ? NIGHT_TEXT
                                                : TEXT
                                );
                            }

                        },
                        spanStart,
                        spanEnd,
                        android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            mushafText.setText(
                    mushafSpannable
            );

            mushafPage.addView(
                    mushafText,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

            /*
             * Small footer ornament.
             */
            TextView bottomOrnament =
                    text(
                            "۞",
                            16,
                            nightMode
                                    ? Color.rgb(172, 151, 103)
                                    : GOLD,
                            true
                    );

            bottomOrnament.setGravity(
                    Gravity.CENTER
            );

            LinearLayout.LayoutParams bottomLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(26)
                    );

            bottomLp.topMargin =
                    dp(6);

            mushafPage.addView(
                    bottomOrnament,
                    bottomLp
            );

            versesBox.addView(
                    mushafPage,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )
            );

        } else {

            /*
             * Comfortable premium reading mode.
             */
        for (int i = 0; i < verses.length(); i++) {

            try {
                JSONObject verse = verses.getJSONObject(i);

                String text = verse.optString(
                        "text",
                        ""
                );

                LinearLayout ayah = new LinearLayout(this);
                readingAyahViews.add(ayah);

                if (i == targetAyahIndex) {
                    targetAyahView[0] = ayah;
                }

                ayah.setOrientation(LinearLayout.VERTICAL);
                ayah.setGravity(Gravity.RIGHT);
                ayah.setPadding(
                        dp(18),
                        dp(16),
                        dp(18),
                        dp(15)
                );

                final int actionAyahIndex = i;
                final String actionAyahText = text;

                boolean ayahBookmarked =
                        isBookmarkedAyah(index, i);

                boolean ayahCurrent =
                        i == targetAyahIndex;

                GradientDrawable ayahBg =
                        new GradientDrawable();

                if (nightMode) {
                    ayahBg.setColor(
                            ayahCurrent
                                    ? Color.rgb(31, 58, 49)
                                    : Color.rgb(31, 35, 33)
                    );
                } else {
                    ayahBg.setColor(
                            ayahCurrent
                                    ? Color.rgb(240, 248, 243)
                                    : Color.rgb(255, 255, 253)
                    );
                }

                ayahBg.setCornerRadius(dp(20));

                int borderColor;

                if (ayahBookmarked) {
                    borderColor = GOLD;
                } else if (ayahCurrent) {
                    borderColor = EMERALD;
                } else {
                    borderColor = nightMode
                            ? Color.rgb(61, 68, 63)
                            : Color.rgb(232, 234, 228);
                }

                ayahBg.setStroke(
                        dp(1),
                        borderColor
                );

                ayah.setBackground(ayahBg);
                ayah.setElevation(
                        dp(ayahCurrent ? 3 : 1)
                );

                ayah.setClickable(true);

                ayah.setOnClickListener(v -> {
                    showAyahActions(
                            surah,
                            index,
                            actionAyahIndex,
                            actionAyahText
                    );
                });

                LinearLayout topRow =
                        new LinearLayout(this);

                topRow.setOrientation(
                        LinearLayout.HORIZONTAL
                );

                topRow.setGravity(
                        Gravity.CENTER_VERTICAL
                );

                TextView number = text(
                        String.valueOf(i + 1),
                        12,
                        ayahCurrent
                                ? EMERALD
                                : GOLD,
                        true
                );

                number.setGravity(
                        Gravity.CENTER
                );

                number.setPadding(
                        dp(6),
                        dp(6),
                        dp(6),
                        dp(6)
                );

                GradientDrawable numberBg =
                        new GradientDrawable();

                numberBg.setShape(
                        GradientDrawable.OVAL
                );

                numberBg.setColor(
                        nightMode
                                ? Color.rgb(48, 53, 48)
                                : Color.rgb(248, 243, 228)
                );

                numberBg.setStroke(
                        dp(1),
                        ayahCurrent
                                ? EMERALD
                                : Color.rgb(220, 199, 145)
                );

                number.setBackground(numberBg);

                LinearLayout.LayoutParams numberLp =
                        new LinearLayout.LayoutParams(
                                dp(38),
                                dp(38)
                        );

                numberLp.gravity =
                        Gravity.RIGHT;

                topRow.addView(
                        number,
                        numberLp
                );

                TextView status = text(
                        ayahCurrent
                                ? "موضع القراءة"
                                : (ayahBookmarked ? "محفوظ" : ""),
                        11,
                        ayahCurrent
                                ? EMERALD
                                : GOLD,
                        true
                );

                status.setGravity(
                        Gravity.CENTER_VERTICAL |
                        Gravity.RIGHT
                );

                LinearLayout.LayoutParams statusLp =
                        new LinearLayout.LayoutParams(
                                0,
                                dp(38),
                                1f
                        );

                statusLp.leftMargin = dp(10);

                topRow.addView(
                        status,
                        statusLp
                );

                ayah.addView(topRow);

                TextView verseText = text(
                        text,
                        24,
                        nightMode
                                ? NIGHT_TEXT
                                : TEXT,
                        false
                );

                verseText.setGravity(
                        Gravity.RIGHT
                );

                verseText.setIncludeFontPadding(true);

                verseText.setLineSpacing(
                        dp(10),
                        1.22f
                );

                verseText.setPadding(
                        0,
                        dp(10),
                        0,
                        dp(6)
                );

                LinearLayout.LayoutParams verseLp =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                verseLp.topMargin = dp(2);

                ayah.addView(
                        verseText,
                        verseLp
                );

                TextView saveButton = text(
                        isSavedPosition(index, i)
                                ? "✓  محفوظ كموضع قراءة"
                                : "🔖  حفظ موضع القراءة",
                        11,
                        isSavedPosition(index, i)
                                ? EMERALD
                                : GOLD,
                        true
                );

                saveButton.setGravity(
                        Gravity.RIGHT |
                        Gravity.CENTER_VERTICAL
                );

                saveButton.setPadding(
                        dp(10),
                        0,
                        dp(10),
                        0
                );

                GradientDrawable saveButtonBg =
                        new GradientDrawable();

                saveButtonBg.setColor(
                        nightMode
                                ? Color.rgb(40, 45, 41)
                                : Color.rgb(248, 249, 245)
                );

                saveButtonBg.setCornerRadius(
                        dp(10)
                );

                saveButtonBg.setStroke(
                        dp(1),
                        nightMode
                                ? Color.rgb(65, 72, 66)
                                : Color.rgb(226, 230, 223)
                );

                saveButton.setBackground(
                        saveButtonBg
                );

                saveButton.setContentDescription(
                        "حفظ موضع القراءة"
                );

                final int savedAyahIndex = i;

                saveButton.setOnClickListener(v -> {
                    saveReadingPosition(
                            surah,
                            index,
                            savedAyahIndex
                    );

                    showReadingScreen(
                            surah,
                            verses,
                            index,
                            savedAyahIndex
                    );
                });

                LinearLayout.LayoutParams saveLp =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dp(36)
                        );

                saveLp.topMargin = dp(8);

                ayah.addView(
                        saveButton,
                        saveLp
                );

                LinearLayout.LayoutParams ayahLp =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                ayahLp.bottomMargin = dp(10);

                versesBox.addView(
                        ayah,
                        ayahLp
                );

                View ayahDivider =
                        new View(this);

                ayahDivider.setBackgroundColor(
                        nightMode
                                ? Color.rgb(54, 60, 55)
                                : Color.rgb(235, 235, 229)
                );

                LinearLayout.LayoutParams dividerLp =
                        new LinearLayout.LayoutParams(
                                dp(42),
                        dp(1)
                        );

                dividerLp.gravity =
                        Gravity.CENTER_HORIZONTAL;

                dividerLp.bottomMargin =
                        dp(8);

                versesBox.addView(
                        ayahDivider,
                        dividerLp
                );

            } catch (Exception ignored) {
            }
        }
        }


        /*
         * Reading density switch
         */
        TextView readingModeButton = text(
                readingFullScreen
                        ? "✕  خروج من ملء الشاشة"
                        : (compactReading
                                ? "◉  قراءة مريحة"
                                : "◉  قراءة مضغوطة"),
                11,
                readingFullScreen
                        ? (nightMode ? GOLD : EMERALD)
                        : (compactReading ? EMERALD : GOLD),
                true
        );

        readingModeButton.setGravity(
                Gravity.CENTER
        );

        readingModeButton.setPadding(
                dp(14),
                0,
                dp(14),
                0
        );

        GradientDrawable readingModeBg =
                new GradientDrawable();

        readingModeBg.setColor(
                nightMode
                        ? Color.rgb(39, 44, 40)
                        : Color.rgb(248, 249, 245)
        );

        readingModeBg.setCornerRadius(
                dp(14)
        );

        readingModeBg.setStroke(
                dp(1),
                compactReading
                        ? EMERALD
                        : Color.rgb(226, 230, 223)
        );

        readingModeButton.setBackground(
                readingModeBg
        );

        readingModeButton.setContentDescription(
                readingFullScreen
                        ? "الخروج من ملء الشاشة"
                        : "تغيير نمط قراءة القرآن"
        );

        readingModeButton.setOnClickListener(v -> {
            if (readingFullScreen) {
                readingFullScreen = false;
            } else {
                readingPrefs.edit()
                        .putBoolean(
                                "compact_quran_reading",
                                !compactReading
                        )
                        .apply();
            }

            showReadingScreen(
                    surah,
                    verses,
                    index,
                    targetAyahIndex
            );
        });

        LinearLayout.LayoutParams modeLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(36)
                );

        modeLp.gravity =
                Gravity.CENTER_HORIZONTAL;

        modeLp.topMargin = dp(6);
        modeLp.bottomMargin = dp(8);

        root.addView(
                readingModeButton,
                modeLp
        );

        // Mini Player — Ultra Premium Quran Player
        quranMiniPlayer = new LinearLayout(this);
        quranMiniPlayer.setOrientation(
                LinearLayout.VERTICAL
        );
        quranMiniPlayer.setGravity(
                Gravity.CENTER_VERTICAL
        );
        quranMiniPlayer.setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
        );

        GradientDrawable miniBg =
                new GradientDrawable();

        miniBg.setColor(
                nightMode
                        ? Color.rgb(28, 38, 33)
                        : Color.rgb(247, 250, 247)
        );

        miniBg.setCornerRadius(
                dp(20)
        );

        miniBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(62, 78, 69)
                        : Color.rgb(218, 229, 221)
        );

        quranMiniPlayer.setBackground(
                miniBg
        );

        quranMiniPlayer.setElevation(
                dp(5)
        );

        quranMiniPlayer.setVisibility(
                View.GONE
        );

        /*
         * Thin premium progress line
         */
        quranMiniSeekBar =
                new android.widget.SeekBar(this);

        quranMiniSeekBar.setMax(1000);
        quranMiniSeekBar.setProgress(0);
        quranMiniSeekBar.setPadding(
                0,
                0,
                0,
                0
        );

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            quranMiniSeekBar.setSplitTrack(false);
        }

        quranMiniSeekBar.setContentDescription(
                "تقدم التلاوة"
        );

        quranMiniSeekBar.setOnSeekBarChangeListener(
                new android.widget.SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            android.widget.SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {
                        if (fromUser
                                && quranPlayer != null
                                && audioPrepared) {
                            try {
                                quranPlayer.seekTo(progress);
                            } catch (Exception ignored) {
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            android.widget.SeekBar seekBar
                    ) {
                        stopAudioProgressUpdates();
                    }

                    @Override
                    public void onStopTrackingTouch(
                            android.widget.SeekBar seekBar
                    ) {
                        if (quranPlayer != null && audioPrepared) {
                            startAudioProgressUpdates();
                        }
                    }
                }
        );

        LinearLayout.LayoutParams miniSeekLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(10)
                );

        miniSeekLp.bottomMargin =
                dp(4);

        quranMiniPlayer.addView(
                quranMiniSeekBar,
                miniSeekLp
        );

        /*
         * Main player row
         */
        LinearLayout miniMainRow =
                new LinearLayout(this);

        miniMainRow.setOrientation(
                LinearLayout.HORIZONTAL
        );
        miniMainRow.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        miniMainRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        /*
         * Premium circular play button
         */
        quranMiniPlayButton = text(
                "▶",
                17,
                nightMode
                        ? Color.rgb(249, 244, 222)
                        : Color.WHITE,
                true
        );

        quranMiniPlayButton.setGravity(
                Gravity.CENTER
        );

        quranMiniPlayButton.setContentDescription(
                "تشغيل أو إيقاف التلاوة"
        );

        GradientDrawable miniPlayBg =
                new GradientDrawable();

        miniPlayBg.setShape(
                GradientDrawable.OVAL
        );

        miniPlayBg.setColor(
                nightMode
                        ? Color.rgb(190, 155, 75)
                        : EMERALD_DARK
        );

        quranMiniPlayButton.setBackground(
                miniPlayBg
        );

        quranMiniPlayButton.setElevation(
                dp(3)
        );

        LinearLayout.LayoutParams miniPlayLp =
                new LinearLayout.LayoutParams(
                        dp(44),
                        dp(44)
                );

        miniPlayLp.setMargins(
                dp(10),
                0,
                dp(2),
                0
        );

        miniMainRow.addView(
                quranMiniPlayButton,
                miniPlayLp
        );

        /*
         * Information area
         */
        LinearLayout miniInfo =
                new LinearLayout(this);

        miniInfo.setOrientation(
                LinearLayout.VERTICAL
        );

        miniInfo.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout.LayoutParams miniInfoLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        quranMiniTitle = text(
                "تلاوة القرآن الكريم",
                14,
                nightMode
                        ? WHITE
                        : TEXT,
                true
        );

        quranMiniTitle.setGravity(
                Gravity.RIGHT
        );

        quranMiniTime = text(
                "00:00   •   00:00",
                10,
                nightMode
                        ? Color.rgb(177, 188, 181)
                        : TEXT_SECONDARY,
                false
        );

        quranMiniTime.setGravity(
                Gravity.RIGHT
        );

        quranMiniSubtitle =
                text(
                        "تلاوة القرآن الكريم",
                        9,
                        nightMode
                                ? Color.rgb(148, 162, 154)
                                : Color.rgb(122, 132, 126),
                        false
                );

        quranMiniSubtitle.setGravity(
                Gravity.RIGHT
        );

        miniInfo.addView(
                quranMiniTitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        miniInfo.addView(
                quranMiniSubtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        miniInfo.addView(
                quranMiniTime,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        miniMainRow.addView(
                miniInfo,
                miniInfoLp
        );

        /*
         * Left-side accent indicator
         */
        TextView miniLive =
                text(
                        "●",
                        11,
                        nightMode
                                ? GOLD
                                : EMERALD_DARK,
                        true
                );

        miniLive.setContentDescription(
                "التلاوة تعمل الآن"
        );

        miniLive.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams miniLiveLp =
                new LinearLayout.LayoutParams(
                        dp(18),
                        dp(44)
                );

        miniMainRow.addView(
                miniLive,
                miniLiveLp
        );

        quranMiniPlayer.addView(
                miniMainRow,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(44)
                )
        );

        quranMiniPlayer.setOnClickListener(v -> {
            if (currentAudioSurahIndex >= 0) {
                toggleQuranAudio(
                        currentAudioSurahIndex
                );
            }
        });

        quranMiniPlayButton.setOnClickListener(v -> {
            if (currentAudioSurahIndex >= 0) {
                toggleQuranAudio(
                        currentAudioSurahIndex
                );
            }
        });

        LinearLayout.LayoutParams miniLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(76)
                );

        miniLp.setMargins(
                dp(16),
                0,
                dp(16),
                dp(10)
        );

        root.addView(
                quranMiniPlayer,
                miniLp
        );

        scroll.addView(versesBox);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        currentReadingSurah = surah;
        currentReadingSurahIndex = index;
        currentReadingAyahIndex =
                targetAyahIndex >= 0 ? targetAyahIndex : 0;

        scroll.setOnScrollChangeListener(
                new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(
                            View v,
                            int scrollX,
                            int scrollY,
                            int oldScrollX,
                            int oldScrollY
                    ) {
                        int visibleIndex = 0;
                        int limit = scrollY + dp(120);

                        for (int j = 0;
                             j < readingAyahViews.size();
                             j++) {

                            View item =
                                    readingAyahViews.get(j);

                            if (item.getTop() <= limit) {
                                visibleIndex = j;
                            } else {
                                break;
                            }
                        }

                        if (visibleIndex !=
                                currentReadingAyahIndex) {

                            currentReadingAyahIndex =
                                    visibleIndex;

                            saveCurrentReadingPosition();
                        }
                    }
                }
        );

        setContentView(root);

        if (targetAyahIndex >= 0
                && targetAyahView[0] != null) {

            final boolean compactReadingMode =
                    getSharedPreferences(
                            "app_settings",
                            MODE_PRIVATE
                    ).getBoolean(
                            "compact_quran_reading",
                            true
                    );

            if (compactReadingMode
                    && targetAyahView[0] instanceof TextView) {

                final TextView mushafTarget =
                        (TextView) targetAyahView[0];

                scroll.post(() -> {
                    try {
                        android.text.Layout layout =
                                mushafTarget.getLayout();

                        if (layout == null) {
                            return;
                        }

                        int safeAyahIndex =
                                Math.max(
                                        0,
                                        Math.min(
                                                targetAyahIndex,
                                                verses.length() - 1
                                        )
                                );

                        int offset = 0;

                        for (int i = 0;
                                i < safeAyahIndex;
                                i++) {

                            JSONObject previousVerse =
                                    verses.getJSONObject(i);

                            String previousText =
                                    previousVerse.optString(
                                            "text",
                                            ""
                                    ).trim();

                            if (previousText.length() == 0) {
                                continue;
                            }

                            if (offset > 0) {
                                offset += 1;
                            }

                            offset += previousText.length();

                            offset += 3;

                            String previousNumber =
                                    String.valueOf(i + 1);

                            offset +=
                                    previousNumber.length();
                        }

                        int line =
                                layout.getLineForOffset(
                                        offset
                                );

                        int lineTop =
                                layout.getLineTop(
                                        line
                                );

                        int targetY =
                                mushafTarget.getTop()
                                        + lineTop
                                        - dp(24);

                        scroll.smoothScrollTo(
                                0,
                                Math.max(
                                        0,
                                        targetY
                                )
                        );

                    } catch (Exception ignored) {
                    }
                });

            } else {

                scroll.post(() -> {
                    scroll.smoothScrollTo(
                            0,
                            Math.max(
                                    0,
                                    targetAyahView[0].getTop()
                                            - dp(12)
                            )
                    );
                });
            }
        }
    }

    private void saveCurrentReadingPosition() {
        final boolean compactReadingMode =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                ).getBoolean(
                        "compact_quran_reading",
                        true
                );

        if (compactReadingMode) {
            return;
        }



        if (currentReadingSurah == null
                || currentReadingSurahIndex < 0
                || currentReadingAyahIndex < 0) {
            return;
        }

        getSharedPreferences(
                PREF_READING,
                MODE_PRIVATE
        )
                .edit()
                .putInt(
                        KEY_SURAH_INDEX,
                        currentReadingSurahIndex
                )
                .putInt(
                        KEY_AYAH_INDEX,
                        currentReadingAyahIndex
                )
                .putString(
                        KEY_SURAH_NAME,
                        currentReadingSurah.optString(
                                "name",
                                ""
                        )
                )
                .apply();
    }

    private TextView text(
            String value,
            float size,
            int color,
            boolean bold
    ) {

        TextView view =
                new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setIncludeFontPadding(true);

        if (bold) {
            view.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.BOLD
                    )
            );
        } else {
            view.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.NORMAL
                    )
            );
        }

        return view;
    }

    private int dp(int value) {

        return (int)(
                value *
                getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private String getSelectedReciterId() {
        return getSharedPreferences(PREF_AUDIO, MODE_PRIVATE)
                .getString(
                        KEY_SELECTED_RECITER,
                        DEFAULT_RECITER_ID
                );
    }

    private void saveSelectedReciterMetadata(Reciter reciter) {
        if (reciter == null) {
            return;
        }

        getSharedPreferences(
                PREF_AUDIO,
                MODE_PRIVATE
        )
                .edit()
                .putString(
                        KEY_SELECTED_RECITER,
                        reciter.id
                )
                .putString(
                        KEY_SELECTED_RECITER_NAME,
                        reciter.name
                )
                .putString(
                        KEY_SELECTED_RECITER_SERVER,
                        reciter.baseUrl
                )
                .putString(
                        KEY_SELECTED_RECITER_MOSHAF,
                        reciter.moshafName
                )
                .apply();
    }

    private Reciter getCachedSelectedReciter() {
        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        PREF_AUDIO,
                        MODE_PRIVATE
                );

        String id = prefs.getString(
                KEY_SELECTED_RECITER,
                DEFAULT_RECITER_ID
        );

        String name = prefs.getString(
                KEY_SELECTED_RECITER_NAME,
                ""
        );

        String server = prefs.getString(
                KEY_SELECTED_RECITER_SERVER,
                ""
        );

        String moshaf = prefs.getString(
                KEY_SELECTED_RECITER_MOSHAF,
                "رواية حفص عن عاصم"
        );

        if (id.isEmpty()
                || name.isEmpty()
                || server.isEmpty()) {
            return null;
        }

        java.util.HashSet<Integer> all =
                new java.util.HashSet<>();

        for (int i = 1; i <= 114; i++) {
            all.add(i);
        }

        return new Reciter(
                id,
                name,
                server,
                moshaf,
                all
        );
    }

    private File getAudioDirectory() {
        File dir = new File(getFilesDir(), "quran_audio");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private File getLocalAudioFile(int surahIndex) {
        String reciterId = getSelectedReciterId();

        return new File(
                getAudioDirectory(),
                reciterId
                        + "_"
                        + String.format(
                                java.util.Locale.US,
                                "%03d",
                                surahIndex + 1
                        )
                        + ".mp3"
        );
    }

    private boolean isAudioDownloaded(int surahIndex) {
        File file = getLocalAudioFile(surahIndex);

        return file.exists() && file.length() > 0;
    }

    private String getAudioSource(int surahIndex) {
        File localFile = getLocalAudioFile(surahIndex);

        if (localFile.exists() && localFile.length() > 0) {
            return localFile.getAbsolutePath();
        }

        return getQuranAudioUrl(surahIndex);
    }

    private String getSelectedReciterName() {
        return getSelectedReciter().name;
    }

    // إنشاء رابط ملف السورة تلقائيًا
    private String getQuranAudioUrl(int surahIndex) {
        Reciter reciter = getSelectedReciter();

        return reciter.baseUrl
                + String.format(
                        java.util.Locale.US,
                        "%03d",
                        surahIndex + 1
                )
                + ".mp3";
    }

    private java.util.ArrayList<Reciter> parseRecitersJson(
            String json
    ) throws Exception {

        java.util.ArrayList<Reciter> loaded =
                new java.util.ArrayList<>();

        JSONObject root =
                new JSONObject(json);

        JSONArray array =
                root.optJSONArray("reciters");

        if (array == null) {
            throw new Exception(
                    "reciters array missing"
            );
        }

        for (int i = 0; i < array.length(); i++) {

            JSONObject reciterObject =
                    array.optJSONObject(i);

            if (reciterObject == null) {
                continue;
            }

            String rawId =
                    String.valueOf(
                            reciterObject.optInt(
                                    "id",
                                    -1
                            )
                    );

            String name =
                    reciterObject.optString(
                            "name",
                            ""
                    ).trim();

            JSONArray moshafArray =
                    reciterObject.optJSONArray(
                            "moshaf"
                    );

            if (rawId.equals("-1")
                    || name.isEmpty()
                    || moshafArray == null
                    || moshafArray.length() == 0) {
                continue;
            }

            JSONObject bestMoshaf = null;
            int bestTotal = -1;

            for (int j = 0;
                    j < moshafArray.length();
                    j++) {

                JSONObject moshaf =
                        moshafArray.optJSONObject(j);

                if (moshaf == null) {
                    continue;
                }

                int total =
                        moshaf.optInt(
                                "surah_total",
                                0
                        );

                String server =
                        moshaf.optString(
                                "server",
                                ""
                        ).trim();

                if (!server.isEmpty()
                        && total > bestTotal) {

                    bestTotal = total;
                    bestMoshaf = moshaf;
                }
            }

            if (bestMoshaf == null) {
                continue;
            }

            String server =
                    bestMoshaf.optString(
                            "server",
                            ""
                    ).trim();

            String moshafName =
                    bestMoshaf.optString(
                            "name",
                            "تلاوة القرآن"
                    ).trim();

            String surahList =
                    bestMoshaf.optString(
                            "surah_list",
                            ""
                    );

            java.util.HashSet<Integer> available =
                    new java.util.HashSet<>();

            String[] parts =
                    surahList.split(",");

            for (String part : parts) {
                try {
                    int surahNumber =
                            Integer.parseInt(
                                    part.trim()
                            );

                    if (surahNumber >= 1
                            && surahNumber <= 114) {
                        available.add(
                                surahNumber
                        );
                    }
                } catch (Exception ignored) {
                }
            }

            if (available.isEmpty()
                    && bestTotal == 114) {

                for (int n = 1; n <= 114; n++) {
                    available.add(n);
                }
            }

            /*
             * أحمد العجمي قد يرجع من API بمعرف رقمي،
             * بينما الملفات القديمة عندنا تستخدم ajm.
             * نوحّد المعرف اعتمادًا على السيرفر.
             */
            String normalizedId = rawId;

            if (server.toLowerCase(
                    java.util.Locale.US
            ).contains("/ajm/")) {
                normalizedId = "ajm";
            }

            loaded.add(
                    new Reciter(
                            normalizedId,
                            name,
                            server,
                            moshafName,
                            available
                    )
            );
        }

        return loaded;
    }

    private void saveRecitersCache(String json) {
        if (json == null || json.trim().isEmpty()) {
            return;
        }

        getSharedPreferences(
                PREF_AUDIO,
                MODE_PRIVATE
        )
                .edit()
                .putString(
                        KEY_RECITERS_CACHE,
                        json
                )
                .apply();
    }

    private boolean loadRecitersFromCache() {
        String json =
                getSharedPreferences(
                        PREF_AUDIO,
                        MODE_PRIVATE
                )
                .getString(
                        KEY_RECITERS_CACHE,
                        ""
                );

        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            java.util.ArrayList<Reciter> cached =
                    parseRecitersJson(json);

            if (cached.isEmpty()) {
                return false;
            }

            reciters.clear();
            reciters.addAll(cached);

            String selectedId =
                    getSelectedReciterId();

            boolean selectedExists = false;

            for (Reciter r : reciters) {
                if (r.id.equals(selectedId)) {
                    selectedExists = true;
                    break;
                }
            }

            if (!selectedExists) {
                saveSelectedReciterMetadata(
                        reciters.get(0)
                );
            }

            return true;

        } catch (Exception ignored) {
            return false;
        }
    }

    // تحميل جميع القراء من API مع حفظ نسخة Offline
    private void loadRecitersFromApi(
            final Runnable onFinished
    ) {
        if (recitersLoading) {
            Toast.makeText(
                    this,
                    "جاري تحديث قائمة القراء...",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        recitersLoading = true;

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        "https://mp3quran.net/api/v3/reciters?language=ar"
                );

                connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setUseCaches(true);

                InputStream input =
                        connection.getInputStream();

                java.io.ByteArrayOutputStream output =
                        new java.io.ByteArrayOutputStream();

                byte[] buffer = new byte[8192];
                int count;

                while ((count = input.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }

                input.close();

                String json =
                        output.toString("UTF-8");

                java.util.ArrayList<Reciter> loaded =
                        parseRecitersJson(json);

                if (loaded.isEmpty()) {
                    throw new Exception(
                            "No reciters returned"
                    );
                }

                saveRecitersCache(json);

                final String selectedBefore =
                        getSelectedReciterId();

                runOnUiThread(() -> {

                    reciters.clear();
                    reciters.addAll(loaded);

                    Reciter selected =
                            null;

                    for (Reciter r : reciters) {
                        if (r.id.equals(
                                selectedBefore
                        )) {
                            selected = r;
                            break;
                        }
                    }

                    if (selected == null) {
                        selected = reciters.get(0);
                    }

                    saveSelectedReciterMetadata(
                            selected
                    );

                    recitersLoading = false;

                    if (onFinished != null) {
                        onFinished.run();
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    recitersLoading = false;

                    boolean cached =
                            loadRecitersFromCache();

                    ensureFallbackReciter();

                    if (!cached) {
                        Toast.makeText(
                                this,
                                "تعذر تحديث قائمة القراء",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    if (onFinished != null) {
                        onFinished.run();
                    }
                });

            } finally {

                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception ignored) {
                    }
                }
            }

        }).start();
    }

    private void showReciterChooser() {

        if (recitersLoading) {
            Toast.makeText(
                    this,
                    "جاري تحديث قائمة القراء...",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (reciters.size() <= 1) {

            if (!loadRecitersFromCache()) {
                loadRecitersFromApi(
                        () -> showReciterChooser()
                );
                return;
            }
        }

        final android.app.Dialog dialog =
                new android.app.Dialog(this);

        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                nightMode
                        ? NIGHT_CARD
                        : Color.WHITE
        );

        bg.setCornerRadius(dp(26));

        box.setBackground(bg);

        TextView title = text(
                "اختيار القارئ",
                20,
                nightMode
                        ? NIGHT_TEXT
                        : TEXT,
                true
        );

        title.setGravity(Gravity.RIGHT);

        box.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                )
        );

        TextView subtitle = text(
                "اختر صوت التلاوة المفضل لديك",
                12,
                nightMode
                        ? NIGHT_SECONDARY
                        : TEXT_SECONDARY,
                false
        );

        subtitle.setGravity(Gravity.RIGHT);

        box.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(28)
                )
        );

        final android.widget.EditText search =
                new android.widget.EditText(this);

        search.setSingleLine(true);
        search.setTextSize(14);
        search.setHint("ابحث عن اسم القارئ...");
        search.setGravity(
                Gravity.RIGHT | Gravity.CENTER_VERTICAL
        );

        search.setPadding(
                dp(16),
                0,
                dp(16),
                0
        );

        GradientDrawable searchBg =
                new GradientDrawable();

        searchBg.setColor(
                nightMode
                        ? Color.rgb(39,44,42)
                        : Color.rgb(248,249,246)
        );

        searchBg.setCornerRadius(dp(16));

        searchBg.setStroke(
                dp(1),
                nightMode
                        ? Color.rgb(65,75,70)
                        : Color.rgb(225,229,224)
        );

        search.setBackground(searchBg);

        box.addView(
                search,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(48)
                )
        );

        final android.widget.ScrollView scroll =
                new android.widget.ScrollView(this);

        scroll.setVerticalScrollBarEnabled(false);

        final LinearLayout list =
                new LinearLayout(this);

        list.setOrientation(
                LinearLayout.VERTICAL
        );

        scroll.addView(list);

        LinearLayout.LayoutParams scrollParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(420)
                );

        scrollParams.topMargin = dp(10);

        box.addView(
                scroll,
                scrollParams
        );

        final String selectedId =
                getSelectedReciterId();

        final java.util.ArrayList<Reciter> allReciters =
                new java.util.ArrayList<>(
                        getReciters()
                );

        final Runnable[] renderHolder =
                new Runnable[1];

        renderHolder[0] = () -> {

            list.removeAllViews();

            String query =
                    search.getText()
                            .toString()
                            .trim()
                            .toLowerCase(
                                    java.util.Locale
                                            .getDefault()
                            );

            int shown = 0;

            for (Reciter reciter : allReciters) {

                if (!query.isEmpty()
                        && !reciter.name
                                .toLowerCase(
                                        java.util.Locale
                                                .getDefault()
                                )
                                .contains(query)) {
                    continue;
                }

                final String id =
                        reciter.id;

                boolean selected =
                        id.equals(
                                getSelectedReciterId()
                        );

                TextView row = text(
                        reciter.name
                                + (selected
                                ? "   ✓"
                                : ""),
                        15,
                        nightMode
                                ? NIGHT_TEXT
                                : TEXT,
                        selected
                );

                row.setGravity(
                        Gravity.CENTER_VERTICAL
                                | Gravity.RIGHT
                );

                row.setPadding(
                        dp(16),
                        0,
                        dp(16),
                        0
                );

                GradientDrawable rowBg =
                        new GradientDrawable();

                rowBg.setColor(
                        selected
                                ? (
                                    nightMode
                                        ? Color.rgb(
                                            45,55,48
                                        )
                                        : Color.rgb(
                                            238,247,241
                                        )
                                  )
                                : (
                                    nightMode
                                        ? Color.rgb(
                                            39,44,42
                                        )
                                        : Color.rgb(
                                            250,250,247
                                        )
                                  )
                );

                rowBg.setCornerRadius(dp(16));

                row.setBackground(rowBg);

                row.setOnClickListener(v -> {

                    if (audioDownloadInProgress) {
                        Toast.makeText(
                                this,
                                "انتظر انتهاء التحميل أولاً",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    saveCurrentAudioPosition();
                    releaseQuranPlayer();

                    saveSelectedReciterMetadata(
                            reciter
                    );

                    if (quranPlayButton != null) {
                        quranPlayButton.setText("▶");
                        quranPlayButton.setEnabled(true);
                    }

                    if (quranSkipBackButton != null) {
                        quranSkipBackButton.setEnabled(false);
                    }

                    if (quranSkipForwardButton != null) {
                        quranSkipForwardButton.setEnabled(false);
                    }

                    if (quranSeekBar != null) {
                        quranSeekBar.setProgress(0);
                    }

                    if (quranDownloadButton != null) {
                        quranDownloadButton.setText(
                                isAudioDownloaded(
                                        currentAudioSurahIndex >= 0
                                                ? currentAudioSurahIndex
                                                : 0
                                )
                                        ? "✓ محفوظة بدون إنترنت"
                                        : "⬇ تحميل السورة"
                        );
                    }

                    if (audioReciterHolder != null) {
                        audioReciterHolder.setText(
                                getSelectedReciterName()
                        );
                    }

                    dialog.dismiss();

                    Toast.makeText(
                            this,
                            "تم اختيار " + reciter.name,
                            Toast.LENGTH_SHORT
                    ).show();
                });

                LinearLayout.LayoutParams lp =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dp(52)
                        );

                lp.topMargin = dp(8);

                list.addView(row, lp);

                shown++;
            }

            if (shown == 0) {

                TextView empty = text(
                        "لا يوجد قارئ بهذا الاسم",
                        14,
                        nightMode
                                ? NIGHT_SECONDARY
                                : TEXT_SECONDARY,
                        false
                );

                empty.setGravity(
                        Gravity.CENTER
                );

                list.addView(
                        empty,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                dp(70)
                        )
                );
            }
        };

        search.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        renderHolder[0].run();
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {
                    }
                }
        );

        renderHolder[0].run();

        dialog.setContentView(box);

        dialog.show();

        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawable(
                            new android.graphics.drawable
                                    .ColorDrawable(
                                    Color.TRANSPARENT
                            )
                    );

            dialog.getWindow().setLayout(
                    (int)(
                            getResources()
                                    .getDisplayMetrics()
                                    .widthPixels * 0.92f
                    ),
                    android.view.WindowManager
                            .LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void toggleQuranAudio(int surahIndex) {

        if (quranPlayer == null
                || currentAudioSurahIndex != surahIndex) {

            playSurahAudio(surahIndex);
            return;
        }

        if (!audioPrepared) {
            return;
        }

        if (quranPlayer.isPlaying()) {

            quranPlayer.pause();

            if (quranPlayButton != null) {
                quranPlayButton.setText("▶");
            }

            stopAudioProgressUpdates();

        } else {

            quranPlayer.start();

            if (quranPlayButton != null) {
                quranPlayButton.setText("⏸");
            }

            startAudioProgressUpdates();
        }
    }

    private void updateQuranDownloadButton(int surahIndex) {
        if (quranDownloadButton == null) return;

        if (audioDownloadInProgress) {
            quranDownloadButton.setText("جاري التحميل...");
            quranDownloadButton.setEnabled(false);
            return;
        }

        quranDownloadButton.setEnabled(true);

        if (isAudioDownloaded(surahIndex)) {
            quranDownloadButton.setText("✓ محفوظة بدون إنترنت");
        } else {
            quranDownloadButton.setText("⬇ تحميل السورة");
        }
    }

    private void deleteSurahAudio(int surahIndex) {
        if (audioDownloadInProgress) {
            Toast.makeText(
                    this,
                    "انتظر حتى يكتمل التحميل",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        File file = getLocalAudioFile(surahIndex);

        if (!file.exists()) {
            updateQuranDownloadButton(surahIndex);
            return;
        }

        if (file.delete()) {
            Toast.makeText(
                    this,
                    "تم حذف التحميل",
                    Toast.LENGTH_SHORT
            ).show();
            updateQuranDownloadButton(surahIndex);
        } else {
            Toast.makeText(
                    this,
                    "تعذر حذف الملف",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void downloadSurahAudio(int surahIndex) {
        if (audioDownloadInProgress) {
            Toast.makeText(
                    this,
                    "يوجد تحميل جارٍ بالفعل",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (isAudioDownloaded(surahIndex)) {
            Toast.makeText(
                    this,
                    "السورة محمّلة بالفعل",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        final String urlString = getQuranAudioUrl(surahIndex);
        final File targetFile = getLocalAudioFile(surahIndex);
        final File tempFile =
                new File(targetFile.getAbsolutePath() + ".download");

        audioDownloadInProgress = true;
        updateQuranDownloadButton(surahIndex);

        Toast.makeText(
                this,
                "جاري تحميل السورة...",
                Toast.LENGTH_SHORT
        ).show();

        audioDownloadThread = new Thread(() -> {
            HttpURLConnection connection = null;
            InputStream input = null;
            FileOutputStream output = null;

            try {
                URL url = new URL(urlString);

                connection =
                        (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode =
                        connection.getResponseCode();

                if (responseCode < 200
                        || responseCode >= 300) {
                    throw new Exception(
                            "HTTP " + responseCode
                    );
                }

                input = connection.getInputStream();

                output = new FileOutputStream(tempFile);

                byte[] buffer = new byte[8192];
                int count;

                while ((count = input.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }

                output.flush();

                if (!tempFile.exists()
                        || tempFile.length() <= 0) {
                    throw new Exception("Empty audio file");
                }

                if (targetFile.exists()) {
                    targetFile.delete();
                }

                if (!tempFile.renameTo(targetFile)) {
                    throw new Exception(
                            "Could not finalize audio file"
                    );
                }

                final long fileSize = targetFile.length();

                runOnUiThread(() -> {
                    audioDownloadInProgress = false;
                    updateQuranDownloadButton(surahIndex);

                    Toast.makeText(
                            this,
                            "تم تحميل السورة بنجاح",
                            Toast.LENGTH_SHORT
                    ).show();
                });

            } catch (Exception e) {

                try {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                } catch (Exception ignored) {
                }

                runOnUiThread(() -> {
                    audioDownloadInProgress = false;
                    updateQuranDownloadButton(surahIndex);

                    Toast.makeText(
                            this,
                            "تعذر تحميل السورة",
                            Toast.LENGTH_SHORT
                    ).show();
                });

            } finally {

                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception ignored) {
                }

                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (Exception ignored) {
                }

                if (connection != null) {
                    connection.disconnect();
                }

                audioDownloadThread = null;
            }
        });

        audioDownloadThread.start();
    }

    // تشغيل السورة من السيرفر
    private String getAudioPositionKey(int surahIndex) {
        String reciterId =
                getSharedPreferences(PREF_AUDIO, MODE_PRIVATE)
                        .getString(
                                KEY_SELECTED_RECITER,
                                DEFAULT_RECITER_ID
                        );

        return KEY_AUDIO_POSITION_PREFIX
                + reciterId
                + "_"
                + surahIndex;
    }

    private int getSavedAudioPosition(int surahIndex) {
        return getSharedPreferences(PREF_READING, MODE_PRIVATE)
                .getInt(getAudioPositionKey(surahIndex), 0);
    }

    private void saveCurrentAudioPosition() {
        if (quranPlayer == null
                || !audioPrepared
                || currentAudioSurahIndex < 0) {
            return;
        }

        int position;
        try {
            position = quranPlayer.getCurrentPosition();
        } catch (Exception ignored) {
            return;
        }

        getSharedPreferences(PREF_READING, MODE_PRIVATE)
                .edit()
                .putInt(
                        getAudioPositionKey(currentAudioSurahIndex),
                        Math.max(0, position)
                )
                .apply();
    }

    private void playSurahAudio(int surahIndex) {

        if (!isSurahAvailableForSelectedReciter(
                surahIndex
        )) {
            Toast.makeText(
                    this,
                    "هذه السورة غير متاحة بهذا القارئ",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }



        releaseQuranPlayer();

        currentAudioSurahIndex = surahIndex;
        audioPrepared = false;

        try {

            quranPlayer =
                    new android.media.MediaPlayer();

            quranPlayer.setAudioAttributes(
                    new android.media.AudioAttributes.Builder()
                            .setUsage(
                                    android.media.AudioAttributes.USAGE_MEDIA
                            )
                            .setContentType(
                                    android.media.AudioAttributes.CONTENT_TYPE_MUSIC
                            )
                            .build()
            );

            File localAudioFile = getLocalAudioFile(surahIndex);

            if (localAudioFile.exists()
                    && localAudioFile.length() > 0) {

                quranPlayer.setDataSource(
                        localAudioFile.getAbsolutePath()
                );

            } else {

                quranPlayer.setDataSource(
                        getQuranAudioUrl(surahIndex)
                );
            }

            quranPlayer.setOnPreparedListener(
                    mp -> {
                        audioPrepared = true;

                        int savedAudioPosition = getSavedAudioPosition(
                                currentAudioSurahIndex
                        );
                        int duration = mp.getDuration();

                        if (savedAudioPosition >= duration) {
                            savedAudioPosition = 0;
                        }

                        if (quranSeekBar != null) {
                            quranSeekBar.setMax(duration);
                            quranSeekBar.setProgress(savedAudioPosition);
                        }

                        if (quranMiniSeekBar != null) {
                            quranMiniSeekBar.setMax(
                                    Math.max(1, duration)
                            );
                            quranMiniSeekBar.setProgress(
                                    Math.max(0, savedAudioPosition)
                            );
                        }

                        if (savedAudioPosition > 0) {
                            mp.seekTo(savedAudioPosition);
                        }

                        mp.start();

                        if (quranPlayButton != null) {
                            quranPlayButton.setEnabled(true);
                            quranPlayButton.setText("⏸");

                            if (quranSkipBackButton != null) {
                                quranSkipBackButton.setEnabled(true);
                            }

                            if (quranSkipForwardButton != null) {
                                quranSkipForwardButton.setEnabled(true);
                            }
                        }

                        updateAudioTime();

                        if (quranMiniPlayer != null) {
                            quranMiniPlayer.setVisibility(View.VISIBLE);
                        }

                        if (quranMiniTitle != null) {
                            String miniTitle = "تلاوة القرآن";

                            try {
                                if (currentAudioSurahIndex >= 0
                                        && surahs != null
                                        && currentAudioSurahIndex < surahs.length()) {

                                    JSONObject audioSurah =
                                            surahs.getJSONObject(
                                                    currentAudioSurahIndex
                                            );

                                    miniTitle =
                                            audioSurah.optString(
                                                    "name",
                                                    "تلاوة القرآن"
                                            );
                                }
                            } catch (Exception ignored) {
                            }

                            quranMiniTitle.setText(miniTitle);
                        }

                        if (quranMiniSubtitle != null) {
                            String reciterName = "الشيخ أحمد العجمي";

                            try {
                                String savedReciterName =
                                        getSharedPreferences(
                                                PREF_AUDIO,
                                                MODE_PRIVATE
                                        ).getString(
                                                KEY_SELECTED_RECITER_NAME,
                                                ""
                                        );

                                if (savedReciterName != null
                                        && !savedReciterName.trim().isEmpty()) {
                                    reciterName =
                                            savedReciterName.trim();
                                }
                            } catch (Exception ignored) {
                            }

                            quranMiniSubtitle.setText(
                                    "بصوت " + reciterName
                            );
                        }

                        if (quranMiniPlayButton != null) {
                            quranMiniPlayButton.setText("⏸");
                        }

                        startAudioProgressUpdates();
                    }
            );

            quranPlayer.setOnCompletionListener(
                    mp -> {
                        if (currentAudioSurahIndex >= 0) {
                            getSharedPreferences(PREF_READING, MODE_PRIVATE)
                                    .edit()
                                    .remove(
                                            getAudioPositionKey(
                                                    currentAudioSurahIndex
                                            )
                                    )
                                    .apply();
                        }

                        if (quranSeekBar != null) {
                            quranSeekBar.setProgress(
                                    quranSeekBar.getMax()
                            );
                        }

                        if (quranPlayButton != null) {
                            quranPlayButton.setEnabled(true);
                            quranPlayButton.setText("▶");

                            if (quranSkipBackButton != null) {
                                quranSkipBackButton.setEnabled(false);
                            }

                            if (quranSkipForwardButton != null) {
                                quranSkipForwardButton.setEnabled(false);
                            }
                        }

                        stopAudioProgressUpdates();

                          if (quranMiniPlayButton != null) {
                              quranMiniPlayButton.setText("▶");
                          }

                          if (quranMiniTime != null) {
                              quranMiniTime.setText("00:00   •   00:00");
                          }

                          if (quranMiniSeekBar != null) {
                              quranMiniSeekBar.setProgress(0);
                          }

                          if (quranMiniPlayer != null) {
                              quranMiniPlayer.setVisibility(View.GONE);
                          }
                    }
            );

            quranPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        Toast.makeText(
                                this,
                                "تعذر تشغيل التلاوة",
                                Toast.LENGTH_SHORT
                        ).show();

                        if (quranPlayButton != null) {
                            quranPlayButton.setEnabled(true);
                            quranPlayButton.setText("▶");
                        }
                        if (quranSkipBackButton != null) {
                            quranSkipBackButton.setEnabled(false);
                        }
                        if (quranSkipForwardButton != null) {
                            quranSkipForwardButton.setEnabled(false);
                        }
                        releaseQuranPlayer();

                        return true;
                    }
            );

            Toast.makeText(
                    this,
                    "جاري تحميل التلاوة...",
                    Toast.LENGTH_SHORT
            ).show();

            if (quranPlayButton != null) {
                quranPlayButton.setText("…");
                quranPlayButton.setEnabled(false);
            }
            quranPlayer.prepareAsync();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "تعذر تشغيل التلاوة",
                    Toast.LENGTH_SHORT
            ).show();

            releaseQuranPlayer();
        }
    }

    // تحديث شريط التقدم والوقت
    private void startAudioProgressUpdates() {

        stopAudioProgressUpdates();

        audioProgressRunnable = new Runnable() {

            @Override
            public void run() {

                if (quranPlayer != null
                        && audioPrepared) {

                    try {

                        int position =
                                quranPlayer.getCurrentPosition();

                        if (quranSeekBar != null) {
                            quranSeekBar.setProgress(
                                    position
                            );
                        }

                        updateAudioTime();

                    } catch (Exception ignored) {
                    }
                }

                if (quranMiniTime != null
                        && quranPlayer != null
                        && audioPrepared) {
                    try {
                        int miniCurrent =
                                quranPlayer.getCurrentPosition();
                        int miniTotal =
                                quranPlayer.getDuration();

                        quranMiniTime.setText(
                                formatAudioTime(miniCurrent)
                                        + "   •   "
                                        + formatAudioTime(miniTotal)
                        );

                        if (quranMiniSeekBar != null) {
                            quranMiniSeekBar.setMax(
                                    Math.max(1, miniTotal)
                            );
                            quranMiniSeekBar.setProgress(
                                    Math.max(0, miniCurrent)
                            );
                        }

                        if (quranMiniPlayButton != null) {
                            quranMiniPlayButton.setText(
                                    quranPlayer.isPlaying()
                                            ? "⏸"
                                            : "▶"
                            );
                        }
                    } catch (Exception ignored) {
                    }
                }

                audioHandler.postDelayed(
                        this,
                        500
                );
            }
        };

        audioHandler.post(
                audioProgressRunnable
        );
    }

    private void stopAudioProgressUpdates() {

        if (audioProgressRunnable != null) {

            audioHandler.removeCallbacks(
                    audioProgressRunnable
            );

            audioProgressRunnable = null;
        }
    }

    private void updateAudioTime() {

        if (quranPlayer == null
                || !audioPrepared
                || quranAudioTime == null) {
            return;
        }

        try {

            int current =
                    quranPlayer.getCurrentPosition();

            int total =
                    quranPlayer.getDuration();

            quranAudioTime.setText(
                    formatAudioTime(current)
                            + "   •   "
                            + formatAudioTime(total)
            );

            quranAudioTime.setTypeface(
                    android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.BOLD
                    )
            );

        } catch (Exception ignored) {
        }
    }

    private String formatAudioTime(int milliseconds) {

        int totalSeconds =
                Math.max(0, milliseconds / 1000);

        int minutes =
                totalSeconds / 60;

        int seconds =
                totalSeconds % 60;

        return String.format(
                java.util.Locale.US,
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    @Override
    protected void onPause() {
        saveCurrentReadingPosition();
        saveCurrentAudioPosition();
            releaseQuranPlayer();
        super.onPause();
    }

    private void releaseQuranPlayer() {

        stopAudioProgressUpdates();

        audioPrepared = false;

        if (quranPlayer != null) {

            try {
                if (quranPlayer.isPlaying()) {
                    quranPlayer.stop();
                }
            } catch (Exception ignored) {
            }

            try {
                quranPlayer.reset();
            } catch (Exception ignored) {
            }

            try {
                quranPlayer.release();
            } catch (Exception ignored) {
            }

            quranPlayer = null;
        }

        currentAudioSurahIndex = -1;
    }


    @Override
    protected void onDestroy() {
        audioDownloadInProgress = false;

        if (audioDownloadThread != null) {
            try {
                audioDownloadThread.interrupt();
            } catch (Exception ignored) {
            }
            audioDownloadThread = null;
        }

        releaseQuranPlayer();
        super.onDestroy();
    }

}
