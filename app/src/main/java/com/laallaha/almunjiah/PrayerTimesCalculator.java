package com.laallaha.almunjiah;

import java.util.Calendar;

public class PrayerTimesCalculator {

    public static class Times {
        public double fajr;
        public double dhuhr;
        public double asr;
        public double maghrib;
        public double isha;
    }

    // القاهرة تقريبًا
    private static final double LATITUDE = 30.0444;
    private static final double LONGITUDE = 31.2357;

    public static Times calculate(
            Calendar date,
            double latitude,
            double longitude
    ) {
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);

        double declination = solarDeclination(dayOfYear);
        double equation = equationOfTime(dayOfYear);

        double timezone =
                date.getTimeZone().getOffset(date.getTimeInMillis())
                / 3600000.0;

        double noon =
                12.0
                + timezone
                - longitude / 15.0
                - equation / 60.0;

        Times t = new Times();

        t.fajr =
                noon
                - hourAngle(18.0, declination) / 15.0;

        t.dhuhr = noon;

        t.asr =
                noon
                + hourAngleAsr(1.0, declination) / 15.0;

        t.maghrib =
                noon
                + hourAngle(0.833, declination) / 15.0;

        t.isha =
                noon
                + hourAngle(17.0, declination) / 15.0;

        return t;
    }

    public static Times calculate(Calendar date) {
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
        int year = date.get(Calendar.YEAR);

        double declination = solarDeclination(dayOfYear);
        double equation = equationOfTime(dayOfYear);

        double timezone =
                date.getTimeZone().getOffset(date.getTimeInMillis())
                / 3600000.0;

        double noon = 12.0
                + timezone
                - LONGITUDE / 15.0
                - equation / 60.0;

        Times t = new Times();

        t.fajr = noon - hourAngle(18.0, declination) / 15.0;
        t.dhuhr = noon;
        t.asr = noon + hourAngleAsr(1.0, declination) / 15.0;
        t.maghrib = noon + hourAngle(0.833, declination) / 15.0;
        t.isha = noon + hourAngle(17.0, declination) / 15.0;

        return t;
    }

    private static double solarDeclination(int day) {
        double gamma = 2.0 * Math.PI / 365.0 * (day - 1);

        return Math.toDegrees(
                0.006918
                - 0.399912 * Math.cos(gamma)
                + 0.070257 * Math.sin(gamma)
                - 0.006758 * Math.cos(2 * gamma)
                + 0.000907 * Math.sin(2 * gamma)
                - 0.002697 * Math.cos(3 * gamma)
                + 0.00148 * Math.sin(3 * gamma)
        );
    }

    private static double equationOfTime(int day) {
        double gamma = 2.0 * Math.PI / 365.0 * (day - 1);

        return 229.18 * (
                0.000075
                + 0.001868 * Math.cos(gamma)
                - 0.032077 * Math.sin(gamma)
                - 0.014615 * Math.cos(2 * gamma)
                - 0.040849 * Math.sin(2 * gamma)
        );
    }

    private static double hourAngle(double angle, double declination) {
        double lat = Math.toRadians(LATITUDE);
        double dec = Math.toRadians(declination);
        double altitude = Math.toRadians(-angle);

        double cosH =
                (Math.sin(altitude) - Math.sin(lat) * Math.sin(dec))
                / (Math.cos(lat) * Math.cos(dec));

        cosH = Math.max(-1.0, Math.min(1.0, cosH));

        return Math.toDegrees(Math.acos(cosH));
    }

    private static double hourAngleAsr(double factor, double declination) {
        double lat = Math.toRadians(LATITUDE);
        double dec = Math.toRadians(declination);

        double angle = -Math.toDegrees(
                Math.atan(1.0 / (factor + Math.tan(Math.abs(lat - dec))))
        );

        return hourAngle(angle, declination);
    }

    public static String format(double time) {
        time = ((time % 24) + 24) % 24;

        int hour = (int) time;
        int minute = (int) Math.round((time - hour) * 60);

        if (minute >= 60) {
            minute = 0;
            hour++;
        }

        hour %= 24;

        String suffix = hour >= 12 ? "م" : "ص";
        int displayHour = hour % 12;

        if (displayHour == 0) {
            displayHour = 12;
        }

        return String.format(
                java.util.Locale.US,
                "%02d:%02d %s",
                displayHour,
                minute,
                suffix
        );
    }

    public static long toMillis(double time) {
        return toMillis(time, Calendar.getInstance());
    }

    public static long toMillis(
            double time,
            Calendar date
    ) {
        time = ((time % 24) + 24) % 24;

        int hour = (int) time;
        int minute = (int) Math.round(
                (time - hour) * 60
        );

        if (minute >= 60) {
            minute = 0;
            hour++;
        }

        Calendar c = (Calendar) date.clone();

        c.set(Calendar.HOUR_OF_DAY, hour % 24);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }
}
