package com.laallaha.almunjiah;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.Calendar;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;import android.widget.FrameLayout;import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import androidx.core.content.FileProvider;
import android.net.Uri;
import android.os.Environment;

public class MainActivity extends Activity {
    private TextView prayerNameView;
    private TextView prayerTimeView;
    private TextView prayerRemainView;

    private static final int PRAYER_LOCATION_REQUEST = 7001;

    private LocationManager prayerLocationManager;

    private final LocationListener prayerLocationListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    PrayerLocation.save(
                            MainActivity.this,
                            location.getLatitude(),
                            location.getLongitude()
                    );

                    updatePrayerInfo();
                    updatePrayerTimesCard();

                    stopPrayerLocationUpdates();
                }
            };


    
    private TextView[] prayerTimeViews;
    private LinearLayout[] prayerCardViews;
private final android.os.Handler prayerHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());

    private final Runnable prayerUpdater = new Runnable() {
        @Override
        public void run() {
            updatePrayerInfo();
            
        updatePrayerTimesCard();
prayerHandler.postDelayed(this, 1000);
        }
    };



    // =========================
    // FORMIX-style design system
    // =========================

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private static final String UPDATE_URL =
            "https://raw.githubusercontent.com/hexdrop/LaalahaAlMunjiah/main/update.json";

    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int GOLD_SOFT = Color.rgb(248, 241, 220);

    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;

    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private static final int BORDER = Color.rgb(228, 231, 226);

    private LinearLayout content;
    private long updateDownloadId = -1;

    private TextView continueSurahView;
    private TextView continueVerseView;
    private TextView continueButtonView;

    private LinearLayout homeNav;
    private LinearLayout quranNav;
    private LinearLayout adhkarNav;
    private LinearLayout prayerNav;
    private LinearLayout moreNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(EMERALD_DARK);
        getWindow().setNavigationBarColor(CREAM);
        buildHome();
        checkForUpdate();
        registerReceiver(
                updateDownloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
        );

        buildHome();
        checkForUpdate();
        registerReceiver(updateDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        checkForUpdate();
        registerReceiver(updateDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        PrayerAlarmScheduler.scheduleNextPrayers(this);



        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        8001
                );
            }
        }

    }

    // ============================================================
    // HOME
    // ============================================================

    @Override
    protected void onResume() {
        super.onResume();

        requestPrayerLocation();

        updateContinueReading();

        // تحديث إضافي بعد الرجوع من قارئ القرآن
        new android.os.Handler(
                android.os.Looper.getMainLooper()
        ).postDelayed(
                () -> updateContinueReading(),
                200
        );
    }

    private void requestPrayerLocation() {

        if (PrayerLocation.hasLocation(this)) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    &&
                    checkSelfPermission(
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        PRAYER_LOCATION_REQUEST
                );

                return;
            }
        }

        startPrayerLocationUpdates();
    }

    private void startPrayerLocationUpdates() {

        try {

            if (prayerLocationManager == null) {
                prayerLocationManager =
                        (LocationManager) getSystemService(
                                LOCATION_SERVICE
                        );
            }

            boolean gps =
                    prayerLocationManager.isProviderEnabled(
                            LocationManager.GPS_PROVIDER
                    );

            boolean network =
                    prayerLocationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER
                    );

            Location lastLocation = null;

            if (gps) {
                lastLocation =
                        prayerLocationManager.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER
                        );
            }

            if (lastLocation == null && network) {
                lastLocation =
                        prayerLocationManager.getLastKnownLocation(
                                LocationManager.NETWORK_PROVIDER
                        );
            }

            if (lastLocation != null) {

                PrayerLocation.save(
                        this,
                        lastLocation.getLatitude(),
                        lastLocation.getLongitude()
                );

                updatePrayerInfo();
                updatePrayerTimesCard();

                return;
            }

            if (gps) {

                prayerLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        10,
                        prayerLocationListener
                );
            }

            if (network) {

                prayerLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000,
                        10,
                        prayerLocationListener
                );
            }

        } catch (SecurityException e) {
            // لا نفعل شيئًا؛ المواقيت ستستخدم الموقع الافتراضي مؤقتًا.
        }
    }

    private void stopPrayerLocationUpdates() {

        if (prayerLocationManager == null) {
            return;
        }

        try {
            prayerLocationManager.removeUpdates(
                    prayerLocationListener
            );
        } catch (SecurityException ignored) {
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == PRAYER_LOCATION_REQUEST) {

            boolean granted = false;

            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }

            if (granted) {
                startPrayerLocationUpdates();
            }
        }
    }

    private void buildHome() {

    LinearLayout root =
            new LinearLayout(this);

    root.setOrientation(
            LinearLayout.VERTICAL
    );

    root.setBackgroundColor(CREAM);

    ScrollView scroll =
            new ScrollView(this);

    scroll.setFillViewport(true);
    scroll.setVerticalScrollBarEnabled(false);
    scroll.setOverScrollMode(
            View.OVER_SCROLL_NEVER
    );

    content =
            new LinearLayout(this);

    content.setOrientation(
            LinearLayout.VERTICAL
    );

    content.setPadding(
            dp(18),
            dp(10),
            dp(18),
            dp(28)
    );

    // ========================================================
    // MODERN HOME
    // ========================================================

    buildModernHero();
    buildModernPrayerStrip();
    buildModernQuickGrid();
    buildModernContinue();
    buildModernAyah();
    buildModernDiscovery();
    buildModernReminder();
    buildModernSadaqa();

    scroll.addView(content);

    LinearLayout bottom =
            buildModernBottomNavigation();

    root.addView(
            scroll,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
            )
    );

    root.addView(
            bottom,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(70)
            )
    );

    setContentView(root);
}


// ============================================================
// MODERN HERO
// ============================================================

private void buildModernHero() {

    FrameLayout hero =
            new FrameLayout(this);

    hero.setClipToOutline(true);

    GradientDrawable heroBg =
            new GradientDrawable();

    heroBg.setColor(
            EMERALD_DARK
    );

    heroBg.setCornerRadius(
            dp(28)
    );

    hero.setBackground(heroBg);

    ImageView image =
            new ImageView(this);

    image.setImageResource(
            R.drawable.la1
    );

    image.setScaleType(
            ImageView.ScaleType.CENTER_CROP
    );

    image.setAlpha(
            0.42f
    );

    hero.addView(
            image,
            new FrameLayout.LayoutParams(
                    -1,
                    -1
            )
    );

    View overlay =
            new View(this);

    GradientDrawable overlayBg =
            new GradientDrawable(
                    GradientDrawable.Orientation.RIGHT_LEFT,
                    new int[]{
                            Color.argb(245, 7, 48, 37),
                            Color.argb(205, 8, 62, 47),
                            Color.argb(90, 8, 62, 47),
                            Color.argb(20, 8, 62, 47)
                    }
            );

    overlay.setBackground(
            overlayBg
    );

    hero.addView(
            overlay,
            new FrameLayout.LayoutParams(
                    -1,
                    -1
            )
    );

    LinearLayout box =
            new LinearLayout(this);

    box.setOrientation(
            LinearLayout.VERTICAL
    );

    box.setGravity(
            Gravity.RIGHT | Gravity.CENTER_VERTICAL
    );

    box.setPadding(
            dp(22),
            dp(20),
            dp(22),
            dp(20)
    );

    FrameLayout.LayoutParams boxParams =
            new FrameLayout.LayoutParams(
                    -1,
                    -1
            );

    boxParams.gravity =
            Gravity.CENTER;

    hero.addView(
            box,
            boxParams
    );

    TextView brand =
            text(
                    "لعلها المنجيه",
                    13,
                    Color.rgb(211, 230, 220),
                    false
            );

    brand.setGravity(
            Gravity.RIGHT
    );

    box.addView(brand);

    prayerNameView =
            text(
                    "—",
                    29,
                    WHITE,
                    true
            );

    prayerNameView.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams nameParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    nameParams.topMargin =
            dp(6);

    box.addView(
            prayerNameView,
            nameParams
    );

    prayerTimeView =
            text(
                    "--:--",
                    20,
                    GOLD,
                    true
            );

    prayerTimeView.setGravity(
            Gravity.RIGHT
    );

    box.addView(
            prayerTimeView
    );

    prayerRemainView =
            text(
                    "متبقي --:--:--",
                    12,
                    WHITE,
                    true
            );

    prayerRemainView.setGravity(
            Gravity.CENTER
    );

    GradientDrawable remainBg =
            new GradientDrawable();

    remainBg.setColor(
            Color.argb(60, 255, 255, 255)
    );

    remainBg.setCornerRadius(
            dp(14)
    );

    prayerRemainView.setBackground(
            remainBg
    );

    prayerRemainView.setPadding(
            dp(13),
            dp(7),
            dp(13),
            dp(7)
    );

    LinearLayout.LayoutParams remainParams =
            new LinearLayout.LayoutParams(
                    -2,
                    -2
            );

    remainParams.gravity =
            Gravity.RIGHT;

    remainParams.topMargin =
            dp(10);

    box.addView(
            prayerRemainView,
            remainParams
    );

    TextView hint =
            text(
                    "الصلاة القادمة • اجعلها موعدًا مع الله",
                    10,
                    Color.rgb(195, 216, 205),
                    false
            );

    hint.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams hintParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    hintParams.topMargin =
            dp(12);

    box.addView(
            hint,
            hintParams
    );

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                    -1,
                    dp(235)
            );

    params.setMargins(
            0,
            0,
            0,
            dp(18)
    );

    content.addView(
            hero,
            params
    );

    prayerHandler.removeCallbacks(
            prayerUpdater
    );

    prayerHandler.post(
            prayerUpdater
    );
}


// ============================================================
// MODERN PRAYER STRIP
// ============================================================

