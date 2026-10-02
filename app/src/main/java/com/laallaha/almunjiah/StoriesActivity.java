package com.laallaha.almunjiah;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoriesActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private static final int BORDER = Color.rgb(228, 231, 226);

    private LinearLayout listContainer;
    private EditText searchBox;

    private final List<Prophet> prophets = new ArrayList<>();
    private static final String ADAM_AUDIO_URL =
            "https://media.myhuda.com/download/files/media/4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/2-Qassas_Anbiaa/1.mp3";

    private MediaPlayer mediaPlayer;
    private TextView activePlayButton;
    private ProgressBar activeDownloadProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);

        try {
            loadProphets();
            buildPage();
        } catch (Throwable e) {
            showCrashDetails(e);
        }
    }


    private LinearLayout createAdamAudioControls() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(0, dp(10), 0, 0);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER_VERTICAL);

        TextView play = text("▶ استماع", 13, WHITE, true);
        play.setGravity(Gravity.CENTER);
        play.setPadding(dp(14), dp(8), dp(14), dp(8));

        GradientDrawable playBg = new GradientDrawable();
        playBg.setColor(EMERALD);
        playBg.setCornerRadius(dp(12));
        play.setBackground(playBg);

        TextView download = text("⬇ تنزيل", 13, EMERALD_DARK, true);
        download.setGravity(Gravity.CENTER);
        download.setPadding(dp(14), dp(8), dp(14), dp(8));

        GradientDrawable downloadBg = new GradientDrawable();
        downloadBg.setColor(EMERALD_SOFT);
        downloadBg.setCornerRadius(dp(12));
        download.setBackground(downloadBg);

        ProgressBar progress = new ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
        );
        progress.setMax(100);
        progress.setProgress(0);
        progress.setVisibility(View.GONE);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(42),
                        1f
                );
        buttonParams.setMargins(0, 0, dp(6), 0);

        buttons.addView(play, buttonParams);

        LinearLayout.LayoutParams downloadParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(42),
                        1f
                );

        buttons.addView(download, downloadParams);

        box.addView(
                buttons,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                )
        );

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(5)
                );
        progressParams.setMargins(0, dp(8), 0, 0);

        box.addView(progress, progressParams);

        File audioFile = getAdamAudioFile();

        if (audioFile.exists() && audioFile.length() > 0) {
            download.setText("✓ أوفلاين");
        }

        play.setOnClickListener(v -> {
            File file = getAdamAudioFile();

            if (mediaPlayer != null && mediaPlayer.isPlaying()
                    && activePlayButton == play) {
                stopAudio();
                play.setText("▶ استماع");
                return;
            }

            if (file.exists() && file.length() > 0) {
                playLocalAudio(file, play);
            } else {
                playOnlineAudio(play);
            }
        });

        download.setOnClickListener(v -> {
            File file = getAdamAudioFile();

            if (file.exists() && file.length() > 0) {
                Toast.makeText(
                        StoriesActivity.this,
                        "قصة آدم متاحة بالفعل أوفلاين",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            downloadAdamAudio(progress, download);
        });

        return box;
    }

    private File getAdamAudioFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (dir == null) {
            dir = getFilesDir();
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(dir, "adam_story.mp3");
    }

    private void playLocalAudio(File file, TextView button) {
        stopAudio();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getAbsolutePath());

            activePlayButton = button;
            button.setText("⏳ تشغيل...");

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                button.setText("⏸ إيقاف");
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                button.setText("▶ استماع");
                stopAudio();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                button.setText("▶ استماع");

                Toast.makeText(
                        StoriesActivity.this,
                        "تعذر تشغيل الملف الصوتي",
                        Toast.LENGTH_SHORT
                ).show();

                stopAudio();
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Throwable e) {
            button.setText("▶ استماع");

            Toast.makeText(
                    StoriesActivity.this,
                    "تعذر تشغيل القصة الصوتية",
                    Toast.LENGTH_SHORT
            ).show();

            stopAudio();
        }
    }

    private void playOnlineAudio(TextView button) {
        stopAudio();

        try {
            mediaPlayer = new MediaPlayer();
            activePlayButton = button;

            button.setText("⏳ تحميل...");

            mediaPlayer.setAudioStreamType(
                    android.media.AudioManager.STREAM_MUSIC
            );

            mediaPlayer.setDataSource(ADAM_AUDIO_URL);

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                button.setText("⏸ إيقاف");
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                button.setText("▶ استماع");
                stopAudio();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                button.setText("▶ استماع");

                Toast.makeText(
                        StoriesActivity.this,
                        "تعذر تشغيل التسجيل من الإنترنت",
                        Toast.LENGTH_LONG
                ).show();

                stopAudio();
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Throwable e) {
            button.setText("▶ استماع");

            Toast.makeText(
                    this,
                    "تعذر تشغيل التسجيل",
                    Toast.LENGTH_LONG
            ).show();

            stopAudio();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Throwable ignored) {
            }

            try {
                mediaPlayer.release();
            } catch (Throwable ignored) {
            }

            mediaPlayer = null;
        }

        if (activePlayButton != null) {
            activePlayButton.setText("▶ استماع");
            activePlayButton = null;
        }
    }

    private void downloadAdamAudio(
            ProgressBar progress,
            TextView downloadButton
    ) {
        progress.setProgress(0);
        progress.setVisibility(View.VISIBLE);
        downloadButton.setText("⏳ تنزيل...");

        new Thread(() -> {
            File target = getAdamAudioFile();
            File temp = new File(target.getParentFile(), "adam_story.tmp");

            HttpURLConnection connection = null;

            try {
                URL url = new URL(ADAM_AUDIO_URL);

                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode < 200 || responseCode >= 300) {
                    throw new Exception(
                            "HTTP " + responseCode
                    );
                }

                int total = connection.getContentLength();

                try (
                        InputStream input = connection.getInputStream();
                        FileOutputStream output =
                                new FileOutputStream(temp)
                ) {
                    byte[] buffer = new byte[8192];
                    int read;
                    long downloaded = 0;

                    while ((read = input.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                        downloaded += read;

                        if (total > 0) {
                            int percent =
                                    (int) ((downloaded * 100L) / total);

                            runOnUiThread(() ->
                                    progress.setProgress(percent)
                            );
                        }
                    }

                    output.flush();
                }

                if (!temp.exists() || temp.length() == 0) {
                    throw new Exception("Empty audio file");
                }

                if (target.exists()) {
                    target.delete();
                }

                if (!temp.renameTo(target)) {
                    throw new Exception("Could not save audio");
                }

                runOnUiThread(() -> {
                    progress.setProgress(100);
                    progress.setVisibility(View.GONE);
                    downloadButton.setText("✓ أوفلاين");

                    Toast.makeText(
                            StoriesActivity.this,
                            "تم تنزيل قصة آدم للاستماع أوفلاين ✓",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } catch (Throwable e) {
                if (temp.exists()) {
                    temp.delete();
                }

                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    downloadButton.setText("⬇ تنزيل");

                    Toast.makeText(
                            StoriesActivity.this,
                            "فشل تنزيل القصة",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(
                            CONNECTIVITY_SERVICE
                    );

            if (cm == null) {
                return false;
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.net.Network network = cm.getActiveNetwork();

                if (network == null) {
                    return false;
                }

                android.net.NetworkCapabilities capabilities =
                        cm.getNetworkCapabilities(network);

                return capabilities != null &&
                        capabilities.hasCapability(
                                android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
                        );
            }

            NetworkInfo info = cm.getActiveNetworkInfo();

            return info != null && info.isConnected();

        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        stopAudio();
        super.onDestroy();
    }

    private void showCrashDetails(Throwable e) {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(40), dp(24), dp(24));
        root.setBackgroundColor(CREAM);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = text(
                "حدث خطأ في صفحة القصص",
                22,
                EMERALD_DARK,
                true
        );

        title.setGravity(Gravity.CENTER);

        TextView details = text(
                e.getClass().getName() +
                "\n\n" +
                String.valueOf(e.getMessage()),
                14,
                TEXT,
                false
        );

        details.setTextIsSelectable(true);
        details.setGravity(Gravity.CENTER);

        TextView back = text(
                "رجوع",
                16,
                WHITE,
                true
        );

        back.setGravity(Gravity.CENTER);
        GradientDrawable backBg = new GradientDrawable();
        backBg.setColor(EMERALD);
        backBg.setCornerRadius(dp(14));
        back.setBackground(backBg);

        back.setOnClickListener(v -> finish());

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(60)
                )
        );

        root.addView(
                details,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        root.addView(
                back,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                )
        );

        setContentView(root);
    }

    private void loadProphets() {
    prophets.clear();

    String base =
            "https://media.myhuda.com/download/files/media/4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/2-Qassas_Anbiaa/";

    prophets.add(new Prophet(
            "آدم عليه السلام",
            "أبو البشر وبداية قصة الإنسان",
            "قصة الخلق والتوبة والعودة إلى الله.",
            base + "1.mp3",
            "adam_story.mp3"
    ));

    prophets.add(new Prophet(
            "إدريس عليه السلام",
            "نبي الصبر والعمل الصالح",
            "من عباد الله الذين أثنى الله عليهم ورفع مكانتهم."
    ));

    prophets.add(new Prophet(
            "نوح عليه السلام",
            "نبي الصبر والثبات",
            "دعا قومه إلى توحيد الله وصبر على أذى قومه.",
            base + "2.mp3",
            "nooh_story.mp3"
    ));

    prophets.add(new Prophet(
            "هود عليه السلام",
            "نبي قوم عاد",
            "دعا قومه إلى عبادة الله وترك الكبر والطغيان.",
            base + "3.mp3",
            "hud_saleh_story.mp3"
    ));

    prophets.add(new Prophet(
            "صالح عليه السلام",
            "نبي قوم ثمود",
            "دعا قومه إلى التوحيد وكانت الناقة آية لقومه.",
            base + "3.mp3",
            "hud_saleh_story.mp3"
    ));

    prophets.add(new Prophet(
            "إبراهيم عليه السلام",
            "خليل الرحمن",
            "إمام التوحيد الذي واجه الشرك وثبت على الحق.",
            base + "4.mp3",
            "ibrahim_story.mp3"
    ));

    prophets.add(new Prophet(
            "لوط عليه السلام",
            "نبي الصبر والثبات",
            "دعا قومه إلى التقوى وترك الفواحش.",
            base + "5.mp3",
            "loot_ismail_ishaq_shuaib_story.mp3"
    ));

    prophets.add(new Prophet(
            "إسماعيل عليه السلام",
            "نبي الصدق والوفاء",
            "وصفه الله بأنه صادق الوعد ومن الصابرين.",
            base + "5.mp3",
            "loot_ismail_ishaq_shuaib_story.mp3"
    ));

    prophets.add(new Prophet(
            "إسحاق عليه السلام",
            "نبي من ذرية إبراهيم",
            "بشارة من الله لإبراهيم وسارة وعبد صالح من الأنبياء.",
            base + "5.mp3",
            "loot_ismail_ishaq_shuaib_story.mp3"
    ));

    prophets.add(new Prophet(
            "يعقوب عليه السلام",
            "إسرائيل الله",
            "نبي كريم عُرف بالصبر الجميل والثقة بالله."
    ));

    prophets.add(new Prophet(
            "يوسف عليه السلام",
            "قصة الصبر والعفو",
            "قصة مليئة بالابتلاء والصبر ثم التمكين والعفو.",
            base + "6.mp3",
            "yusuf_story.mp3"
    ));

    prophets.add(new Prophet(
            "أيوب عليه السلام",
            "نبي الصبر",
            "ابتُلي فصبر، فكان مثالًا عظيمًا في الثبات وحسن الظن بالله.",
            base + "7.mp3",
            "ayoub_younus_musa_story.mp3"
    ));

    prophets.add(new Prophet(
            "شعيب عليه السلام",
            "نبي الأمانة والعدل",
            "دعا قومه إلى التوحيد والعدل في البيع والشراء.",
            base + "5.mp3",
            "loot_ismail_ishaq_shuaib_story.mp3"
    ));

    prophets.add(new Prophet(
            "موسى عليه السلام",
            "كليم الله",
            "قصة مواجهة فرعون والخروج ببني إسرائيل ونصر الله.",
            base + "8.mp3",
            "musa_story.mp3"
    ));

    prophets.add(new Prophet(
            "هارون عليه السلام",
            "نبي مؤازر لموسى",
            "أرسله الله مع موسى معينًا له في الدعوة."
    ));

    prophets.add(new Prophet(
            "داود عليه السلام",
            "النبي الملك",
            "آتاه الله الملك والحكمة والزبور.",
            base + "9.mp3",
            "yusha_dawud_sulayman_zakariya_yahya_story.mp3"
    ));

    prophets.add(new Prophet(
            "سليمان عليه السلام",
            "نبي الملك والحكمة",
            "آتاه الله ملكًا عظيمًا وسخر له من خلقه ما شاء.",
            base + "9.mp3",
            "yusha_dawud_sulayman_zakariya_yahya_story.mp3"
    ));

    prophets.add(new Prophet(
            "يونس عليه السلام",
            "نبي التوبة والرجوع",
            "قصة عظيمة في الدعاء والتوبة والفرج بعد الكرب.",
            base + "7.mp3",
            "ayoub_younus_musa_story.mp3"
    ));

    prophets.add(new Prophet(
            "زكريا عليه السلام",
            "نبي الدعاء والرجاء",
            "دعا ربه سرًا ورزقه الله يحيى.",
            base + "9.mp3",
            "yusha_dawud_sulayman_zakariya_yahya_story.mp3"
    ));

    prophets.add(new Prophet(
            "يحيى عليه السلام",
            "نبي الطهارة والتقوى",
            "آتاه الله الحكم صبيًا وجعله سيدًا وحصورًا ونبيًا صالحًا.",
            base + "9.mp3",
            "yusha_dawud_sulayman_zakariya_yahya_story.mp3"
    ));

    prophets.add(new Prophet(
            "عيسى عليه السلام",
            "كلمة الله ورسوله",
            "أرسله الله إلى بني إسرائيل مؤيدًا بالآيات.",
            base + "10.mp3",
            "isa_story.mp3"
    ));

    prophets.add(new Prophet(
            "محمد ﷺ",
            "خاتم الأنبياء والمرسلين",
            "رحمة للعالمين، أرسله الله بالهدى ودين الحق."
    ));
}

private void buildPage() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        root.addView(buildHeader());

        searchBox = new EditText(this);
        searchBox.setHint("ابحث عن نبي...");
        searchBox.setTextSize(14);
        searchBox.setTextColor(TEXT);
        searchBox.setHintTextColor(TEXT_SECONDARY);
        searchBox.setSingleLine(true);
        searchBox.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(WHITE);
        searchBg.setCornerRadius(dp(18));
        searchBg.setStroke(dp(1), BORDER);
        searchBox.setBackground(searchBg);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        searchParams.setMargins(
                dp(18),
                dp(16),
                dp(18),
                dp(8)
        );

        root.addView(searchBox, searchParams);

        TextView count = text(
                "قصص الأنبياء والمرسلين",
                18,
                TEXT,
                true
        );

        count.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams countParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(34)
                );

        countParams.setMargins(
                dp(20),
                dp(4),
                dp(20),
                0
        );

        root.addView(count, countParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setPadding(
                dp(18),
                dp(8),
                dp(18),
                dp(25)
        );

        scroll.addView(listContainer);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

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
                        filterProphets(
                                s == null ? "" : s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {}
                }
        );

        setContentView(root);

        renderProphets(prophets);
    }

    private LinearLayout buildHeader() {

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.RIGHT);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        EMERALD_DARK,
                        EMERALD
                }
        );

        bg.setCornerRadii(new float[]{
                0,0,
                0,0,
                dp(28),dp(28),
                dp(28),dp(28)
        });

        header.setBackground(bg);
        header.setPadding(
                dp(22),
                dp(20),
                dp(22),
                dp(22)
        );

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text(
                "‹",
                34,
                WHITE,
                false
        );

        back.setGravity(Gravity.CENTER);
        back.setOnClickListener(v -> finish());

        top.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                )
        );

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setGravity(Gravity.RIGHT);

        TextView title = text(
                "قصص الأنبياء",
                25,
                WHITE,
                true
        );

        TextView subtitle = text(
                "نورٌ من قصصهم، وعِبرٌ لحياتنا",
                12,
                Color.rgb(220, 239, 230),
                false
        );

        titleBox.addView(title);
        titleBox.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(25)
                )
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        titleParams.setMargins(
                dp(10),
                0,
                0,
                0
        );

        top.addView(titleBox, titleParams);

        TextView star = text(
                "✦",
                25,
                GOLD,
                true
        );

        star.setGravity(Gravity.CENTER);

        top.addView(
                star,
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                )
        );

        header.addView(top);

        TextView quote = text(
                "وَكُلًّا نَقُصُّ عَلَيْكَ مِنْ أَنبَاءِ الرُّسُلِ",
                15,
                WHITE,
                true
        );

        quote.setGravity(Gravity.RIGHT);
        quote.setPadding(
                0,
                dp(10),
                0,
                0
        );

        header.addView(quote);

        return header;
    }

    private void renderProphets(List<Prophet> data) {

        listContainer.removeAllViews();

        if (data.isEmpty()) {

            TextView empty = text(
                    "لا توجد نتيجة بهذا الاسم",
                    15,
                    TEXT_SECONDARY,
                    false
            );

            empty.setGravity(Gravity.CENTER);

            listContainer.addView(
                    empty,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(120)
                    )
            );

            return;
        }

        for (int i = 0; i < data.size(); i++) {
            listContainer.addView(
                    createProphetCard(
                            data.get(i),
                            i
                    )
            );
        }
    }

    private LinearLayout createProphetCard(
            Prophet prophet,
            int index
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(
                dp(15),
                dp(13),
                dp(15),
                dp(13)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                index % 5 == 0
                        ? Color.rgb(247, 251, 248)
                        : WHITE
        );

        bg.setCornerRadius(dp(21));
        bg.setStroke(dp(1), BORDER);

        card.setBackground(bg);
        card.setElevation(dp(2));

        TextView arrow = text(
                "‹",
                27,
                EMERALD,
                true
        );

        arrow.setGravity(Gravity.CENTER);

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        dp(28),
                        dp(45)
                )
        );

        LinearLayout textBox =
                new LinearLayout(this);

        textBox.setOrientation(
                LinearLayout.VERTICAL
        );

        textBox.setGravity(Gravity.RIGHT);

        TextView name = text(
                prophet.name,
                18,
                EMERALD_DARK,
                true
        );

        TextView subtitle = text(
                prophet.subtitle,
                12,
                GOLD,
                true
        );

        TextView description = text(
                prophet.description,
                12,
                TEXT_SECONDARY,
                false
        );

        textBox.addView(name);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(22)
                );

        textBox.addView(
                subtitle,
                subParams
        );

        textBox.addView(description);
        if ("آدم عليه السلام".equals(prophet.name)) {
            textBox.addView(createAdamAudioControls());
        }


        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        textParams.setMargins(
                dp(10),
                0,
                dp(10),
                0
        );

        card.addView(
                textBox,
                textParams
        );

        TextView number = text(
                String.valueOf(index + 1),
                12,
                EMERALD,
                true
        );

        GradientDrawable numberBg =
                new GradientDrawable();

        numberBg.setColor(EMERALD_SOFT);
        numberBg.setShape(
                GradientDrawable.OVAL
        );

        number.setBackground(numberBg);
        number.setGravity(Gravity.CENTER);

        card.addView(
                number,
                new LinearLayout.LayoutParams(
                        dp(36),
                        dp(36)
                )
        );

        card.setOnClickListener(v -> {

            // النبي محمد ﷺ له صفحة مستقلة للسيرة النبوية
            if ("محمد ﷺ".equals(prophet.name)) {

                android.content.Intent intent =
                        new android.content.Intent(
                                StoriesActivity.this,
                                SeerahActivity.class
                        );

                startActivity(intent);

            } else {

                // باقي الأنبياء يفتحون صفحة القصة العادية
                android.content.Intent intent =
                        new android.content.Intent(
                                StoriesActivity.this,
                                StoryDetailActivity.class
                        );

                intent.putExtra("name", prophet.name);
                intent.putExtra("subtitle", prophet.subtitle);
                intent.putExtra("description", prophet.description);
                intent.putExtra("audioUrl", prophet.audioUrl);
                intent.putExtra("audioFileName", prophet.audioFileName);

                startActivity(intent);
            }
        });

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                dp(5),
                0,
                dp(5)
        );

        card.setLayoutParams(params);

        return card;
    }

    private void filterProphets(String query) {

        String q = query
                .trim()
                .toLowerCase(Locale.ROOT);

        if (q.isEmpty()) {
            renderProphets(prophets);
            return;
        }

        List<Prophet> filtered =
                new ArrayList<>();

        for (Prophet p : prophets) {

            String all =
                    (p.name + " "
                            + p.subtitle + " "
                            + p.description)
                            .toLowerCase(Locale.ROOT);

            if (all.contains(q)) {
                filtered.add(p);
            }
        }

        renderProphets(filtered);
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
        v.setTextDirection(View.TEXT_DIRECTION_RTL);

        return v;
    }

    private int dp(int value) {
        return (int) (
                value *
                getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private static class Prophet {

        String name;
        String subtitle;
        String description;
        String audioUrl;
        String audioFileName;

        Prophet(
                String name,
                String subtitle,
                String description
        ) {
            this(
                    name,
                    subtitle,
                    description,
                    "",
                    ""
            );
        }

        Prophet(
                String name,
                String subtitle,
                String description,
                String audioUrl,
                String audioFileName
        ) {
            this.name = name;
            this.subtitle = subtitle;
            this.description = description;
            this.audioUrl = audioUrl;
            this.audioFileName = audioFileName;
        }
    }
}
