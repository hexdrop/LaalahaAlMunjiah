package com.laallaha.almunjiah;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Environment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeerahActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private static final int BORDER = Color.rgb(228, 231, 226);

    private final List<SeerahPart> parts = new ArrayList<>();

    private LinearLayout listContainer;
    private EditText searchBox;

    private MediaPlayer mediaPlayer;
    private SeekBar activeSeekBar;
    private TextView activePlayButton;
    private TextView activeCurrentTime;
    private TextView activeTotalTime;
    private ProgressBar activeProgress;

    private SeerahPart activePart;

    private final Handler handler = new Handler();

    private static final String BASE =
            "https://media.myhuda.com/download/files/media/" +
            "4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/3-Seirah/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);

        loadSeerah();
        buildPage();
    }

    private void loadSeerah() {

        parts.clear();

        add(1, "حال العرب قبل البعثة - السيرة النبوية",
                "1.mp3");

        add(2, "خبر أصحاب الفيل ونذر عبد المطلب ذبح إبنه - السيرة النبوية",
                "2.mp3");

        add(3, "مولد خير البشر صلى الله عليه وسلم - السيرة النبوية",
                "3.mp3");

        add(4, "علامات النبوة وأحداث ما قبل البعثة - السيرة النبوية",
                "4.mp3");

        add(5, "نزول الوحى - السيرة النبوية",
                "5.mp3");

        add(6, "بدأ الدعوة إلى الله - السيرة النبوية",
                "6.mp3");

        add(7, "جدال كفار قريش ومعارضتهم الدعوة للإسلام - السيرة النبوية",
                "7.mp3");

        add(8, "صد كفار قريش عن الإسلام وتعذيبهم للمسلمين - السيرة النبوية",
                "8.mp3");

        add(9, "الهجرة الأولى إلى الحبشة وإسلام حمزة وعمر - السيرة النبوية",
                "9.mp3");

        add(10, "مقاطعة قريش للمسلمين وحصارهم وعام الحزن - السيرة النبوية",
                "10.mp3");

        add(11, "الإسراء والمعراج - دعوة الحجيج - بيعة العقبة - السيرة النبوية",
                "11.mp3");

        add(12, "الهجرة إلى المدينة وما واكبها من أحداث - السيرة النبوية",
                "12.mp3");

        add(13, "استقبال النبي صلى الله عليه وسلم بالمدينة - السيرة النبوية",
                "13.mp3");

        add(14, "الإذن بالقتال - تحويل القبلة - غزوة بدر - السيرة النبوية",
                "14.mp3");

        add(15, "أحداث غزوة بدر الكبرى - السيرة النبوية",
                "15.mp3");

        add(16, "تبعات الإنتصار في بدر - يهود بني قينقاع - السيرة النبوية",
                "16.mp3");

        add(17, "أحداث غزوة أحد - السيرة النبوية",
                "17.mp3");

        add(18, "نتائج غزوة أحد - إجلاء يهود بنو النضير - السيرة النبوية",
                "18.mp3");

        add(19, "غزوة الأحزاب - السيرة النبوية",
                "19.mp3");

        /*
         * لا يوجد رابط 20.mp3 ضمن الروابط التي زودتنا بها.
         * لذلك لا نضيف جزءًا وهميًا.
         */

        add(20, "غزوة بني المصطلق - حادثة الإفك - السيرة النبوية",
                "21.mp3");

        add(21, "عمرة الحديبية - صلح الحديبية - السيرة النبوية",
                "22.mp3");

        add(22, "فتح خيبر - السيرة النبوية",
                "23.mp3");

        add(23, "عمرة القضاء - معركة مؤثة - السيرة النبوية",
                "24.mp3");

        add(24, "فتح مكة - السيرة النبوية",
                "25.mp3");

        add(25, "غزوة حنين - سلسلة السيرة النبوية",
                "26.mp3");

        add(26, "حجة الوداع - سلسلة السيرة النبوية",
                "28.mp3");

        add(27, "وفاة الرسول صلى الله عليه وسلم - السيرة النبوية",
                "29.mp3");
    }

    private void add(int number, String title, String fileName) {
        parts.add(new SeerahPart(
                number,
                title,
                BASE + fileName,
                "seerah_" + String.format(Locale.US, "%02d", number) + ".mp3"
        ));
    }

    private void buildPage() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        root.addView(buildHeader());

        searchBox = new EditText(this);
        searchBox.setHint("ابحث في السيرة النبوية...");
        searchBox.setTextSize(14);
        searchBox.setTextColor(TEXT);
        searchBox.setHintTextColor(TEXT_SECONDARY);
        searchBox.setSingleLine(true);
        searchBox.setPadding(dp(18), 0, dp(18), 0);

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(WHITE);
        searchBg.setCornerRadius(dp(18));
        searchBg.setStroke(dp(1), BORDER);
        searchBox.setBackground(searchBg);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        searchParams.setMargins(
                dp(18),
                dp(14),
                dp(18),
                dp(10)
        );

        root.addView(searchBox, searchParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setPadding(dp(16), 0, dp(16), dp(28));

        scroll.addView(listContainer);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        setContentView(root);

        searchBox.addTextChangedListener(
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
                        renderList(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {}
                }
        );

        renderList("");
    }

    private View buildHeader() {

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setPadding(dp(20), dp(22), dp(20), dp(22));

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        EMERALD_DARK,
                        EMERALD
                }
        );

        header.setBackground(bg);

        LinearLayout.LayoutParams hp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(170)
                );

        TextView back = text("‹", 34, WHITE);
        back.setGravity(Gravity.CENTER);
        back.setTypeface(Typeface.DEFAULT_BOLD);

        back.setOnClickListener(v -> finish());

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(
                        dp(46),
                        dp(46)
                );

        backParams.gravity = Gravity.RIGHT;
        header.addView(back, backParams);

        TextView title = text(
                "السيرة النبوية",
                27,
                WHITE
        );

        title.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                )
        );

        title.setGravity(Gravity.CENTER);
        header.addView(title);

        TextView subtitle = text(
                "سيرة خير البشر ﷺ",
                15,
                Color.rgb(232, 239, 234)
        );

        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(5), 0, 0);

        header.addView(subtitle);

        TextView count = text(
                "27 جزءًا • استماع وتحميل بدون إنترنت",
                12,
                Color.rgb(210, 226, 217)
        );

        count.setGravity(Gravity.CENTER);
        count.setPadding(0, dp(7), 0, 0);

        header.addView(count);

        return header;
    }

    private void renderList(String query) {

        if (listContainer == null) return;

        listContainer.removeAllViews();

        String q = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        for (SeerahPart part : parts) {

            if (!q.isEmpty()
                    && !part.title.toLowerCase(Locale.ROOT).contains(q)) {
                continue;
            }

            listContainer.addView(
                    createPartCard(part)
            );
        }

        if (listContainer.getChildCount() == 0) {

            TextView empty = text(
                    "لا توجد نتائج مطابقة",
                    15,
                    TEXT_SECONDARY
            );

            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(35), 0, dp(35));

            listContainer.addView(empty);
        }
    }

    private View createPartCard(SeerahPart part) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(16));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(WHITE);
        bg.setCornerRadius(dp(20));
        bg.setStroke(dp(1), BORDER);

        card.setBackground(bg);
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cp.setMargins(0, dp(7), 0, dp(7));

        card.setLayoutParams(cp);

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView number = text(
                String.valueOf(part.number),
                15,
                WHITE
        );

        number.setGravity(Gravity.CENTER);

        GradientDrawable numberBg = new GradientDrawable();
        numberBg.setColor(EMERALD);
        numberBg.setShape(GradientDrawable.OVAL);

        number.setBackground(numberBg);

        top.addView(
                number,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                )
        );

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setPadding(dp(12), 0, dp(8), 0);

        TextView title = text(
                part.title,
                15,
                TEXT
        );

        title.setTypeface(
                Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                )
        );

        title.setGravity(Gravity.RIGHT);

        titleBox.addView(title);

        TextView small = text(
                "السيرة النبوية • الجزء " + part.number,
                11,
                TEXT_SECONDARY
        );

        small.setPadding(0, dp(5), 0, 0);
        small.setGravity(Gravity.RIGHT);

        titleBox.addView(small);

        top.addView(
                titleBox,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        card.addView(top);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(13), 0, 0);

        TextView play = text(
                "▶",
                17,
                WHITE
        );

        play.setGravity(Gravity.CENTER);

        GradientDrawable playBg = new GradientDrawable();
        playBg.setColor(EMERALD_DARK);
        playBg.setCornerRadius(dp(16));

        play.setBackground(playBg);

        controls.addView(
                play,
                new LinearLayout.LayoutParams(
                        dp(50),
                        dp(45)
                )
        );

        SeekBar seek = new SeekBar(this);

        LinearLayout.LayoutParams seekParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(42),
                        1
                );

        seekParams.setMargins(
                dp(8),
                0,
                dp(8),
                0
        );

        controls.addView(seek, seekParams);

        TextView download = text(
                "↓",
                22,
                EMERALD_DARK
        );

        download.setGravity(Gravity.CENTER);

        GradientDrawable downloadBg = new GradientDrawable();
        downloadBg.setColor(EMERALD_SOFT);
        downloadBg.setCornerRadius(dp(16));
        downloadBg.setStroke(dp(1), BORDER);

        download.setBackground(downloadBg);

        controls.addView(
                download,
                new LinearLayout.LayoutParams(
                        dp(50),
                        dp(45)
                )
        );

        card.addView(controls);

        LinearLayout times = new LinearLayout(this);
        times.setOrientation(LinearLayout.HORIZONTAL);

        TextView current = text(
                "00:00",
                10,
                TEXT_SECONDARY
        );

        TextView total = text(
                "00:00",
                10,
                TEXT_SECONDARY
        );

        times.addView(
                current,
                new LinearLayout.LayoutParams(
                        0,
                        dp(20),
                        1
                )
        );

        total.setGravity(Gravity.RIGHT);

        times.addView(
                total,
                new LinearLayout.LayoutParams(
                        0,
                        dp(20),
                        1
                )
        );

        card.addView(times);

        ProgressBar progress = new ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
        );

        progress.setVisibility(View.GONE);

        card.addView(
                progress,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(4)
                )
        );

        File localFile = getAudioFile(part);

        if (localFile.exists()) {

            download.setText("✓");
            download.setTextColor(EMERALD_DARK);

            GradientDrawable downloadedBg = new GradientDrawable();
            downloadedBg.setColor(EMERALD_SOFT);
            downloadedBg.setCornerRadius(dp(16));
            downloadedBg.setStroke(dp(1), EMERALD);

            download.setBackground(downloadedBg);
        }

        play.setOnClickListener(
                v -> playPart(
                        part,
                        play,
                        seek,
                        current,
                        total
                )
        );

        download.setOnClickListener(
                v -> downloadPart(
                        part,
                        download,
                        progress
                )
        );

        seek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar bar,
                            int progressValue,
                            boolean fromUser
                    ) {
                        if (fromUser
                                && activePart == part
                                && mediaPlayer != null
                                && mediaPlayer.isPlaying()) {

                            mediaPlayer.seekTo(progressValue);
                        }

                        if (activePart == part
                                && mediaPlayer != null) {

                            current.setText(
                                    formatTime(
                                            mediaPlayer.getCurrentPosition()
                                    )
                            );
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar bar
                    ) {}

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar bar
                    ) {
                        if (activePart == part
                                && mediaPlayer != null) {

                            mediaPlayer.seekTo(
                                    bar.getProgress()
                            );
                        }
                    }
                }
        );

        return card;
    }

    private void playPart(
            SeerahPart part,
            TextView button,
            SeekBar seek,
            TextView current,
            TextView total
    ) {

        try {

            if (activePart == part
                    && mediaPlayer != null) {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();
                    button.setText("▶");

                } else {

                    mediaPlayer.start();
                    button.setText("Ⅱ");
                    updateProgress();
                }

                return;
            }

            stopPlayer();

            activePart = part;
            activePlayButton = button;
            activeSeekBar = seek;
            activeCurrentTime = current;
            activeTotalTime = total;

            button.setText("…");

            File local = getAudioFile(part);

            mediaPlayer = new MediaPlayer();

            if (local.exists()) {
                mediaPlayer.setDataSource(
                        local.getAbsolutePath()
                );
            } else {
                mediaPlayer.setDataSource(
                        part.audioUrl
                );
            }

            mediaPlayer.setOnPreparedListener(
                    mp -> {

                        prepared = true;

                        seek.setMax(
                                mp.getDuration()
                        );

                        total.setText(
                                formatTime(
                                        mp.getDuration()
                                )
                        );

                        mp.start();

                        button.setText("Ⅱ");

                        updateProgress();
                    }
            );

            mediaPlayer.setOnCompletionListener(
                    mp -> {

                        button.setText("▶");
                        seek.setProgress(0);
                        current.setText("00:00");

                        activePart = null;
                        prepared = false;
                    }
            );

            mediaPlayer.setOnErrorListener(
                    (mp, what, extra) -> {

                        button.setText("▶");

                        Toast.makeText(
                                this,
                                "تعذر تشغيل الحلقة",
                                Toast.LENGTH_SHORT
                        ).show();

                        stopPlayer();

                        return true;
                    }
            );

            mediaPlayer.prepareAsync();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "تعذر تشغيل الحلقة",
                    Toast.LENGTH_SHORT
            ).show();

            stopPlayer();
        }
    }

    private boolean prepared = false;

    private void updateProgress() {

        if (mediaPlayer == null
                || activeSeekBar == null
                || !prepared) {
            return;
        }

        try {

            int position =
                    mediaPlayer.getCurrentPosition();

            activeSeekBar.setProgress(position);

            if (activeCurrentTime != null) {
                activeCurrentTime.setText(
                        formatTime(position)
                );
            }

            if (mediaPlayer.isPlaying()) {

                handler.postDelayed(
                        this::updateProgress,
                        500
                );
            }

        } catch (Exception ignored) {}
    }

    private void stopPlayer() {

        if (mediaPlayer != null) {

            try {
                mediaPlayer.stop();
            } catch (Exception ignored) {}

            try {
                mediaPlayer.release();
            } catch (Exception ignored) {}

            mediaPlayer = null;
        }

        prepared = false;

        if (activePlayButton != null) {
            activePlayButton.setText("▶");
        }

        activePart = null;
        activePlayButton = null;
        activeSeekBar = null;
        activeCurrentTime = null;
        activeTotalTime = null;
    }

    private void downloadPart(
            SeerahPart part,
            TextView button,
            ProgressBar progress
    ) {

        File target = getAudioFile(part);

        if (target.exists()) {

            Toast.makeText(
                    this,
                    "الحلقة محفوظة بالفعل للاستماع بدون إنترنت",
                    Toast.LENGTH_SHORT
            ).show();

            button.setText("✓");
            return;
        }

        button.setText("…");
        progress.setVisibility(View.VISIBLE);
        progress.setProgress(0);

        new Thread(() -> {

            File temp = new File(
                    target.getParentFile(),
                    target.getName() + ".tmp"
            );

            HttpURLConnection connection = null;

            try {

                URL url = new URL(part.audioUrl);

                connection =
                        (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(20000);
                connection.setReadTimeout(30000);
                connection.setRequestMethod("GET");
                connection.connect();

                int length =
                        connection.getContentLength();

                InputStream input =
                        connection.getInputStream();

                FileOutputStream output =
                        new FileOutputStream(temp);

                byte[] buffer = new byte[8192];

                long downloaded = 0;
                int read;

                while ((read = input.read(buffer)) != -1) {

                    output.write(buffer, 0, read);
                    downloaded += read;

                    if (length > 0) {

                        int percent =
                                (int) (
                                        downloaded * 100L
                                                / length
                                );

                        runOnUiThread(() ->
                                progress.setProgress(percent)
                        );
                    }
                }

                output.flush();
                output.close();
                input.close();

                if (target.exists()) {
                    target.delete();
                }

                if (!temp.renameTo(target)) {
                    throw new Exception(
                            "Could not rename downloaded file"
                    );
                }

                runOnUiThread(() -> {

                    progress.setVisibility(View.GONE);

                    button.setText("✓");
                    button.setTextColor(EMERALD_DARK);

                    Toast.makeText(
                            this,
                            "تم تحميل الحلقة للاستماع بدون إنترنت",
                            Toast.LENGTH_SHORT
                    ).show();
                });

            } catch (Exception e) {

                if (temp.exists()) {
                    temp.delete();
                }

                runOnUiThread(() -> {

                    progress.setVisibility(View.GONE);
                    button.setText("↓");

                    Toast.makeText(
                            this,
                            "فشل تحميل الحلقة",
                            Toast.LENGTH_SHORT
                    ).show();
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    private File getAudioFile(SeerahPart part) {

        File dir = getExternalFilesDir(
                Environment.DIRECTORY_MUSIC
        );

        if (dir == null) {
            dir = getFilesDir();
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(
                dir,
                part.localFileName
        );
    }

    private String formatTime(int milliseconds) {

        int totalSeconds =
                Math.max(0, milliseconds / 1000);

        int minutes =
                totalSeconds / 60;

        int seconds =
                totalSeconds % 60;

        return String.format(
                Locale.US,
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    private TextView text(
            String value,
            float size,
            int color
    ) {

        TextView t = new TextView(this);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        return t;
    }

    private int dp(int value) {

        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
                        + 0.5f
        );
    }

    @Override
    protected void onDestroy() {

        stopPlayer();

        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    private static class SeerahPart {

        int number;
        String title;
        String audioUrl;
        String localFileName;

        SeerahPart(
                int number,
                String title,
                String audioUrl,
                String localFileName
        ) {
            this.number = number;
            this.title = title;
            this.audioUrl = audioUrl;
            this.localFileName = localFileName;
        }
    }
}