private void buildModernPrayerStrip() {

    LinearLayout section =
            new LinearLayout(this);

    section.setOrientation(
            LinearLayout.VERTICAL
    );

    TextView title =
            text(
                    "مواقيت اليوم",
                    18,
                    TEXT,
                    true
            );

    title.setGravity(
            Gravity.RIGHT
    );

    section.addView(title);

    TextView subtitle =
            text(
                    "صلواتك الخمس في نظرة واحدة",
                    10,
                    TEXT_SECONDARY,
                    false
            );

    subtitle.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    subParams.topMargin =
            dp(3);

    subParams.bottomMargin =
            dp(10);

    section.addView(
            subtitle,
            subParams
    );

    HorizontalScrollView scroll =
            new HorizontalScrollView(this);

    scroll.setHorizontalScrollBarEnabled(false);
    scroll.setOverScrollMode(
            View.OVER_SCROLL_NEVER
    );

    LinearLayout row =
            new LinearLayout(this);

    row.setOrientation(
            LinearLayout.HORIZONTAL
    );

    String[] names = {
            "الفجر",
            "الظهر",
            "العصر",
            "المغرب",
            "العشاء"
    };

    prayerTimeViews =
            new TextView[5];

    prayerCardViews =
            new LinearLayout[5];

    for (int i = 0; i < 5; i++) {

        LinearLayout item =
                new LinearLayout(this);

        item.setOrientation(
                LinearLayout.VERTICAL
        );

        item.setGravity(
                Gravity.CENTER
        );

        item.setPadding(
                dp(10),
                dp(9),
                dp(10),
                dp(9)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(
                WHITE
        );

        bg.setCornerRadius(
                dp(17)
        );

        bg.setStroke(
                dp(1),
                BORDER
        );

        item.setBackground(bg);

        TextView name =
                text(
                        names[i],
                        11,
                        TEXT_SECONDARY,
                        true
                );

        name.setGravity(
                Gravity.CENTER
        );

        item.addView(name);

        TextView time =
                text(
                        "--:--",
                        15,
                        EMERALD_DARK,
                        true
                );

        time.setGravity(
                Gravity.CENTER
        );

        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        timeParams.topMargin =
                dp(5);

        item.addView(
                time,
                timeParams
        );

        prayerTimeViews[i] =
                time;

        prayerCardViews[i] =
                item;

        LinearLayout.LayoutParams itemParams =
                new LinearLayout.LayoutParams(
                        dp(88),
                        dp(72)
                );

        itemParams.setMargins(
                dp(3),
                0,
                dp(5),
                0
        );

        row.addView(
                item,
                itemParams
        );
    }

    scroll.addView(row);

    section.addView(
            scroll,
            new LinearLayout.LayoutParams(
                    -1,
                    dp(78)
            )
    );

    LinearLayout.LayoutParams sectionParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    sectionParams.setMargins(
            0,
            0,
            0,
            dp(22)
    );

    content.addView(
            section,
            sectionParams
    );

    updatePrayerTimesCard();
}


// ============================================================
// MODERN QUICK GRID
// ============================================================

private void buildModernQuickGrid() {

    LinearLayout section = new LinearLayout(this);
    section.setOrientation(LinearLayout.VERTICAL);

    TextView title = text(
            "الوصول السريع",
            18,
            TEXT,
            true
    );
    title.setGravity(Gravity.RIGHT);
    section.addView(title);

    TextView subtitle = text(
            "أهم أبواب الخير بين يديك",
            10,
            TEXT_SECONDARY,
            false
    );
    subtitle.setGravity(Gravity.RIGHT);

    LinearLayout.LayoutParams subtitleParams =
            new LinearLayout.LayoutParams(-1, -2);
    subtitleParams.topMargin = dp(3);
    subtitleParams.bottomMargin = dp(11);
    section.addView(subtitle, subtitleParams);

    // القرآن + قصص الأنبياء
    LinearLayout row1 = new LinearLayout(this);
    row1.setOrientation(LinearLayout.HORIZONTAL);

    row1.addView(
            modernAction(
                    R.drawable.ic_quran,
                    "القرآن الكريم",
                    "وردك اليومي",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    QuranActivity.class
                            )
                    )
            ),
            modernWeight()
    );

    row1.addView(
            modernAction(
                    R.drawable.ic_stories,
                    "قصص الأنبياء",
                    "عبر وهداية",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    StoriesActivity.class
                            )
                    )
            ),
            modernWeight()
    );

    section.addView(
            row1,
            new LinearLayout.LayoutParams(-1, dp(105))
    );

    // الأذكار + القبلة
    LinearLayout row2 = new LinearLayout(this);
    row2.setOrientation(LinearLayout.HORIZONTAL);

    row2.addView(
            modernAction(
                    R.drawable.ic_adhkar,
                    "الأذكار",
                    "حصنك اليومي",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    AdhkarActivity.class
                            )
                    )
            ),
            modernWeight()
    );

    row2.addView(
            modernAction(
                    R.drawable.ic_qibla,
                    "القبلة",
                    "اعرف اتجاهك",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    QiblaActivity.class
                            )
                    )
            ),
            modernWeight()
    );

    LinearLayout.LayoutParams row2Params =
            new LinearLayout.LayoutParams(-1, dp(105));
    row2Params.topMargin = dp(7);
    section.addView(row2, row2Params);

    // السبحة
    LinearLayout row3 = new LinearLayout(this);
    row3.setOrientation(LinearLayout.HORIZONTAL);

    row3.addView(
            modernAction(
                    R.drawable.ic_tasbeeh,
                    "السبحة",
                    "اذكر الله",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    TasbeehActivity.class
                            )
                    )
            ),
            modernWeight()
    );

    LinearLayout.LayoutParams row3Params =
            new LinearLayout.LayoutParams(-1, dp(105));
    row3Params.topMargin = dp(7);
    section.addView(row3, row3Params);

        // الصوتيات
        section.addView(
                modernAudioAction(
                        R.drawable.ic_audio,
                        "الصوتيات",
                        "قصص القرآن والسيرة والخلفاء والمزيد",
                        () -> startActivity(
                                new android.content.Intent(
                                        MainActivity.this,
                                        AudioLibraryActivity.class
                                )
                        )
                ),
                new LinearLayout.LayoutParams(-1, dp(82))
        );

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);
    params.setMargins(0, 0, 0, dp(22));

    content.addView(section, params);
}

private LinearLayout modernAudioAction(
        int iconRes,
        String titleText,
        String subtitleText,
        final Runnable action
) {
    LinearLayout card = new LinearLayout(this);
    card.setOrientation(LinearLayout.HORIZONTAL);
    card.setGravity(Gravity.CENTER_VERTICAL);
    card.setPadding(dp(15), dp(9), dp(15), dp(9));

    GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{
                    Color.rgb(38, 104, 86),
                    Color.rgb(18, 79, 65),
                    Color.rgb(25, 91, 74)
            }
    );

    bg.setCornerRadius(dp(19));
    bg.setStroke(dp(1), Color.argb(135, 218, 184, 105));

    card.setBackground(bg);
    card.setElevation(dp(7));

    ImageView icon = new ImageView(this);
    icon.setImageResource(iconRes);
    icon.setColorFilter(
            Color.rgb(232, 198, 118),
            android.graphics.PorterDuff.Mode.SRC_IN
    );
    icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

    GradientDrawable iconBg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{
                    Color.argb(185, 248, 241, 220),
                    Color.argb(120, 231, 244, 238)
            }
    );

    iconBg.setShape(GradientDrawable.OVAL);
    iconBg.setStroke(dp(1), Color.argb(210, 190, 155, 75));
    icon.setBackground(iconBg);

    card.addView(
            icon,
            new LinearLayout.LayoutParams(dp(52), dp(52))
    );

    LinearLayout texts = new LinearLayout(this);
    texts.setOrientation(LinearLayout.VERTICAL);
    texts.setGravity(Gravity.RIGHT);

    TextView title = text(titleText, 16, WHITE, true);
    title.setGravity(Gravity.RIGHT);
    texts.addView(title);

    TextView sub = text(
            subtitleText,
            10,
            Color.rgb(195, 216, 205),
            false
    );
    sub.setGravity(Gravity.RIGHT);

    LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(-1, -2);
    subParams.topMargin = dp(3);
    texts.addView(sub, subParams);

    LinearLayout.LayoutParams textParams =
            new LinearLayout.LayoutParams(0, -2, 1f);
    textParams.setMargins(dp(12), 0, dp(8), 0);
    card.addView(texts, textParams);

    TextView arrow = text("‹", 30, GOLD, true);
    arrow.setGravity(Gravity.CENTER);

    card.addView(
            arrow,
            new LinearLayout.LayoutParams(dp(30), dp(55))
    );

    card.setOnClickListener(v -> action.run());

    return card;
}

        private LinearLayout modernAction(
        int iconRes,
        String titleText,
        String subtitleText,
        final Runnable action
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
            dp(11),
            dp(13),
            dp(11)
    );

    GradientDrawable bg =
            new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{
                            Color.rgb(38, 104, 86),
                            Color.rgb(18, 79, 65),
                            Color.rgb(25, 91, 74)
                    }
            );

    bg.setCornerRadius(
            dp(19)
    );

    bg.setStroke(
            dp(1),
            Color.argb(135, 218, 184, 105)
    );

    card.setBackground(bg);
    card.setElevation(dp(7));

    ImageView icon =
            new ImageView(this);

    icon.setImageResource(
            iconRes
    );

    icon.setColorFilter(
            Color.rgb(232, 198, 118),
            android.graphics.PorterDuff.Mode.SRC_IN
    );

    icon.setScaleType(
            ImageView.ScaleType.CENTER_INSIDE
    );

    GradientDrawable iconBg =
            new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{
                            Color.argb(185, 248, 241, 220),
                            Color.argb(120, 231, 244, 238)
                    }
            );

    iconBg.setShape(
            GradientDrawable.OVAL
    );

    iconBg.setStroke(
            dp(1),
            Color.argb(210, 190, 155, 75)
    );

    icon.setBackground(
            iconBg
    );

    card.addView(
            icon,
            new LinearLayout.LayoutParams(
                    dp(45),
                    dp(45)
            )
    );

    LinearLayout texts =
            new LinearLayout(this);

    texts.setOrientation(
            LinearLayout.VERTICAL
    );

    texts.setGravity(
            Gravity.RIGHT
    );

    TextView title =
            text(
                    titleText,
                    13,
                    WHITE,
                    true
            );

    title.setGravity(
            Gravity.RIGHT
    );

    texts.addView(title);

    TextView sub =
            text(
                    subtitleText,
                    10,
                    Color.rgb(195, 216, 205),
                    false
            );

    sub.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    subParams.topMargin =
            dp(3);

    texts.addView(
            sub,
            subParams
    );

    LinearLayout.LayoutParams textParams =
            new LinearLayout.LayoutParams(
                    0,
                    -2,
                    1f
            );

    textParams.setMargins(
            dp(10),
            0,
            0,
            0
    );

    card.addView(
            texts,
            textParams
    );

    card.setOnClickListener(
            v -> action.run()
    );

    return card;
}


private LinearLayout.LayoutParams modernWeight() {

    LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(
                    0,
                    -1,
                    1f
            );

    p.setMargins(
            dp(2),
            0,
            dp(2),
            0
    );

    return p;
}


// ============================================================
// MODERN CONTINUE READING
// ============================================================

