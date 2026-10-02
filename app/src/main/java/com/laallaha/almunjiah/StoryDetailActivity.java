package com.laallaha.almunjiah;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StoryDetailActivity extends Activity {

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);

    private static final String ADAM_AUDIO_URL =
            "https://media.myhuda.com/download/files/media/4-DROOS_WA_KHOTAB/29-Nabeel_EL-Awady/2-Qassas_Anbiaa/1.mp3";

private String audioUrl;
private String audioFileName;

    private MediaPlayer mediaPlayer;

    private SeekBar seekBar;
    private TextView playButton;
    private TextView currentTime;
    private TextView totalTime;
    private TextView downloadButton;
    private ProgressBar downloadProgress;

    private android.os.Handler handler = new android.os.Handler();

    private boolean prepared = false;
    private boolean userSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);

        buildPage();
    }

    private void buildPage() {

        String name = getIntent().getStringExtra("name");
        String subtitle = getIntent().getStringExtra("subtitle");
        String description = getIntent().getStringExtra("description");

audioUrl = getIntent().getStringExtra("audioUrl");
audioFileName = getIntent().getStringExtra("audioFileName");

if (audioUrl == null || audioUrl.trim().isEmpty()) {
    audioUrl = ADAM_AUDIO_URL;
}

if (audioFileName == null || audioFileName.trim().isEmpty()) {
    audioFileName = "adam_story.mp3";
}

        if (name == null) name = "آدم عليه السلام";
        if (subtitle == null) subtitle = "قصة نبي الله آدم عليه السلام";
        if (description == null) description = "";

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        root.addView(createHeader(name, subtitle),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(185)
                ));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(30));

        content.addView(createPlayerCard());

        TextView sectionTitle = text(
                "قصة " + name.replace(" عليه السلام", ""),
                22,
                EMERALD_DARK,
                true
        );
        sectionTitle.setGravity(Gravity.RIGHT);
        sectionTitle.setPadding(0, dp(24), 0, dp(10));

        content.addView(sectionTitle);

        TextView story = text(
                description,
                17,
                TEXT,
                false
        );
        story.setGravity(Gravity.RIGHT);
        story.setLineSpacing(dp(5), 1.15f);

        content.addView(story);

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

    private LinearLayout createHeader(String name, String subtitle) {

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

        TextView title = text(name, 24, WHITE, true);
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
                new LinearLayout.LayoutParams(dp(45), dp(55))
        );

        header.addView(top);

        TextView sub = text(subtitle, 14, Color.rgb(220, 235, 228), false);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, dp(8), 0, 0);

        header.addView(sub);

        TextView quote = text(
                "﴿ وَكُلًّا نَقُصُّ عَلَيْكَ مِنْ أَنبَاءِ الرُّسُلِ ﴾",
                15,
                WHITE,
                true
        );
        quote.setGravity(Gravity.CENTER);
        quote.setPadding(0, dp(15), 0, 0);

        header.addView(quote);

        return header;
    }

    private LinearLayout createPlayerCard() {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(WHITE);
        bg.setCornerRadius(dp(22));
        bg.setStroke(dp(1), Color.rgb(228, 231, 226));

        card.setBackground(bg);
        card.setElevation(dp(5));

        TextView label = text(
                "الاستماع إلى القصة",
                18,
                EMERALD_DARK,
                true
        );
        label.setGravity(Gravity.RIGHT);

        card.addView(label);

        LinearLayout mainControls = new LinearLayout(this);
        mainControls.setGravity(Gravity.CENTER_VERTICAL);
        mainControls.setPadding(0, dp(16), 0, dp(8));

        TextView rewind = controlButton("↶ 15");
        rewind.setOnClickListener(v -> seekRelative(-15000));

        mainControls.addView(
                rewind,
                new LinearLayout.LayoutParams(dp(75), dp(48))
        );

        playButton = controlButton("▶");
        playButton.setTextSize(22);

        GradientDrawable playBg = new GradientDrawable();
        playBg.setColor(EMERALD);
        playBg.setShape(GradientDrawable.OVAL);
        playButton.setBackground(playBg);
        playButton.setTextColor(WHITE);

        playButton.setOnClickListener(v -> togglePlayback());

        LinearLayout.LayoutParams playParams =
                new LinearLayout.LayoutParams(dp(64), dp(64));
        playParams.setMargins(dp(12), 0, dp(12), 0);

        mainControls.addView(playButton, playParams);

        TextView forward = controlButton("15 ↷");
        forward.setOnClickListener(v -> seekRelative(15000));

        mainControls.addView(
                forward,
                new LinearLayout.LayoutParams(dp(75), dp(48))
        );

        card.addView(mainControls);

        LinearLayout times = new LinearLayout(this);
        times.setGravity(Gravity.CENTER_VERTICAL);

        currentTime = text("00:00", 12, TEXT_SECONDARY, false);
        totalTime = text("00:00", 12, TEXT_SECONDARY, false);

        times.addView(
                currentTime,
                new LinearLayout.LayoutParams(
                        0,
                        dp(25),
                        1f
                )
        );

        totalTime.setGravity(Gravity.LEFT);

        times.addView(
                totalTime,
                new LinearLayout.LayoutParams(
                        0,
                        dp(25),
                        1f
                )
        );

        card.addView(times);

        seekBar = new SeekBar(this);
        seekBar.setMax(1000);
        seekBar.setProgress(0);

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar bar,
                            int progress,
                            boolean fromUser
                    ) {
                        if (fromUser && mediaPlayer != null && prepared) {
                            int duration = mediaPlayer.getDuration();
                            int position =
                                    (int) ((duration * (long) progress) / 1000L);

                            currentTime.setText(formatTime(position));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar bar) {
                        userSeeking = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar bar) {
                        userSeeking = false;

                        if (mediaPlayer != null && prepared) {
                            int duration = mediaPlayer.getDuration();

                            int position =
                                    (int) ((duration * (long) bar.getProgress()) / 1000L);

                            mediaPlayer.seekTo(position);
                        }
                    }
                }
        );

        card.addView(seekBar);

        downloadButton = text(
                "⬇ تنزيل للاستماع أوفلاين",
                14,
                EMERALD_DARK,
                true
        );
        downloadButton.setGravity(Gravity.CENTER);
        downloadButton.setPadding(0, dp(12), 0, dp(12));

        GradientDrawable downloadBg = new GradientDrawable();
        downloadBg.setColor(EMERALD_SOFT);
        downloadBg.setCornerRadius(dp(14));

        downloadButton.setBackground(downloadBg);
        downloadButton.setOnClickListener(v -> downloadAudio());

        card.addView(
                downloadButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(48)
                )
        );

        downloadProgress = new ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
        );

        downloadProgress.setMax(100);
        downloadProgress.setVisibility(View.GONE);

        card.addView(
                downloadProgress,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(5)
                )
        );

        File file = getAudioFile();

        if (file.exists() && file.length() > 0) {
            downloadButton.setText("✓ متاح أوفلاين");
        }

        return card;
    }

    private TextView controlButton(String value) {

        TextView v = text(value, 13, EMERALD_DARK, true);
        v.setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(EMERALD_SOFT);
        bg.setCornerRadius(dp(14));

        v.setBackground(bg);

        return v;
    }

    private void togglePlayback() {

        if (mediaPlayer != null && prepared) {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setText("▶");
            } else {
                mediaPlayer.start();
                playButton.setText("⏸");
                updateProgress();
            }

            return;
        }

        File file = getAudioFile();

        if (file.exists() && file.length() > 0) {
            playFile(file);
        } else {
            playOnline();
        }
    }

    private void playFile(File file) {

        releasePlayer();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(file.getAbsolutePath());

            mediaPlayer.setOnPreparedListener(mp -> {
                prepared = true;

                int duration = mp.getDuration();

                seekBar.setProgress(0);
                totalTime.setText(formatTime(duration));
                currentTime.setText("00:00");

                mp.start();
                playButton.setText("⏸");

                updateProgress();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                playButton.setText("▶");
                seekBar.setProgress(1000);
                currentTime.setText(totalTime.getText());
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(
                        this,
                        "تعذر تشغيل التسجيل",
                        Toast.LENGTH_LONG
                ).show();

                releasePlayer();
                playButton.setText("▶");

                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Throwable e) {
            releasePlayer();
            playButton.setText("▶");

            Toast.makeText(
                    this,
                    "تعذر تشغيل التسجيل",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void playOnline() {

        releasePlayer();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.setOnPreparedListener(mp -> {
                prepared = true;

                int duration = mp.getDuration();

                totalTime.setText(formatTime(duration));
                currentTime.setText("00:00");
                seekBar.setProgress(0);

                mp.start();
                playButton.setText("⏸");

                updateProgress();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                playButton.setText("▶");
                seekBar.setProgress(1000);
                currentTime.setText(totalTime.getText());
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(
                        this,
                        "تعذر تشغيل التسجيل من الإنترنت",
                        Toast.LENGTH_LONG
                ).show();

                releasePlayer();
                playButton.setText("▶");

                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Throwable e) {
            releasePlayer();
            playButton.setText("▶");

            Toast.makeText(
                    this,
                    "تعذر تشغيل التسجيل",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void updateProgress() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mediaPlayer != null &&
                        prepared &&
                        mediaPlayer.isPlaying()) {

                    int duration = mediaPlayer.getDuration();
                    int position = mediaPlayer.getCurrentPosition();

                    if (duration > 0 && !userSeeking) {

                        int progress =
                                (int) ((position * 1000L) / duration);

                        seekBar.setProgress(progress);
                        currentTime.setText(formatTime(position));
                    }

                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    private void seekRelative(int milliseconds) {

        if (mediaPlayer == null || !prepared) {
            return;
        }

        int duration = mediaPlayer.getDuration();
        int position = mediaPlayer.getCurrentPosition();

        int newPosition = position + milliseconds;

        if (newPosition < 0) {
            newPosition = 0;
        }

        if (newPosition > duration) {
            newPosition = duration;
        }

        mediaPlayer.seekTo(newPosition);

        if (duration > 0) {
            seekBar.setProgress(
                    (int) ((newPosition * 1000L) / duration)
            );
        }

        currentTime.setText(formatTime(newPosition));
    }

    private String formatTime(int milliseconds) {

        int totalSeconds = milliseconds / 1000;

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format(
                java.util.Locale.US,
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    private void downloadAudio() {

        File target = getAudioFile();

        if (target.exists() && target.length() > 0) {

            Toast.makeText(
                    this,
                    "القصة متاحة بالفعل للاستماع أوفلاين ✓",
                    Toast.LENGTH_SHORT
            ).show();

            downloadButton.setText("✓ متاح أوفلاين");
            return;
        }

        downloadProgress.setProgress(0);
        downloadProgress.setVisibility(View.VISIBLE);
        downloadButton.setText("⏳ جاري التنزيل...");

        new Thread(() -> {

            HttpURLConnection connection = null;
            File temp = new File(target.getParentFile(), audioFileName + ".tmp");

            try {

                URL url = new URL(audioUrl);

                connection =
                        (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);

                connection.connect();

                int code = connection.getResponseCode();

                if (code < 200 || code >= 300) {
                    throw new Exception("HTTP " + code);
                }

                int total = connection.getContentLength();

                try (
                        InputStream input =
                                connection.getInputStream();

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
                                    downloadProgress.setProgress(percent)
                            );
                        }
                    }

                    output.flush();
                }

                if (!temp.exists() || temp.length() == 0) {
                    throw new Exception("Empty file");
                }

                if (target.exists()) {
                    target.delete();
                }

                if (!temp.renameTo(target)) {
                    throw new Exception("Save failed");
                }

                runOnUiThread(() -> {

                    downloadProgress.setProgress(100);
                    downloadProgress.setVisibility(View.GONE);
                    downloadButton.setText("✓ متاح أوفلاين");

                    Toast.makeText(
                            StoryDetailActivity.this,
                            "تم تنزيل القصة بنجاح ✓",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } catch (Throwable e) {

                if (temp.exists()) {
                    temp.delete();
                }

                runOnUiThread(() -> {

                    downloadProgress.setVisibility(View.GONE);
                    downloadButton.setText(
                            "⬇ تنزيل للاستماع أوفلاين"
                    );

                    Toast.makeText(
                            StoryDetailActivity.this,
                            "فشل تنزيل التسجيل",
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

    private File getAudioFile() {
    String safeName = audioFileName;

    if (safeName == null || safeName.trim().isEmpty()) {
        safeName = "adam_story.mp3";
    }

    safeName = safeName.replaceAll("[^a-zA-Z0-9._-]", "_");

    if (!safeName.toLowerCase().endsWith(".mp3")) {
        safeName += ".mp3";
    }

    File dir = getExternalFilesDir(
            Environment.DIRECTORY_MUSIC
    );

    if (dir == null) {
        dir = getFilesDir();
    }

    if (!dir.exists()) {
        dir.mkdirs();
    }

    return new File(dir, safeName);
}

private void releasePlayer() {

        prepared = false;

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
        return (int) (
                value *
                getResources().getDisplayMetrics().density +
                0.5f
        );
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer != null &&
                prepared &&
                mediaPlayer.isPlaying()) {

            mediaPlayer.pause();
            playButton.setText("▶");
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
        releasePlayer();

        super.onDestroy();
    }
}
