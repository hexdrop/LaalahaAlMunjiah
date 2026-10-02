package com.laallaha.almunjiah;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class QiblaActivity extends Activity implements SensorEventListener {

    private static final int LOCATION_REQUEST = 4101;

    // Kaaba
    private static final double KAABA_LAT = 21.422487;
    private static final double KAABA_LON = 39.826206;

    private static final int EMERALD = Color.rgb(18, 115, 85);
    private static final int EMERALD_DARK = Color.rgb(10, 79, 59);
    private static final int EMERALD_SOFT = Color.rgb(231, 244, 238);
    private static final int GOLD = Color.rgb(190, 155, 75);
    private static final int CREAM = Color.rgb(249, 248, 243);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 45, 41);
    private static final int TEXT_SECONDARY = Color.rgb(105, 113, 108);
    private static final int BORDER = Color.rgb(228, 231, 226);

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private Sensor accelerometerSensor;
    private Sensor magneticSensor;

    private LocationManager locationManager;

    private ImageView qiblaNeedle;
    private TextView directionText;
    private TextView angleText;
    private TextView locationText;
    private TextView statusText;

    private float currentAzimuth = 0f;
    private double qiblaBearing = 0.0;
    private boolean hasQibla = false;

    private boolean locationRequested = false;

    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    private final float[] accelerometerValues = new float[3];
    private final float[] magneticValues = new float[3];

    private boolean hasAccelerometer = false;
    private boolean hasMagnetic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR
        );

        accelerometerSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
        );

        magneticSensor = sensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD
        );

        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        buildUi();
        checkLocationPermission();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(
            String value,
            float size,
            int color,
            boolean bold
    ) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);

        if (bold) {
            t.setTypeface(Typeface.create(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            ));
        }

        return t;
    }

    private GradientDrawableCompat background(
            int color,
            float radius,
            int strokeColor
    ) {
        return new GradientDrawableCompat(
                color,
                dp(radius),
                strokeColor
        );
    }

    private void buildUi() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);

        // =====================================================
        // HEADER
        // =====================================================

        FrameLayout header = new FrameLayout(this);
        header.setPadding(
                dp(18),
                dp(10),
                dp(18),
                dp(12)
        );

        GradientDrawableCompat headerBg =
                background(EMERALD_DARK, 0, EMERALD_DARK);

        header.setBackground(headerBg);

        TextView back = text(
                "‹",
                36,
                WHITE,
                false
        );

        FrameLayout.LayoutParams backParams =
                new FrameLayout.LayoutParams(
                        dp(48),
                        dp(48),
                        Gravity.START | Gravity.CENTER_VERTICAL
                );

        back.setOnClickListener(v -> finish());
        header.addView(back, backParams);

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setGravity(Gravity.CENTER);

        TextView title = text(
                "القبلة",
                23,
                WHITE,
                true
        );

        TextView subtitle = text(
                "اتجاه بيت الله الحرام",
                11,
                Color.rgb(214, 232, 224),
                false
        );

        titleBox.addView(title);
        titleBox.addView(subtitle);

        header.addView(
                titleBox,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                )
        );

        TextView star = text(
                "✦",
                22,
                GOLD,
                true
        );

        FrameLayout.LayoutParams starParams =
                new FrameLayout.LayoutParams(
                        dp(40),
                        dp(40),
                        Gravity.END | Gravity.CENTER_VERTICAL
                );

        header.addView(star, starParams);

        root.addView(
                header,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(76)
                )
        );

        // =====================================================
        // CONTENT
        // =====================================================

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(20)
        );

        TextView intro = text(
                "وجّه هاتفك نحو القبلة",
                18,
                TEXT,
                true
        );

        content.addView(
                intro,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        TextView introSub = text(
                "حرّك الهاتف ببطء حتى يستقر المؤشر في اتجاه مكة",
                11,
                TEXT_SECONDARY,
                false
        );

        LinearLayout.LayoutParams introSubParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        introSubParams.setMargins(
                0,
                dp(4),
                0,
                dp(16)
        );

        content.addView(introSub, introSubParams);

        // =====================================================
        // COMPASS
        // =====================================================

        FrameLayout compass = new FrameLayout(this);

        GradientDrawableCompat compassBg =
                background(WHITE, 150, BORDER);

        compass.setBackground(compassBg);
        compass.setElevation(dp(8));

        // Outer decorative ring
        FrameLayout ring = new FrameLayout(this);

        GradientDrawableCompat ringBg =
                background(EMERALD_SOFT, 140, EMERALD);

        ring.setBackground(ringBg);

        FrameLayout.LayoutParams ringParams =
                new FrameLayout.LayoutParams(
                        dp(250),
                        dp(250),
                        Gravity.CENTER
                );

        compass.addView(ring, ringParams);

        // Direction labels
        addCompassLabel(ring, "ش", Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        addCompassLabel(ring, "ج", Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        addCompassLabel(ring, "غ", Gravity.CENTER_VERTICAL | Gravity.START);
        addCompassLabel(ring, "ق", Gravity.CENTER_VERTICAL | Gravity.END);

        // Central Qibla needle
        qiblaNeedle = new ImageView(this);
        qiblaNeedle.setImageDrawable(
                new QiblaNeedleDrawable(
                        dp(104),
                        dp(132),
                        EMERALD,
                        GOLD
                )
        );

        FrameLayout.LayoutParams needleParams =
                new FrameLayout.LayoutParams(
                        dp(150),
                        dp(150),
                        Gravity.CENTER
                );

        compass.addView(qiblaNeedle, needleParams);

        // Kaaba center badge
        TextView kaaba = text(
                "🕋",
                31,
                TEXT,
                false
        );

        FrameLayout.LayoutParams kaabaParams =
                new FrameLayout.LayoutParams(
                        dp(70),
                        dp(70),
                        Gravity.CENTER
                );

        compass.addView(kaaba, kaabaParams);

        content.addView(
                compass,
                new LinearLayout.LayoutParams(
                        dp(292),
                        dp(292)
                )
        );

        // =====================================================
        // ANGLE
        // =====================================================

        angleText = text(
                "—°",
                30,
                EMERALD_DARK,
                true
        );

        LinearLayout.LayoutParams angleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        angleParams.setMargins(
                0,
                dp(12),
                0,
                0
        );

        content.addView(angleText, angleParams);

        directionText = text(
                "جاري تحديد اتجاه القبلة",
                13,
                TEXT_SECONDARY,
                true
        );

        content.addView(directionText);

        // =====================================================
        // STATUS CARD
        // =====================================================

        LinearLayout statusCard = new LinearLayout(this);
        statusCard.setOrientation(LinearLayout.VERTICAL);
        statusCard.setGravity(Gravity.CENTER);
        statusCard.setPadding(
                dp(14),
                dp(12),
                dp(14),
                dp(12)
        );

        statusCard.setBackground(
                background(WHITE, 20, BORDER)
        );

        statusText = text(
                "جاري تحديد موقعك...",
                12,
                TEXT,
                true
        );

        locationText = text(
                "",
                10,
                TEXT_SECONDARY,
                false
        );

        statusCard.addView(statusText);

        LinearLayout.LayoutParams locationParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        locationParams.setMargins(
                0,
                dp(3),
                0,
                0
        );

        statusCard.addView(locationText, locationParams);

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        statusParams.setMargins(
                dp(12),
                dp(12),
                dp(12),
                0
        );

        content.addView(statusCard, statusParams);

        root.addView(
                content,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                )
        );

        setContentView(root);
    }

    private void addCompassLabel(
            FrameLayout parent,
            String value,
            int gravity
    ) {
        TextView label = text(
                value,
                14,
                EMERALD_DARK,
                true
        );

        FrameLayout.LayoutParams p =
                new FrameLayout.LayoutParams(
                        dp(35),
                        dp(35),
                        gravity
                );

        p.setMargins(
                dp(10),
                dp(10),
                dp(10),
                dp(10)
        );

        parent.addView(label, p);
    }

    private void checkLocationPermission() {

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
                        LOCATION_REQUEST
                );

                return;
            }
        }

        startLocation();
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

        if (requestCode == LOCATION_REQUEST) {

            boolean granted = false;

            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }

            if (granted) {
                startLocation();
            } else {
                statusText.setText(
                        "نحتاج إذن الموقع لحساب اتجاه القبلة"
                );
                locationText.setText(
                        "يمكنك السماح بالموقع من إعدادات التطبيق"
                );
            }
        }
    }

    private void startLocation() {

        try {

            boolean gps =
                    locationManager.isProviderEnabled(
                            LocationManager.GPS_PROVIDER
                    );

            boolean network =
                    locationManager.isProviderEnabled(
                            LocationManager.NETWORK_PROVIDER
                    );

            Location best = null;

            if (gps) {
                best = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                );
            }

            if (best == null && network) {
                best = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER
                );
            }

            if (best != null) {
                updateQibla(best);
            }

            if (gps) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000,
                        5,
                        locationListener,
                        Looper.getMainLooper()
                );
            }

            if (network) {

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000,
                        10,
                        locationListener,
                        Looper.getMainLooper()
                );
            }

            if (!gps && !network) {

                statusText.setText(
                        "خدمة الموقع غير مفعّلة"
                );

                locationText.setText(
                        "فعّل الموقع ثم ارجع إلى التطبيق"
                );
            }

        } catch (SecurityException e) {

            statusText.setText(
                    "تعذّر الوصول إلى موقعك"
            );
        }
    }

    private final LocationListener locationListener =
            new LocationListener() {

                @Override
                public void onLocationChanged(
                        Location location
                ) {
                    updateQibla(location);
                }
            };

    private void updateQibla(Location location) {

        PrayerLocation.save(
                this,
                location.getLatitude(),
                location.getLongitude()
        );

        qiblaBearing = calculateQiblaBearing(
                location.getLatitude(),
                location.getLongitude()
        );

        hasQibla = true;

        angleText.setText(
                String.format(
                        Locale.US,
                        "%.0f°",
                        qiblaBearing
                )
        );

        statusText.setText(
                "اتجاه القبلة محسوب لموقعك"
        );

        locationText.setText(
                String.format(
                        Locale.US,
                        "الموقع: %.4f° ، %.4f°",
                        location.getLatitude(),
                        location.getLongitude()
                )
        );

        updateDirection();
    }

    private double calculateQiblaBearing(
            double latitude,
            double longitude
    ) {

        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(KAABA_LAT);

        double deltaLon =
                Math.toRadians(KAABA_LON - longitude);

        double y =
                Math.sin(deltaLon) * Math.cos(lat2);

        double x =
                Math.cos(lat1) * Math.sin(lat2)
                        -
                        Math.sin(lat1)
                                * Math.cos(lat2)
                                * Math.cos(deltaLon);

        double bearing =
                Math.toDegrees(
                        Math.atan2(y, x)
                );

        return (bearing + 360) % 360;
    }

    private void updateDirection() {

        if (!hasQibla || qiblaNeedle == null) return;

        /*
         * The needle graphic points UP at 0 degrees.
         * Therefore its rotation is the difference between
         * the Qibla bearing and the phone heading.
         */
        float relative =
                normalize(
                        qiblaBearing - currentAzimuth
                );

        /*
         * Keep the pointer visually attached to the Qibla.
         * The heading itself is already smoothed by updateAzimuth().
         */
        qiblaNeedle.setRotation(relative);

        float abs = Math.abs(relative);

        if (abs <= 5f) {

            directionText.setText(
                    "أنت في اتجاه القبلة ✓"
            );

            directionText.setTextColor(
                    EMERALD
            );

            statusText.setText(
                    "ثبّت الهاتف قليلًا"
            );

        } else if (abs <= 15f) {

            directionText.setText(
                    "اقتربت من اتجاه القبلة"
            );

            directionText.setTextColor(
                    GOLD
            );

            statusText.setText(
                    "حرّك الهاتف ببطء نحو المؤشر"
            );

        } else {

            directionText.setText(
                    "اتبع رأس المؤشر للوصول إلى القبلة"
            );

            directionText.setTextColor(
                    TEXT_SECONDARY
            );

            statusText.setText(
                    "الإبرة تشير إلى اتجاه القبلة"
            );
        }
    }

    private float normalize(double value) {

        while (value > 180) value -= 360;
        while (value < -180) value += 360;

        return (float) value;
    }

    @Override
    protected void onResume() {
        super.onResume();

        hasAccelerometer = false;
        hasMagnetic = false;

        if (accelerometerSensor != null) {
            sensorManager.registerListener(
                    this,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_GAME
            );
        }

        if (magneticSensor != null) {
            sensorManager.registerListener(
                    this,
                    magneticSensor,
                    SensorManager.SENSOR_DELAY_GAME
            );
        }

        if (accelerometerSensor == null || magneticSensor == null) {
            statusText.setText("البوصلة غير متاحة على هذا الجهاز");
            locationText.setText("حساسات الاتجاه المطلوبة غير متاحة");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);

        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException ignored) {
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(
                    event.values,
                    0,
                    accelerometerValues,
                    0,
                    3
            );
            hasAccelerometer = true;
        }

        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(
                    event.values,
                    0,
                    magneticValues,
                    0,
                    3
            );
            hasMagnetic = true;
        }

        if (hasAccelerometer && hasMagnetic) {

            boolean success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerValues,
                    magneticValues
            );

            if (success) {
                SensorManager.getOrientation(
                        rotationMatrix,
                        orientation
                );

                float azimuth = (float) Math.toDegrees(
                        orientation[0]
                );

                updateAzimuth(azimuth);

                if (hasQibla) {
                    statusText.setText(
                            String.format(
                                    Locale.US,
                                    "الحساسات تعمل ✓  الاتجاه: %.0f°",
                                    azimuth
                            )
                    );
                }
            } else if (hasQibla) {
                statusText.setText(
                        "الحساسات تعمل لكن تعذر حساب الاتجاه"
                );
            }
        } else if (hasQibla) {
            if (!hasAccelerometer) {
                statusText.setText("في انتظار حساس الحركة...");
            } else {
                statusText.setText("في انتظار حساس المجال المغناطيسي...");
            }
        }
    }

    private void updateAzimuth(float azimuth) {

        azimuth =
                (azimuth + 360f) % 360f;

        float delta =
                normalize(
                        azimuth - currentAzimuth
                );

        currentAzimuth =
                currentAzimuth + delta * 0.18f;

        currentAzimuth =
                (currentAzimuth + 360f) % 360f;

        updateDirection();
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {

        if (sensor.getType()
                == Sensor.TYPE_ROTATION_VECTOR
                && accuracy
                == SensorManager.SENSOR_STATUS_UNRELIABLE) {

            statusText.setText(
                    "البوصلة تحتاج إلى معايرة"
            );

            locationText.setText(
                    "حرّك الهاتف على شكل رقم 8 عدة مرات"
            );
        }
    }

    // =========================================================
    // Small local drawable helpers
    // =========================================================

    private static class GradientDrawableCompat
            extends android.graphics.drawable.GradientDrawable {

        GradientDrawableCompat(
                int color,
                float radius,
                int strokeColor
        ) {
            setColor(color);
            setCornerRadius(radius);

            if (strokeColor != color) {
                setStroke(1, strokeColor);
            }
        }
    }

    private static class QiblaNeedleDrawable
            extends android.graphics.drawable.Drawable {

        private final android.graphics.Paint paint =
                new android.graphics.Paint(
                        android.graphics.Paint.ANTI_ALIAS_FLAG
                );

        private final int width;
        private final int height;
        private final int green;
        private final int gold;

        QiblaNeedleDrawable(
                int width,
                int height,
                int green,
                int gold
        ) {
            this.width = width;
            this.height = height;
            this.green = green;
            this.gold = gold;

            paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
            paint.setStrokeJoin(android.graphics.Paint.Join.ROUND);
        }

        @Override
        public void draw(android.graphics.Canvas canvas) {

            android.graphics.Rect b = getBounds();

            float cx = b.centerX();
            float cy = b.centerY();

            float size = Math.min(b.width(), b.height());

            if (size <= 0) {
                return;
            }

            /*
             * Draw a very clear Qibla pointer.
             * The pointer is deliberately larger than the old version
             * so it remains visible on small phone screens.
             */

            float tipY = cy - size * 0.46f;
            float shoulderY = cy + size * 0.10f;
            float bottomY = cy + size * 0.22f;
            float halfWidth = size * 0.095f;

            // Soft shadow
            paint.setStyle(android.graphics.Paint.Style.FILL);
            paint.setColor(android.graphics.Color.argb(70, 0, 0, 0));
            paint.setShadowLayer(
                    size * 0.035f,
                    0,
                    size * 0.018f,
                    android.graphics.Color.argb(100, 0, 0, 0)
            );

            android.graphics.Path shadow = new android.graphics.Path();
            shadow.moveTo(cx, tipY);
            shadow.lineTo(cx - halfWidth, shoulderY);
            shadow.lineTo(cx, bottomY);
            shadow.lineTo(cx + halfWidth, shoulderY);
            shadow.close();

            canvas.drawPath(shadow, paint);

            paint.clearShadowLayer();

            // Main emerald arrow
            paint.setColor(green);
            paint.setStyle(android.graphics.Paint.Style.FILL);

            android.graphics.Path arrow = new android.graphics.Path();

            arrow.moveTo(cx, tipY);
            arrow.lineTo(cx - halfWidth, shoulderY);
            arrow.lineTo(cx, bottomY);
            arrow.lineTo(cx + halfWidth, shoulderY);
            arrow.close();

            canvas.drawPath(arrow, paint);

            // Gold center highlight
            paint.setColor(gold);

            android.graphics.Path highlight = new android.graphics.Path();
            highlight.moveTo(cx, tipY + size * 0.035f);
            highlight.lineTo(
                    cx - size * 0.028f,
                    shoulderY - size * 0.015f
            );
            highlight.lineTo(cx, shoulderY + size * 0.015f);
            highlight.lineTo(
                    cx + size * 0.028f,
                    shoulderY - size * 0.015f
            );
            highlight.close();

            canvas.drawPath(highlight, paint);

            // Center hub
            paint.setColor(gold);
            canvas.drawCircle(
                    cx,
                    cy,
                    size * 0.055f,
                    paint
            );

            paint.setColor(WHITE);
            canvas.drawCircle(
                    cx,
                    cy,
                    size * 0.025f,
                    paint
            );
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(
                android.graphics.ColorFilter filter
        ) {
            paint.setColorFilter(filter);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return width;
        }

        @Override
        public int getIntrinsicHeight() {
            return height;
        }
    }
}