private void buildModernContinue() {

    LinearLayout card =
            new LinearLayout(this);

    card.setOrientation(
            LinearLayout.VERTICAL
    );

    card.setPadding(
            dp(19),
            dp(18),
            dp(19),
            dp(18)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(
            Color.rgb(244, 248, 245)
    );

    bg.setCornerRadius(
            dp(22)
    );

    card.setBackground(bg);

    TextView small =
            text(
                    "ورد القرآن",
                    10,
                    EMERALD,
                    true
            );

    small.setGravity(
            Gravity.RIGHT
    );

    card.addView(small);

    continueSurahView =
            text(
                    "ابدأ قراءة القرآن",
                    19,
                    TEXT,
                    true
            );

    continueSurahView.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams surahParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    surahParams.topMargin =
            dp(5);

    card.addView(
            continueSurahView,
            surahParams
    );

    continueVerseView =
            text(
                    "اجعل للقرآن نصيبًا من يومك",
                    11,
                    TEXT_SECONDARY,
                    false
            );

    continueVerseView.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams verseParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    verseParams.topMargin =
            dp(4);

    card.addView(
            continueVerseView,
            verseParams
    );

    View line =
            new View(this);

    GradientDrawable lineBg =
            new GradientDrawable();

    lineBg.setColor(
            EMERALD
    );

    lineBg.setCornerRadius(
            dp(4)
    );

    line.setBackground(lineBg);

    LinearLayout.LayoutParams lineParams =
            new LinearLayout.LayoutParams(
                    dp(72),
                    dp(4)
            );

    lineParams.gravity =
            Gravity.RIGHT;

    lineParams.topMargin =
            dp(14);

    card.addView(
            line,
            lineParams
    );

    continueButtonView =
            text(
                    "متابعة القراءة  ←",
                    12,
                    WHITE,
                    true
            );

    continueButtonView.setGravity(
            Gravity.CENTER
    );

    GradientDrawable buttonBg =
            new GradientDrawable();

    buttonBg.setColor(
            EMERALD_DARK
    );

    buttonBg.setCornerRadius(
            dp(14)
    );

    continueButtonView.setBackground(
            buttonBg
    );

    LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(
                    -1,
                    dp(44)
            );

    buttonParams.topMargin =
            dp(14);

    card.addView(
            continueButtonView,
            buttonParams
    );

    continueButtonView.setOnClickListener(
            v -> {

                android.content.SharedPreferences prefs =
                        getSharedPreferences(
                                "quran_reading",
                                MODE_PRIVATE
                        );

                int surahIndex =
                        prefs.getInt(
                                "surah_index",
                                -1
                        );

                int ayahIndex =
                        prefs.getInt(
                                "ayah_index",
                                -1
                        );

                android.content.Intent intent =
                        new android.content.Intent(
                                MainActivity.this,
                                QuranActivity.class
                        );

                if (surahIndex >= 0) {
                    intent.putExtra(
                            "open_surah_index",
                            surahIndex
                    );
                }

                if (ayahIndex >= 0) {
                    intent.putExtra(
                            "open_ayah_index",
                            ayahIndex
                    );
                }

                startActivity(intent);
            }
    );

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    params.setMargins(
            0,
            0,
            0,
            dp(18)
    );

    content.addView(
            card,
            params
    );

    updateContinueReading();
}


// ============================================================
// MODERN AYAH
// ============================================================

private String[] getDailyAyah() {
    try {
        InputStream input = getAssets().open("quran.json");
        byte[] data = new byte[input.available()];
        input.read(data);
        input.close();

        String json = new String(data, StandardCharsets.UTF_8);
        JSONArray surahs = new JSONArray(json);

        int totalVerses = 0;

        for (int i = 0; i < surahs.length(); i++) {
            JSONObject surah = surahs.getJSONObject(i);
            totalVerses += surah.getInt("total_verses");
        }

        if (totalVerses == 0) {
            return new String[]{
                "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
                "سورة الشرح • 6"
            };
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        long daySequence =
                calendar.get(Calendar.YEAR) * 366L
                + dayOfYear - 1;

        int target = (int) (daySequence % totalVerses);

        for (int i = 0; i < surahs.length(); i++) {
            JSONObject surah = surahs.getJSONObject(i);
            int count = surah.getInt("total_verses");

            if (target < count) {
                JSONArray verses = surah.getJSONArray("verses");
                JSONObject verse = verses.getJSONObject(target);

                String ayahText = verse.getString("text");
                int ayahNumber = verse.getInt("id");
                String surahName = surah.getString("name");

                return new String[]{
                    ayahText,
                    "سورة " + surahName + " • " + ayahNumber
                };
            }

            target -= count;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return new String[]{
        "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
        "سورة الشرح • 6"
    };
}

private void buildModernAyah() {

    String[] dailyAyah = getDailyAyah();

    LinearLayout card =
            new LinearLayout(this);

    card.setOrientation(
            LinearLayout.VERTICAL
    );

    card.setGravity(
            Gravity.CENTER
    );

    card.setPadding(
            dp(22),
            dp(24),
            dp(22),
            dp(23)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(
            EMERALD_DARK
    );

    bg.setCornerRadius(
            dp(24)
    );

    card.setBackground(bg);

    TextView label =
            text(
                    "✦  آية اليوم  ✦",
                    11,
                    GOLD,
                    true
            );

    label.setGravity(
            Gravity.CENTER
    );

    card.addView(label);

    TextView ayah =
            text(
                    dailyAyah[0],
                    24,
                    WHITE,
                    true
            );

    ayah.setGravity(
            Gravity.CENTER
    );

    ayah.setLineSpacing(
            dp(4),
            1.15f
    );

    LinearLayout.LayoutParams ayahParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    ayahParams.topMargin =
            dp(17);

    card.addView(
            ayah,
            ayahParams
    );

    TextView second =
            text(
                    "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
                    16,
                    Color.rgb(211, 230, 220),
                    false
            );

    second.setGravity(
            Gravity.CENTER
    );

    second.setVisibility(View.GONE);

    LinearLayout.LayoutParams secondParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    secondParams.topMargin =
            dp(9);

    card.addView(
            second,
            secondParams
    );

    TextView source =
            text(
                    dailyAyah[1],
                    10,
                    GOLD,
                    true
            );

    source.setGravity(
            Gravity.CENTER
    );

    LinearLayout.LayoutParams sourceParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    sourceParams.topMargin =
            dp(17);

    card.addView(
            source,
            sourceParams
    );

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    params.setMargins(
            0,
            0,
            0,
            dp(20)
    );

    content.addView(
            card,
            params
    );
}


// ============================================================
// MODERN DISCOVERY
// ============================================================

private void buildModernDiscovery() {

    LinearLayout card = new LinearLayout(this);
    card.setOrientation(LinearLayout.HORIZONTAL);
    card.setGravity(Gravity.CENTER_VERTICAL);

    card.setPadding(
            dp(18),
            dp(14),
            dp(18),
            dp(14)
    );

    GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{
                    Color.rgb(38, 104, 86),
                    Color.rgb(18, 79, 65),
                    Color.rgb(25, 91, 74)
            }
    );

    bg.setCornerRadius(dp(20));
    bg.setStroke(dp(1), BORDER);
    card.setBackground(bg);

    ImageView icon = new ImageView(this);

    int iconId = getResources().getIdentifier(
            "ic_quran",
            "drawable",
            getPackageName()
    );

    if (iconId != 0) {
        icon.setImageResource(iconId);
    }

    LinearLayout.LayoutParams iconParams =
            new LinearLayout.LayoutParams(
                    dp(52),
                    dp(52)
            );

    iconParams.setMargins(
            0,
            0,
            dp(14),
            0
    );

    card.addView(icon, iconParams);

    LinearLayout textBox = new LinearLayout(this);
    textBox.setOrientation(LinearLayout.VERTICAL);
    textBox.setGravity(Gravity.CENTER_VERTICAL);

    TextView title = text(
            "اختبار تحفيظ القرآن",
            17,
            WHITE,
            true
    );

    title.setGravity(Gravity.RIGHT);

    TextView subtitle = text(
            "استرجع الآية من حفظك",
            11,
            Color.rgb(195, 216, 205),
            false
    );

    subtitle.setGravity(Gravity.RIGHT);

    textBox.addView(title);

    LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    subParams.topMargin = dp(4);

    textBox.addView(
            subtitle,
            subParams
    );

    LinearLayout.LayoutParams textParams =
            new LinearLayout.LayoutParams(
                    0,
                    -2,
                    1f
            );

    card.addView(
            textBox,
            textParams
    );

    TextView arrow = text(
            "‹",
            30,
            GOLD,
            true
    );

    arrow.setGravity(Gravity.CENTER);

    card.addView(
            arrow,
            new LinearLayout.LayoutParams(
                    dp(32),
                    dp(52)
            )
    );

    card.setOnClickListener(v -> {
        startActivity(
                new android.content.Intent(
                        MainActivity.this,
                        IslamicQuestionsActivity.class
                )
        );
    });

    LinearLayout.LayoutParams cardParams =
            new LinearLayout.LayoutParams(
                    -1,
                    dp(82)
            );

    cardParams.setMargins(
            0,
            0,
            0,
            dp(20)
    );

    content.addView(
            card,
            cardParams
    );
}

// ============================================================
// MODERN REMINDER
// ============================================================

private void buildModernReminder() {

    LinearLayout card =
            new LinearLayout(this);

    card.setOrientation(
            LinearLayout.HORIZONTAL
    );

    card.setGravity(
            Gravity.CENTER_VERTICAL
    );

    card.setPadding(
            dp(15),
            dp(14),
            dp(15),
            dp(14)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(
            WHITE
    );

    bg.setCornerRadius(
            dp(19)
    );

    bg.setStroke(
            dp(1),
            BORDER
    );

    card.setBackground(bg);

    TextView icon =
            text(
                    "✦",
                    19,
                    GOLD,
                    true
            );

    icon.setGravity(
            Gravity.CENTER
    );

    GradientDrawable iconBg =
            new GradientDrawable();

    iconBg.setColor(
            GOLD_SOFT
    );

    iconBg.setShape(
            GradientDrawable.OVAL
    );

    icon.setBackground(
            iconBg
    );

    card.addView(
            icon,
            new LinearLayout.LayoutParams(
                    dp(45),
                    dp(45)
            )
    );

    LinearLayout texts =
            new LinearLayout(this);

    texts.setOrientation(
            LinearLayout.VERTICAL
    );

    texts.setGravity(
            Gravity.RIGHT
    );

    TextView title =
            text(
                    "وردك اليومي",
                    14,
                    TEXT,
                    true
            );

    title.setGravity(
            Gravity.RIGHT
    );

    texts.addView(title);

    TextView sub =
            text(
                    "اجعل لسانك عامرًا بذكر الله",
                    10,
                    TEXT_SECONDARY,
                    false
            );

    sub.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams subParams =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    subParams.topMargin =
            dp(3);

    texts.addView(
            sub,
            subParams
    );

    LinearLayout.LayoutParams textParams =
            new LinearLayout.LayoutParams(
                    0,
                    -2,
                    1f
            );

    textParams.setMargins(
            dp(11),
            0,
            dp(7),
            0
    );

    card.addView(
            texts,
            textParams
    );

    TextView action =
            text(
                    "اذكر الله",
                    10,
                    EMERALD_DARK,
                    true
            );

    action.setGravity(
            Gravity.CENTER
    );

    GradientDrawable actionBg =
            new GradientDrawable();

    actionBg.setColor(
            EMERALD_SOFT
    );

    actionBg.setCornerRadius(
            dp(12)
    );

    action.setBackground(
            actionBg
    );

    action.setOnClickListener(
            v -> startActivity(
                    new android.content.Intent(
                            MainActivity.this,
                            AdhkarActivity.class
                    )
            )
    );

    card.addView(
            action,
            new LinearLayout.LayoutParams(
                    dp(72),
                    dp(38)
            )
    );

    card.setOnClickListener(
            v -> startActivity(
                    new android.content.Intent(
                            MainActivity.this,
                            AdhkarActivity.class
                    )
            )
    );

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                    -1,
                    -2
            );

    params.setMargins(
            0,
            0,
            0,
            dp(18)
    );

    content.addView(
            card,
            params
    );
}


// ============================================================
// MODERN SADAQA
// ============================================================

private void buildModernSadaqa() {

    LinearLayout card = new LinearLayout(this);
    card.setOrientation(LinearLayout.HORIZONTAL);
    card.setGravity(Gravity.CENTER_VERTICAL);
    card.setPadding(dp(13), dp(10), dp(13), dp(10));

    GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{
                    Color.argb(95, 42, 105, 88),
                    Color.argb(35, 8, 45, 42)
            }
    );

    bg.setCornerRadius(dp(17));
    bg.setStroke(
            dp(1),
            Color.argb(70, 218, 184, 105)
    );

    card.setBackground(bg);
    card.setElevation(dp(4));

    ImageView icon = new ImageView(this);
    icon.setImageResource(R.drawable.ic_sadaqa);
    icon.setColorFilter(
            GOLD,
            android.graphics.PorterDuff.Mode.SRC_IN
    );
    icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

    GradientDrawable iconBg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{
                    Color.argb(105, 218, 184, 105),
                    Color.argb(35, 35, 150, 112)
            }
    );

    iconBg.setShape(GradientDrawable.OVAL);
    iconBg.setStroke(
            dp(1),
            Color.argb(80, 218, 184, 105)
    );

    icon.setBackground(iconBg);

    card.addView(
            icon,
            new LinearLayout.LayoutParams(dp(43), dp(43))
    );

    LinearLayout texts = new LinearLayout(this);
    texts.setOrientation(LinearLayout.VERTICAL);
    texts.setGravity(Gravity.RIGHT);

    TextView title = text(
            "صدقة جارية",
            16,
            GOLD,
            true
    );
    title.setGravity(Gravity.RIGHT);

    texts.addView(title);

    TextView body = text(
                "عن والدي رحمه الله • اللهم تقبلها واجعل أجرها ممتدًا",
                11,
                Color.rgb(210, 70, 70),
                true
        );
        LinearLayout.LayoutParams bodyParams =
                new LinearLayout.LayoutParams(-1, -2);
        bodyParams.topMargin = dp(3);

        texts.addView(body, bodyParams);

    LinearLayout.LayoutParams textParams =
            new LinearLayout.LayoutParams(0, -2, 1f);
    textParams.setMargins(dp(11), 0, dp(6), 0);

    card.addView(texts, textParams);

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);

    params.setMargins(
            0,
            0,
            0,
            dp(12)
    );

    content.addView(card, params);
}

// ============================================================
// MODERN NAVIGATION
// ============================================================

private LinearLayout buildModernBottomNavigation() {

    LinearLayout nav =
            new LinearLayout(this);

    nav.setOrientation(
            LinearLayout.HORIZONTAL
    );

    nav.setGravity(
            Gravity.CENTER_VERTICAL
    );

    nav.setPadding(
            dp(10),
            dp(5),
            dp(10),
            dp(6)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(
            WHITE
    );

    bg.setStroke(
            dp(1),
            BORDER
    );

    bg.setCornerRadius(
            dp(21)
    );

    nav.setBackground(bg);

    nav.setElevation(
            dp(5)
    );

    homeNav =
            premiumNavItem(
                    R.drawable.ic_crescent,
                    "الرئيسية",
                    true
            );

    quranNav =
            premiumNavItem(
                    R.drawable.ic_quran,
                    "القرآن",
                    false
            );

    adhkarNav =
            premiumNavItem(
                    R.drawable.ic_adhkar,
                    "الأذكار",
                    false
            );

    prayerNav =
            premiumNavItem(
                    R.drawable.ic_qibla,
                    "الصلاة",
                    false
            );

    moreNav =
            premiumNavTextItem(
                    "•••",
                    "المزيد",
                    false
            );

    quranNav.setOnClickListener(
            v -> startActivity(
                    new android.content.Intent(
                            MainActivity.this,
                            QuranActivity.class
                    )
            )
    );

    adhkarNav.setOnClickListener(
            v -> startActivity(
                    new android.content.Intent(
                            MainActivity.this,
                            AdhkarActivity.class
                    )
            )
    );

        prayerNav.setClickable(true);
        prayerNav.setFocusable(true);

        prayerNav.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                MainActivity.this,
                                PrayerSettingsActivity.class
                        )
                )
        );

        moreNav.setClickable(true);
        moreNav.setFocusable(true);

        moreNav.setOnClickListener(v ->
                startActivity(
                        new android.content.Intent(
                                MainActivity.this,
                                SettingsActivity.class
                        )
                )
        );

    nav.addView(
            homeNav,
            navWeight()
    );

    nav.addView(
            quranNav,
            navWeight()
    );

    nav.addView(
            adhkarNav,
            navWeight()
    );

    nav.addView(
            prayerNav,
            navWeight()
    );

    nav.addView(
            moreNav,
            navWeight()
    );

    return nav;
}

private PrayerTimesCalculator.Times calculatePrayerTimes(
        Calendar date
) {
    double latitude =
            PrayerLocation.getLatitude(this);

    double longitude =
            PrayerLocation.getLongitude(this);

    return PrayerTimesCalculator.calculate(
            date,
            latitude,
            longitude
    );
}

private void updatePrayerInfo() {
    Calendar now = Calendar.getInstance();

    PrayerTimesCalculator.Times today =
            calculatePrayerTimes(now);

    String[] names = {
            "الفجر",
            "الظهر",
            "العصر",
            "المغرب",
            "العشاء"
    };

    double[] values = {
            today.fajr,
            today.dhuhr,
            today.asr,
            today.maghrib,
            today.isha
    };

    long nowMillis = now.getTimeInMillis();

    int nextIndex = -1;
    long nextMillis = Long.MAX_VALUE;
    double nextTime = 0;

    for (int i = 0; i < values.length; i++) {
        long prayerMillis =
                PrayerTimesCalculator.toMillis(
                        values[i],
                        now
                );

        if (prayerMillis > nowMillis
                && prayerMillis < nextMillis) {
            nextMillis = prayerMillis;
            nextIndex = i;
            nextTime = values[i];
        }
    }

    // بعد العشاء: نحسب فجر الغد فعليًا
    if (nextIndex == -1) {
        Calendar tomorrow =
                (Calendar) now.clone();

        tomorrow.add(
                Calendar.DAY_OF_YEAR,
                1
        );

        PrayerTimesCalculator.Times tomorrowTimes =
                calculatePrayerTimes(tomorrow);

        nextIndex = 0;
        nextTime = tomorrowTimes.fajr;

        nextMillis =
                PrayerTimesCalculator.toMillis(
                        nextTime,
                        tomorrow
                );
    }

    prayerNameView.setText(names[nextIndex]);

    prayerTimeView.setText(
            PrayerTimesCalculator.format(nextTime)
    );

    long remaining = nextMillis - nowMillis;

    if (remaining < 0) {
        remaining = 0;
    }

    long totalSeconds = remaining / 1000;
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    prayerRemainView.setText(
            String.format(
                    java.util.Locale.US,
                    "متبقي %02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds
            )
    );
}

private void buildPrayerTimesCard() {

    LinearLayout section = new LinearLayout(this);
    section.setOrientation(LinearLayout.VERTICAL);
    section.setPadding(0, 0, 0, 0);

    LinearLayout header = new LinearLayout(this);
    header.setOrientation(LinearLayout.HORIZONTAL);
    header.setGravity(Gravity.CENTER_VERTICAL);

    LinearLayout titleBox = new LinearLayout(this);
    titleBox.setOrientation(LinearLayout.VERTICAL);
    titleBox.setGravity(Gravity.RIGHT);

    TextView title = text(
            "مواقيت الصلاة",
            19,
            TEXT,
            true
    );
    title.setGravity(Gravity.RIGHT);

    TextView subtitle = text(
            "الصلوات الخمس • اليوم",
            11,
            TEXT_SECONDARY,
            false
    );
    subtitle.setGravity(Gravity.RIGHT);
    subtitle.setPadding(0, dp(3), 0, 0);

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

    TextView mark = text(
            "✦",
            17,
            GOLD,
            true
    );
    mark.setGravity(Gravity.CENTER);

    GradientDrawable markBg = new GradientDrawable();
    markBg.setColor(GOLD_SOFT);
    markBg.setShape(GradientDrawable.OVAL);

    mark.setBackground(markBg);

    header.addView(
            mark,
            new LinearLayout.LayoutParams(dp(38), dp(38))
    );

    LinearLayout.LayoutParams headerParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    headerParams.setMargins(
            dp(3),
            0,
            dp(3),
            dp(11)
    );

    section.addView(header, headerParams);

    HorizontalScrollView horizontal =
            new HorizontalScrollView(this);

    horizontal.setHorizontalScrollBarEnabled(false);
    horizontal.setClipToPadding(false);
    horizontal.setOverScrollMode(View.OVER_SCROLL_NEVER);

    LinearLayout row =
            new LinearLayout(this);

    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);
    row.setPadding(dp(1), 0, dp(1), dp(3));

    String[] names = {
            "الفجر",
            "الظهر",
            "العصر",
            "المغرب",
            "العشاء"
    };

    prayerTimeViews = new TextView[names.length];
    prayerCardViews = new LinearLayout[names.length];

    for (int i = 0; i < names.length; i++) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(
                dp(12),
                dp(11),
                dp(12),
                dp(11)
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(WHITE);
        bg.setCornerRadius(dp(16));
        bg.setStroke(dp(1), Color.rgb(235, 237, 233));

        card.setBackground(bg);
        card.setElevation(dp(1));

        TextView dot = text(
                i == 0 ? "●" : "●",
                8,
                i == 0 ? GOLD : EMERALD,
                true
        );
        dot.setGravity(Gravity.CENTER);

        card.addView(
                dot,
                new LinearLayout.LayoutParams(
                        dp(16),
                        dp(12)
                )
        );

        TextView name = text(
                names[i],
                12,
                TEXT_SECONDARY,
                true
        );
        name.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        nameParams.topMargin = dp(5);
        card.addView(name, nameParams);

        TextView time = text(
                "--:--",
                17,
                EMERALD_DARK,
                true
        );
        time.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        timeParams.topMargin = dp(4);
        card.addView(time, timeParams);

        prayerTimeViews[i] = time;
        prayerCardViews[i] = card;

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        dp(91),
                        dp(91)
                );

        cardParams.setMargins(
                dp(3),
                0,
                dp(3),
                dp(5)
        );

        row.addView(card, cardParams);
    }

    horizontal.addView(row);

    section.addView(
            horizontal,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(101)
            )
    );

    LinearLayout.LayoutParams sectionParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    sectionParams.setMargins(0, 0, 0, dp(18));

    content.addView(section, sectionParams);

    updatePrayerTimesCard();
}

private void updatePrayerTimesCard() {

    if (prayerTimeViews == null
            || prayerCardViews == null) {
        return;
    }

    Calendar now = Calendar.getInstance();

    PrayerTimesCalculator.Times today =
            calculatePrayerTimes(now);

    double[] values = {
            today.fajr,
            today.dhuhr,
            today.asr,
            today.maghrib,
            today.isha
    };

    long nowMillis = now.getTimeInMillis();

    int nextIndex = -1;
    long nextMillis = Long.MAX_VALUE;

    for (int i = 0; i < values.length; i++) {

        long prayerMillis =
                PrayerTimesCalculator.toMillis(
                        values[i],
                        now
                );

        if (prayerMillis > nowMillis
                && prayerMillis < nextMillis) {

            nextMillis = prayerMillis;
            nextIndex = i;
        }
    }

    /*
     * بعد العشاء:
     * الفجر القادم هو فجر الغد،
     * لذلك نستخدم وقت فجر الغد في الكارت.
     */
    if (nextIndex == -1) {

        Calendar tomorrow =
                (Calendar) now.clone();

        tomorrow.add(
                Calendar.DAY_OF_YEAR,
                1
        );

        PrayerTimesCalculator.Times tomorrowTimes =
                calculatePrayerTimes(tomorrow);

        values[0] = tomorrowTimes.fajr;

        nextIndex = 0;
        nextMillis =
                PrayerTimesCalculator.toMillis(
                        values[0],
                        tomorrow
                );
    }

    for (int i = 0; i < values.length; i++) {

        prayerTimeViews[i].setText(
                PrayerTimesCalculator.format(
                        values[i]
                )
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setCornerRadius(dp(20));

        if (i == nextIndex) {

            bg.setColor(EMERALD_SOFT);
            bg.setStroke(dp(2), GOLD);

        } else {

            bg.setColor(WHITE);
            bg.setStroke(dp(1), BORDER);
        }

        prayerCardViews[i].setBackground(bg);
    }
}

private void buildQuickActions() {

    LinearLayout section = new LinearLayout(this);
    section.setOrientation(LinearLayout.VERTICAL);

    // عنوان القسم
    LinearLayout header = new LinearLayout(this);
    header.setOrientation(LinearLayout.VERTICAL);
    header.setGravity(Gravity.RIGHT);

    TextView title = text(
            "الوصول السريع",
            20,
            TEXT,
            true
    );
    title.setGravity(Gravity.RIGHT);

    TextView subtitle = text(
            "أقرب أبواب الخير إليك",
            11,
            TEXT_SECONDARY,
            false
    );
    subtitle.setGravity(Gravity.RIGHT);
    subtitle.setPadding(0, dp(3), 0, 0);

    header.addView(title);
    header.addView(subtitle);

    LinearLayout.LayoutParams headerParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    headerParams.setMargins(
            dp(2),
            0,
            dp(2),
            dp(14)
    );

    section.addView(header, headerParams);

    // القرآن الكريم — العنصر الرئيسي
    LinearLayout quran = new LinearLayout(this);
    quran.setOrientation(LinearLayout.HORIZONTAL);
    quran.setGravity(Gravity.CENTER_VERTICAL);
    quran.setPadding(
            dp(18),
            dp(16),
            dp(18),
            dp(16)
    );

    GradientDrawable quranBg =
            new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{
                            EMERALD_DARK,
                            EMERALD
                    }
            );

    quranBg.setCornerRadius(dp(22));
    quran.setBackground(quranBg);
    quran.setElevation(dp(2));

    ImageView quranIcon = new ImageView(this);
    quranIcon.setImageResource(R.drawable.ic_quran);
    quranIcon.setColorFilter(
            Color.WHITE,
            android.graphics.PorterDuff.Mode.SRC_IN
    );

    GradientDrawable iconBg = new GradientDrawable();
    iconBg.setColor(Color.argb(42, 255, 255, 255));
    iconBg.setShape(GradientDrawable.OVAL);
    quranIcon.setBackground(iconBg);
    quranIcon.setPadding(
            dp(12),
            dp(12),
            dp(12),
            dp(12)
    );

    quran.addView(
            quranIcon,
            new LinearLayout.LayoutParams(
                    dp(54),
                    dp(54)
            )
    );

    LinearLayout quranText =
            new LinearLayout(this);

    quranText.setOrientation(LinearLayout.VERTICAL);
    quranText.setGravity(Gravity.RIGHT);

    TextView quranTitle = text(
            "القرآن الكريم",
            18,
            WHITE,
            true
    );
    quranTitle.setGravity(Gravity.RIGHT);

    TextView quranSub = text(
            "وردك اليومي بين يديك",
            11,
            Color.rgb(218, 236, 228),
            false
    );
    quranSub.setGravity(Gravity.RIGHT);
    quranSub.setPadding(0, dp(4), 0, dp(8));

    TextView quranAction = text(
            "ابدأ القراءة",
            11,
            GOLD,
            true
    );
    quranAction.setGravity(Gravity.RIGHT);

    quranText.addView(quranTitle);
    quranText.addView(quranSub);
    quranText.addView(quranAction);

    LinearLayout.LayoutParams quranTextParams =
            new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );

    quranTextParams.setMargins(
            dp(14),
            0,
            dp(10),
            0
    );

    quran.addView(quranText, quranTextParams);

    TextView arrow = text(
            "‹",
            28,
            WHITE,
            false
    );
    arrow.setGravity(Gravity.CENTER);

    quran.addView(
            arrow,
            new LinearLayout.LayoutParams(
                    dp(28),
                    dp(54)
            )
    );

    quran.setOnClickListener(v -> {
        startActivity(
                new android.content.Intent(
                        MainActivity.this,
                        QuranActivity.class
                )
        );
    });

    LinearLayout.LayoutParams quranParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(92)
            );

    quranParams.setMargins(0, 0, 0, dp(14));

    section.addView(quran, quranParams);

    // الخدمات الأساسية
    LinearLayout tools =
            new LinearLayout(this);

    tools.setOrientation(LinearLayout.HORIZONTAL);
    tools.setGravity(Gravity.CENTER_VERTICAL);

    tools.addView(
            addPremiumMiniAction(
                    R.drawable.ic_adhkar,
                    "الأذكار",
                    "حصنك اليومي",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    AdhkarActivity.class
                            )
                    )
            ),
            miniWeight()
    );

    tools.addView(
            addPremiumMiniAction(
                    R.drawable.ic_qibla,
                    "القبلة",
                    "اتجاه القبلة",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    QiblaActivity.class
                            )
                    )
            ),
            miniWeight()
    );

    tools.addView(
            addPremiumMiniAction(
                    R.drawable.ic_tasbeeh,
                    "السبحة",
                    "سبّح واذكر",
                    () -> startActivity(
                            new android.content.Intent(
                                    MainActivity.this,
                                    TasbeehActivity.class
                            )
                    )
            ),
            miniWeight()
    );

    LinearLayout.LayoutParams toolsParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(112)
            );

    toolsParams.setMargins(0, 0, 0, dp(14));

    section.addView(tools, toolsParams);

// سؤال ديني
    LinearLayout religiousQuestion =
            new LinearLayout(this);

    religiousQuestion.setOrientation(
            LinearLayout.HORIZONTAL
    );

    religiousQuestion.setGravity(
            Gravity.CENTER_VERTICAL
    );

    religiousQuestion.setPadding(
            dp(18),
            dp(14),
            dp(18),
            dp(14)
    );

    GradientDrawable religiousQuestionBg =
            new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{
                            GOLD_SOFT,
                            WHITE
                    }
            );

    religiousQuestionBg.setCornerRadius(
            dp(20)
    );

    religiousQuestionBg.setStroke(
            dp(1),
            Color.rgb(235, 228, 208)
    );

    religiousQuestion.setBackground(
            religiousQuestionBg
    );

    ImageView questionIcon =
            new ImageView(this);

    int questionIconId =
            getResources().getIdentifier(
                    "ic_question",
                    "drawable",
                    getPackageName()
            );

    if (questionIconId != 0) {
        questionIcon.setImageResource(
                questionIconId
        );
    } else {
        questionIcon.setImageResource(
                R.drawable.ic_quran
        );
    }

    questionIcon.setColorFilter(
            GOLD,
            android.graphics.PorterDuff.Mode.SRC_IN
    );

    questionIcon.setPadding(
            dp(10),
            dp(10),
            dp(10),
            dp(10)
    );

    GradientDrawable questionIconBg =
            new GradientDrawable();

    questionIconBg.setColor(
            Color.argb(55, 190, 155, 75)
    );

    questionIconBg.setShape(
            GradientDrawable.OVAL
    );

    questionIcon.setBackground(
            questionIconBg
    );

    religiousQuestion.addView(
            questionIcon,
            new LinearLayout.LayoutParams(
                    dp(52),
                    dp(52)
            )
    );

    LinearLayout questionText =
            new LinearLayout(this);

    questionText.setOrientation(
            LinearLayout.VERTICAL
    );

    questionText.setGravity(
            Gravity.RIGHT
    );

    TextView questionTitle =
            text(
                    "سؤال ديني",
                    16,
                    TEXT,
                    true
            );

    questionTitle.setGravity(
            Gravity.RIGHT
    );

    TextView questionSub =
            text(
                    "اختبر معلوماتك الإسلامية",
                    10,
                    TEXT_SECONDARY,
                    false
            );

    questionSub.setGravity(
            Gravity.RIGHT
    );

    questionSub.setPadding(
            0,
            dp(4),
            0,
            0
    );

    questionText.addView(
            questionTitle
    );

    questionText.addView(
            questionSub
    );

    LinearLayout.LayoutParams questionTextParams =
            new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );

    questionTextParams.setMargins(
            dp(14),
            0,
            dp(8),
            0
    );

    religiousQuestion.addView(
            questionText,
            questionTextParams
    );

    TextView questionArrow =
            text(
                    "‹",
                    24,
                    GOLD,
                    true
            );

    questionArrow.setGravity(
            Gravity.CENTER
    );

    religiousQuestion.addView(
            questionArrow,
            new LinearLayout.LayoutParams(
                    dp(25),
                    dp(50)
            )
    );

    religiousQuestion.setOnClickListener(v -> {
        startActivity(
                new android.content.Intent(
                        MainActivity.this,
                        IslamicQuestionsActivity.class
                )
        );
    });

    section.addView(
            religiousQuestion,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(82)
            )
    );

    LinearLayout.LayoutParams sectionParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    sectionParams.setMargins(
            0,
            0,
            0,
            dp(20)
    );

    content.addView(section, sectionParams);
}

private LinearLayout.LayoutParams miniWeight() {
    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
            );

    params.setMargins(
            dp(3),
            0,
            dp(3),
            0
    );

    return params;
}

private void buildContinueReading() {

    android.content.SharedPreferences readingPrefs =
            getSharedPreferences(
                    "quran_reading",
                    MODE_PRIVATE
            );

    int savedSurahIndex =
            readingPrefs.getInt(
                    "surah_index",
                    -1
            );

    int savedAyahIndex =
            readingPrefs.getInt(
                    "ayah_index",
                    -1
            );

    String savedSurahName =
            readingPrefs.getString(
                    "surah_name",
                    ""
            );

    boolean hasReading =
            savedSurahIndex >= 0
                    && savedAyahIndex >= 0
                    && !savedSurahName.isEmpty();

    String displayedSurah =
            hasReading
                    ? "سورة " + savedSurahName
                    : "ابدأ قراءة القرآن";

    String displayedVerse =
            hasReading
                    ? "آخر قراءة • الآية " + (savedAyahIndex + 1)
                    : "لم يتم حفظ موضع قراءة بعد";

    LinearLayout card =
            new LinearLayout(this);

    card.setOrientation(
            LinearLayout.VERTICAL
    );

    card.setPadding(
            dp(18),
            dp(17),
            dp(18),
            dp(17)
    );

    GradientDrawable cardBg =
            new GradientDrawable();

    cardBg.setColor(WHITE);
    cardBg.setCornerRadius(dp(22));
    cardBg.setStroke(
            dp(1),
            BORDER
    );

    card.setBackground(cardBg);

    // Header
    LinearLayout header =
            new LinearLayout(this);

    header.setOrientation(
            LinearLayout.HORIZONTAL
    );

    header.setGravity(
            Gravity.CENTER_VERTICAL
    );

    LinearLayout titleBox =
            new LinearLayout(this);

    titleBox.setOrientation(
            LinearLayout.VERTICAL
    );

    titleBox.setGravity(
            Gravity.RIGHT
    );

    TextView title =
            text(
                    "متابعة القراءة",
                    17,
                    TEXT,
                    true
            );

    title.setGravity(
            Gravity.RIGHT
    );

    TextView subtitle =
            text(
                    "أكمل وردك من حيث توقفت",
                    10,
                    TEXT_SECONDARY,
                    false
            );

    subtitle.setGravity(
            Gravity.RIGHT
    );

    subtitle.setPadding(
            0,
            dp(3),
            0,
            0
    );

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

    FrameLayout iconHolder =
            new FrameLayout(this);

    GradientDrawable iconBg =
            new GradientDrawable();

    iconBg.setColor(
            EMERALD_SOFT
    );

    iconBg.setShape(
            GradientDrawable.OVAL
    );

    iconHolder.setBackground(
            iconBg
    );

    ImageView quranIcon =
            new ImageView(this);

    quranIcon.setImageResource(
            R.drawable.ic_quran
    );

    quranIcon.setScaleType(
            ImageView.ScaleType.CENTER_INSIDE
    );

    quranIcon.setPadding(
            dp(9),
            dp(9),
            dp(9),
            dp(9)
    );

    iconHolder.addView(
            quranIcon,
            new FrameLayout.LayoutParams(
                    dp(46),
                    dp(46),
                    Gravity.CENTER
            )
    );

    header.addView(
            iconHolder,
            new LinearLayout.LayoutParams(
                    dp(46),
                    dp(46)
            )
    );

    card.addView(header);

    // Reading information
    TextView surah =
            text(
                    displayedSurah,
                    23,
                    EMERALD_DARK,
                    true
            );

    surah.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams surahParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    surahParams.setMargins(
            0,
            dp(16),
            0,
            0
    );

    card.addView(
            surah,
            surahParams
    );

    TextView verse =
            text(
                    displayedVerse,
                    12,
                    TEXT_SECONDARY,
                    false
            );

    verse.setGravity(
            Gravity.RIGHT
    );

    LinearLayout.LayoutParams verseParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    verseParams.setMargins(
            0,
            dp(3),
            0,
            0
    );

    card.addView(
            verse,
            verseParams
    );

    // Progress
    float progressValue = 0.72f;
    int progressPercent = 72;

    LinearLayout progressRow =
            new LinearLayout(this);

    progressRow.setOrientation(
            LinearLayout.HORIZONTAL
    );

    progressRow.setGravity(
            Gravity.CENTER_VERTICAL
    );

    TextView percentage =
            text(
                    progressPercent + "%",
                    12,
                    EMERALD_DARK,
                    true
            );

    percentage.setGravity(
            Gravity.CENTER
    );

    GradientDrawable percentageBg =
            new GradientDrawable();

    percentageBg.setColor(
            GOLD_SOFT
    );

    percentageBg.setShape(
            GradientDrawable.OVAL
    );

    percentageBg.setStroke(
            dp(1),
            Color.rgb(235, 218, 172)
    );

    percentage.setBackground(
            percentageBg
    );

    progressRow.addView(
            percentage,
            new LinearLayout.LayoutParams(
                    dp(42),
                    dp(42)
            )
    );

    LinearLayout progressArea =
            new LinearLayout(this);

    progressArea.setOrientation(
            LinearLayout.VERTICAL
    );

    progressArea.setGravity(
            Gravity.CENTER_VERTICAL
    );

    LinearLayout progressBackground =
            new LinearLayout(this);

    GradientDrawable progressBg =
            new GradientDrawable();

    progressBg.setColor(
            Color.rgb(232, 237, 234)
    );

    progressBg.setCornerRadius(
            dp(6)
    );

    progressBackground.setBackground(
            progressBg
    );

    LinearLayout progress =
            new LinearLayout(this);

    GradientDrawable progressFill =
            new GradientDrawable();

    progressFill.setColor(
            EMERALD
    );

    progressFill.setCornerRadius(
            dp(6)
    );

    progress.setBackground(
            progressFill
    );

    progressBackground.addView(
            progress,
            new LinearLayout.LayoutParams(
                    0,
                    dp(6),
                    progressValue
            )
    );

    progressBackground.addView(
            new View(this),
            new LinearLayout.LayoutParams(
                    0,
                    dp(6),
                    1f - progressValue
            )
    );

    progressArea.addView(
            progressBackground,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(6)
            )
    );

    TextView progressLabel =
            text(
                    progressPercent + "% من السورة",
                    10,
                    TEXT_SECONDARY,
                    false
            );

    progressLabel.setGravity(
            Gravity.RIGHT
    );

    progressLabel.setPadding(
            0,
            dp(6),
            0,
            0
    );

    progressArea.addView(
            progressLabel
    );

    LinearLayout.LayoutParams progressAreaParams =
            new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );

    progressAreaParams.setMargins(
            dp(13),
            0,
            0,
            0
    );

    progressRow.addView(
            progressArea,
            progressAreaParams
    );

    LinearLayout.LayoutParams progressRowParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    progressRowParams.setMargins(
            0,
            dp(15),
            0,
            0
    );

    card.addView(
            progressRow,
            progressRowParams
    );

    // Continue button
    continueButtonView =
            text(
                    hasReading
                            ? "متابعة القراءة  ←"
                            : "ابدأ قراءة القرآن  ←",
                    12,
                    WHITE,
                    true
            );

    continueButtonView.setGravity(
            Gravity.CENTER
    );

    GradientDrawable buttonBg =
            new GradientDrawable();

    buttonBg.setColor(
            EMERALD_DARK
    );

    buttonBg.setCornerRadius(
            dp(14)
    );

    continueButtonView.setBackground(
            buttonBg
    );

    continueButtonView.setOnClickListener(v -> {

        android.content.SharedPreferences latestPrefs =
                getSharedPreferences(
                        "quran_reading",
                        MODE_PRIVATE
                );

        int latestSurahIndex =
                latestPrefs.getInt(
                        "surah_index",
                        -1
                );

        int latestAyahIndex =
                latestPrefs.getInt(
                        "ayah_index",
                        -1
                );

        android.content.Intent intent =
                new android.content.Intent(
                        MainActivity.this,
                        QuranActivity.class
                );

        if (latestSurahIndex >= 0
                && latestAyahIndex >= 0) {

            intent.putExtra(
                    "open_surah_index",
                    latestSurahIndex
            );

            intent.putExtra(
                    "open_ayah_index",
                    latestAyahIndex
            );
        }

        startActivity(intent);
    });

    LinearLayout.LayoutParams buttonParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(44)
            );

    buttonParams.setMargins(
            0,
            dp(15),
            0,
            0
    );

    card.addView(
            continueButtonView,
            buttonParams
    );

    LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    params.setMargins(
            0,
            0,
            0,
            dp(16)
    );

    content.addView(
            card,
            params
    );
}

private void updateContinueReading() {

    if (continueSurahView == null
            || continueVerseView == null
            || continueButtonView == null) {
        return;
    }

    android.content.SharedPreferences readingPrefs =
            getSharedPreferences(
                    "quran_reading",
                    MODE_PRIVATE
            );

    int surahIndex =
            readingPrefs.getInt(
                    "surah_index",
                    -1
            );

    int ayahIndex =
            readingPrefs.getInt(
                    "ayah_index",
                    -1
            );

    String surahName =
            readingPrefs.getString(
                    "surah_name",
                    ""
            );

    boolean hasReading =
            surahIndex >= 0
                    && ayahIndex >= 0
                    && !surahName.isEmpty();

    if (hasReading) {

        continueSurahView.setText(
                "سورة " + surahName
        );

        continueVerseView.setText(
                "آخر قراءة • الآية " + (ayahIndex + 1)
        );

        continueButtonView.setText(
                "متابعة القراءة  ←"
        );

    } else {

        continueSurahView.setText(
                "ابدأ قراءة القرآن"
        );

        continueVerseView.setText(
                "لم يتم حفظ موضع قراءة بعد"
        );

        continueButtonView.setText(
                "ابدأ قراءة القرآن  ←"
        );
    }
}

private LinearLayout addPremiumMiniAction(
        int iconRes,
        String titleText,
        String subtitleText,
        final Runnable action
) {

    LinearLayout card =
            new LinearLayout(this);

    card.setOrientation(
            LinearLayout.VERTICAL
    );

    card.setGravity(
            Gravity.CENTER
    );

    card.setPadding(
            dp(8),
            dp(9),
            dp(8),
            dp(9)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(WHITE);
    bg.setCornerRadius(dp(18));
    bg.setStroke(
            dp(1),
            BORDER
    );

    card.setBackground(bg);
    card.setElevation(dp(1));

    ImageView icon =
            new ImageView(this);

    icon.setImageResource(iconRes);
    icon.setScaleType(
            ImageView.ScaleType.CENTER_INSIDE
    );

    icon.setPadding(
            dp(8),
            dp(8),
            dp(8),
            dp(8)
    );

    GradientDrawable iconBg =
            new GradientDrawable();

    iconBg.setColor(EMERALD_SOFT);
    iconBg.setShape(
            GradientDrawable.OVAL
    );

    icon.setBackground(iconBg);

    LinearLayout.LayoutParams iconParams =
            new LinearLayout.LayoutParams(
                    dp(42),
                    dp(42)
            );

    card.addView(
            icon,
            iconParams
    );

    TextView title =
            text(
                    titleText,
                    13,
                    TEXT,
                    true
            );

    title.setGravity(
            Gravity.CENTER
    );

    LinearLayout.LayoutParams titleParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    titleParams.setMargins(
            0,
            dp(7),
            0,
            0
    );

    card.addView(
            title,
            titleParams
    );

    TextView subtitle =
            text(
                    subtitleText,
                    9,
                    TEXT_SECONDARY,
                    false
            );

    subtitle.setGravity(
            Gravity.CENTER
    );

    LinearLayout.LayoutParams subtitleParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    subtitleParams.setMargins(
            0,
            dp(3),
            0,
            0
    );

    card.addView(
            subtitle,
            subtitleParams
    );

    card.setOnClickListener(
            v -> action.run()
    );

    return card;
}

private void buildAyahCard() {

    LinearLayout card =
            new LinearLayout(this);

    card.setOrientation(
            LinearLayout.VERTICAL
    );

    card.setGravity(
            Gravity.CENTER
    );

    card.setPadding(
            dp(24),
            dp(23),
            dp(24),
            dp(22)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(
            EMERALD_DARK
    );

    bg.setCornerRadius(
            dp(24)
    );

    card.setBackground(bg);

    // عنوان البطاقة
    TextView label =
            text(
                    "✦  آية اليوم  ✦",
                    12,
                    GOLD,
                    true
            );

    label.setGravity(
            Gravity.CENTER
    );

    card.addView(
            label,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
    );

    // زخرفة بسيطة
    View line =
            new View(this);

    GradientDrawable lineBg =
            new GradientDrawable();

    lineBg.setColor(
            Color.argb(150, 190, 155, 75)
    );

    lineBg.setCornerRadius(
            dp(2)
    );

    line.setBackground(
            lineBg
    );

    LinearLayout.LayoutParams lineParams =
            new LinearLayout.LayoutParams(
                    dp(42),
                    dp(2)
            );

    lineParams.setMargins(
            0,
            dp(12),
            0,
            dp(16)
    );

    card.addView(
            line,
            lineParams
    );

    // الآية
    TextView ayah =
            text(
                    "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
                    25,
                    WHITE,
                    true
            );

    ayah.setGravity(
            Gravity.CENTER
    );

    ayah.setLineSpacing(
            dp(5),
            1.15f
    );

    ayah.setPadding(
            dp(4),
            0,
            dp(4),
            0
    );

    card.addView(
            ayah,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
    );

    // تكملة الآية
    TextView ayahSecond =
            text(
                    "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا",
                    17,
                    Color.rgb(220, 235, 228),
                    false
            );

    ayahSecond.setGravity(
            Gravity.CENTER
    );

    ayahSecond.setPadding(
            0,
            dp(9),
            0,
            0
    );

    card.addView(
            ayahSecond,
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
    );

    // المرجع
    TextView source =
            text(
                    "سورة الشرح • 5 - 6",
                    11,
                    GOLD,
                    true
            );

    source.setGravity(
            Gravity.CENTER
    );

    LinearLayout.LayoutParams sourceParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    sourceParams.setMargins(
            0,
            dp(16),
            0,
            0
    );

    card.addView(
            source,
            sourceParams
    );

    LinearLayout.LayoutParams cardParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    cardParams.setMargins(
            0,
            0,
            0,
            dp(17)
    );

    content.addView(
            card,
            cardParams
    );
}

private void buildDailyReminder() {

    LinearLayout card =
            new LinearLayout(this);

    card.setOrientation(
            LinearLayout.HORIZONTAL
    );

    card.setGravity(
            Gravity.CENTER_VERTICAL
    );

    card.setPadding(
            dp(16),
            dp(15),
            dp(16),
            dp(15)
    );

    GradientDrawable bg =
            new GradientDrawable();

    bg.setColor(
            WHITE
    );

    bg.setCornerRadius(
            dp(20)
    );

    bg.setStroke(
            dp(1),
            BORDER
    );

    card.setBackground(bg);

    // أيقونة بسيطة وهادئة
    TextView icon =
            text(
                    "✦",
                    20,
                    GOLD,
                    true
            );

    icon.setGravity(
            Gravity.CENTER
    );

    GradientDrawable iconBg =
            new GradientDrawable();

    iconBg.setColor(
            GOLD_SOFT
    );

    iconBg.setShape(
            GradientDrawable.OVAL
    );

    icon.setBackground(
            iconBg
    );

    LinearLayout.LayoutParams iconParams =
            new LinearLayout.LayoutParams(
                    dp(48),
                    dp(48)
            );

    iconParams.setMargins(
            0,
            0,
            dp(13),
            0
    );

    card.addView(
            icon,
            iconParams
    );

    // النص
    LinearLayout texts =
            new LinearLayout(this);

    texts.setOrientation(
            LinearLayout.VERTICAL
    );

    texts.setGravity(
            Gravity.RIGHT
    );

    TextView title =
            text(
                    "تذكير اليوم",
                    15,
                    TEXT,
                    true
            );

    title.setGravity(
            Gravity.RIGHT
    );

    texts.addView(title);

    TextView description =
            text(
                    "اجعل لسانك عامرًا بذكر الله",
                    11,
                    TEXT_SECONDARY,
                    false
            );

    description.setGravity(
            Gravity.RIGHT
    );

    description.setPadding(
            0,
            dp(4),
            0,
            0
    );

    texts.addView(description);

    LinearLayout.LayoutParams textParams =
            new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );

    textParams.setMargins(
            0,
            0,
            dp(8),
            0
    );

    card.addView(
            texts,
            textParams
    );

    // زر الأذكار
    TextView action =
            text(
                    "اذكر الله",
                    11,
                    EMERALD_DARK,
                    true
            );

    action.setGravity(
            Gravity.CENTER
    );

    action.setPadding(
            dp(10),
            0,
            dp(10),
            0
    );

    GradientDrawable actionBg =
            new GradientDrawable();

    actionBg.setColor(
            EMERALD_SOFT
    );

    actionBg.setCornerRadius(
            dp(13)
    );

    action.setBackground(
            actionBg
    );

    action.setOnClickListener(
            v -> startActivity(
                    new android.content.Intent(
                            MainActivity.this,
                            AdhkarActivity.class
                    )
            )
    );

    card.addView(
            action,
            new LinearLayout.LayoutParams(
                    dp(74),
                    dp(38)
            )
    );

    // الضغط على البطاقة يفتح الأذكار
    card.setOnClickListener(
            v -> startActivity(
                    new android.content.Intent(
                            MainActivity.this,
                            AdhkarActivity.class
                    )
            )
    );

    LinearLayout.LayoutParams cardParams =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

    cardParams.setMargins(
            0,
            0,
            0,
            dp(16)
    );

    content.addView(
            card,
            cardParams
    );
}

private LinearLayout buildBottomNavigation() {

    LinearLayout nav =
            new LinearLayout(this);

    nav.setOrientation(
            LinearLayout.HORIZONTAL
    );

    nav.setGravity(
            Gravity.CENTER_VERTICAL
    );

    nav.setPadding(
            dp(8),
            dp(7),
            dp(8),
            dp(7)
    );

    GradientDrawable navBg =
            new GradientDrawable();

    navBg.setColor(
            WHITE
    );

    navBg.setCornerRadius(
            dp(22)
    );

    navBg.setStroke(
            dp(1),
            BORDER
    );

    nav.setBackground(navBg);

    // ظل خفيف فقط — بدون الإحساس بالكارت العائم
    nav.setElevation(dp(4));

    homeNav =
            premiumNavItem(
                    R.drawable.ic_crescent,
                    "الرئيسية",
                    true
            );

    quranNav =
            premiumNavItem(
                    R.drawable.ic_quran,
                    "القرآن",
                    false
            );

    adhkarNav =
            premiumNavItem(
                    R.drawable.ic_adhkar,
                    "الأذكار",
                    false
            );

    adhkarNav.setOnClickListener(
            v -> startActivity(
                    new android.content.Intent(
                            MainActivity.this,
                            AdhkarActivity.class
                    )
            )
    );

    prayerNav =
            premiumNavItem(
                    R.drawable.ic_qibla,
                    "الصلاة",
                    false
            );

    moreNav =
            premiumNavTextItem(
                    "•••",
                    "المزيد",
                    false
            );

        moreNav.setClickable(true);
        moreNav.setFocusable(true);

        moreNav.setOnClickListener(v -> {
            android.content.Intent intent =
                    new android.content.Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });

    nav.addView(
            homeNav,
            navWeight()
    );

    nav.addView(
            quranNav,
            navWeight()
    );

    nav.addView(
            adhkarNav,
            navWeight()
    );

    nav.addView(
            prayerNav,
            navWeight()
    );

    nav.addView(
            moreNav,
            navWeight()
    );

    return nav;
}

private LinearLayout premiumNavItem(
            int iconRes,
            String labelText,
            boolean selected
    ) {

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(
                dp(3),
                dp(4),
                dp(3),
                dp(4)
        );

        if (selected) {

            GradientDrawable selectedBg =
                    new GradientDrawable(
                            GradientDrawable.Orientation.TL_BR,
                            new int[]{
                                    EMERALD_SOFT,
                                    Color.rgb(224, 241, 233)
                            }
                    );

            selectedBg.setCornerRadius(dp(19));

            item.setBackground(selectedBg);
        }

        ImageView icon = new ImageView(this);

        icon.setImageResource(iconRes);

        icon.setColorFilter(
                selected
                        ? EMERALD
                        : TEXT_SECONDARY
        );

        icon.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(25),
                        dp(25)
                );

        item.addView(icon, iconParams);

        TextView label = text(
                labelText,
                10,
                selected
                        ? EMERALD
                        : TEXT_SECONDARY,
                selected
        );

        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setIncludeFontPadding(false);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(20)
                );

        labelParams.topMargin = dp(3);

        item.addView(
                label,
                labelParams
        );

        if (selected) {

            View indicator = new View(this);

            GradientDrawable indicatorBg =
                    new GradientDrawable();

            indicatorBg.setColor(GOLD);
            indicatorBg.setCornerRadius(dp(4));

            indicator.setBackground(
                    indicatorBg
            );

            LinearLayout.LayoutParams indicatorParams =
                    new LinearLayout.LayoutParams(
                            dp(20),
                            dp(3)
                    );

            indicatorParams.topMargin = dp(3);

            item.addView(
                    indicator,
                    indicatorParams
            );
        }

        return item;
    }

    private LinearLayout premiumNavTextItem(
            String iconText,
            String labelText,
            boolean selected
    ) {

        LinearLayout item = new LinearLayout(this);

        item.setOrientation(
                LinearLayout.VERTICAL
        );

        item.setGravity(Gravity.CENTER);

        item.setPadding(
                dp(3),
                dp(4),
                dp(3),
                dp(4)
        );

        if (selected) {

            GradientDrawable selectedBg =
                    new GradientDrawable();

            selectedBg.setColor(
                    EMERALD_SOFT
            );

            selectedBg.setCornerRadius(
                    dp(19)
            );

            item.setBackground(
                    selectedBg
            );
        }

        TextView icon = text(
                iconText,
                18,
                selected
                        ? EMERALD
                        : TEXT_SECONDARY,
                true
        );

        icon.setGravity(
                Gravity.CENTER
        );

        icon.setIncludeFontPadding(
                false
        );

        item.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(25),
                        dp(25)
                )
        );

        TextView label = text(
                labelText,
                10,
                selected
                        ? EMERALD
                        : TEXT_SECONDARY,
                selected
        );

        label.setGravity(
                Gravity.CENTER
        );

        label.setSingleLine(true);
        label.setIncludeFontPadding(false);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(20)
                );

        labelParams.topMargin = dp(3);

        item.addView(
                label,
                labelParams
        );

        return item;
    }

private LinearLayout.LayoutParams navWeight() {
        return new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
        );
    }

    private LinearLayout navItem(
            int iconRes,
            String labelText,
            boolean selected
    ) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(4), dp(3), dp(4), dp(3));

        if (selected) {
            GradientDrawable selectedBg = new GradientDrawable();
            selectedBg.setColor(EMERALD_SOFT);
            selectedBg.setCornerRadius(dp(17));
            item.setBackground(selectedBg);
        }

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setColorFilter(
                selected ? EMERALD : TEXT_SECONDARY
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(25),
                        dp(25)
                );

        item.addView(icon, iconParams);

        TextView label = text(
                labelText,
                10,
                selected ? EMERALD : TEXT_SECONDARY,
                selected
        );

        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setIncludeFontPadding(false);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(21)
                );

        labelParams.topMargin = dp(3);

        item.addView(label, labelParams);

        if (selected) {
            View indicator = new View(this);

            GradientDrawable indicatorBg = new GradientDrawable();
            indicatorBg.setColor(GOLD);
            indicatorBg.setCornerRadius(dp(3));
            indicator.setBackground(indicatorBg);

            LinearLayout.LayoutParams indicatorParams =
                    new LinearLayout.LayoutParams(
                            dp(18),
                            dp(3)
                    );

            indicatorParams.topMargin = dp(2);

            item.addView(
                    indicator,
                    indicatorParams
            );
        }

        return item;
    }

    private LinearLayout navTextItem(
            String iconText,
            String labelText,
            boolean selected
    ) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(4), dp(3), dp(4), dp(3));

        if (selected) {
            GradientDrawable selectedBg = new GradientDrawable();
            selectedBg.setColor(EMERALD_SOFT);
            selectedBg.setCornerRadius(dp(17));
            item.setBackground(selectedBg);
        }

        TextView icon = text(
                iconText,
                20,
                selected ? EMERALD : TEXT_SECONDARY,
                true
        );

        icon.setGravity(Gravity.CENTER);
        icon.setIncludeFontPadding(false);

        item.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(25),
                        dp(25)
                )
        );

        TextView label = text(
                labelText,
                10,
                selected ? EMERALD : TEXT_SECONDARY,
                selected
        );

        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setIncludeFontPadding(false);

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(21)
                );

        labelParams.topMargin = dp(3);

        item.addView(label, labelParams);

        return item;
    }

    private LinearLayout.LayoutParams weight() {
        return new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
        );
    }

// ============================================================
    // HELPERS
    // ============================================================

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
        view.setGravity(Gravity.RIGHT);
        view.setIncludeFontPadding(true);

        if (bold) {
            view.setTypeface(
                    Typeface.create("sans-serif", Typeface.BOLD)
            );
        } else {
            view.setTypeface(
                    Typeface.create("sans-serif", Typeface.NORMAL)
            );
        }

        return view;
    }

    private GradientDrawable roundDrawable(
            int fill,
            int radius,
            int stroke
    ) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));

        if (stroke != Color.TRANSPARENT) {
            drawable.setStroke(dp(1), stroke);
        }

        return drawable;
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private final BroadcastReceiver updateDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                return;
            }

            long downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID,
                    -1
            );

            if (downloadId != updateDownloadId) {
                return;
            }

            try {
                DownloadManager downloadManager =
                        (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                DownloadManager.Query query =
                        new DownloadManager.Query().setFilterById(downloadId);

                android.database.Cursor cursor = downloadManager.query(query);

                if (cursor != null && cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(
                            DownloadManager.COLUMN_STATUS
                    );

                    int status = cursor.getInt(statusIndex);

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        int localUriIndex = cursor.getColumnIndex(
                                DownloadManager.COLUMN_LOCAL_URI
                        );

                        String localUri = cursor.getString(localUriIndex);

                        if (localUri != null && localUri.startsWith("file://")) {
                            File apkFile = new File(Uri.parse(localUri).getPath());

                            if (apkFile.exists()) {
                                Uri apkUri = FileProvider.getUriForFile(
                                        MainActivity.this,
                                        getPackageName() + ".fileprovider",
                                        apkFile
                                );

                                Intent installIntent = new Intent(
                                        Intent.ACTION_VIEW
                                );
                                installIntent.setDataAndType(
                                        apkUri,
                                        "application/vnd.android.package-archive"
                                );
                                installIntent.addFlags(
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                                installIntent.addFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                );

                                startActivity(installIntent);
                            }
                        }
                    }
                }

                if (cursor != null) {
                    cursor.close();
                }

            } catch (Exception e) {
                android.widget.Toast.makeText(
                        MainActivity.this,
                        "تعذر فتح ملف التحديث للتثبيت",
                        android.widget.Toast.LENGTH_LONG
                ).show();
            }
        }
    };

    private void downloadUpdate(String downloadUrl) {
        try {
            DownloadManager downloadManager =
                    (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

            Uri uri = Uri.parse(downloadUrl);

            DownloadManager.Request request =
                    new DownloadManager.Request(uri);

            request.setTitle("لعلها المنجيه");
            request.setDescription("جاري تنزيل التحديث...");
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            );
            request.setDestinationInExternalFilesDir(
                    this,
                    Environment.DIRECTORY_DOWNLOADS,
                    "LaalahaAlMunjiah-update.apk"
            );

            updateDownloadId = downloadManager.enqueue(request);

            android.widget.Toast.makeText(
                    this,
                    "بدأ تنزيل التحديث",
                    android.widget.Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            android.widget.Toast.makeText(
                    this,
                    "تعذر بدء تنزيل التحديث",
                    android.widget.Toast.LENGTH_LONG
            ).show();
        }
    }

    private void checkForUpdate() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(UPDATE_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setUseCaches(false);

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                JSONObject update = new JSONObject(response.toString());
                int latestVersionCode = update.optInt("versionCode", 0);

                if (latestVersionCode > getAppVersionCode()) {
                    runOnUiThread(() -> {
                        String title = update.optString("title", "تحديث جديد متاح");
                        String changelog = update.optString("changelog", "يتوفر إصدار جديد من التطبيق.");

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(title)
                                .setMessage(changelog)
                                .setNegativeButton("لاحقًا", null)
                                .setPositiveButton("تحديث الآن", (dialog, which) -> {
                                    String downloadUrl = update.optString("downloadUrl", "");
                                    if (!downloadUrl.isEmpty()) {
                                        downloadUpdate(downloadUrl);
                                    }
                                })
                                .show();
                    });
                }

            } catch (Exception ignored) {
                // Update checking must never affect normal app operation.
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }



    private long getAppVersionCode() {
        try {
            android.content.pm.PackageInfo packageInfo =
                    getPackageManager().getPackageInfo(getPackageName(), 0);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                return packageInfo.getLongVersionCode();
            }

            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }


}
