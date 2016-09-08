package com.calendaridex.constants;

import com.calendaridex.ApplicationLoader;

import java.util.Locale;

/**
 * Created by Navruz on 17.06.2016.
 */
public class ContextConstants {

    public static final String APP_URL = "http://app.aksanetworks.com/www/api/";
    public static final int UPDATE_PERIOD = 3;

//    public static final Locale Indonesia = new Locale("in", "ID");
//    public static final Locale France = new Locale("fr", "FR");
//    public static final Locale Germany = new Locale("de", "DE");
//    public static final Locale Turkish = new Locale("tr", "TR");
//    public static final Locale Brasil = new Locale("pt", "BR");
//    public static final Locale SINGAPORE = new Locale("en", "SG");
//    public static final Locale UK = new Locale("en", "GB");
//    public static final Locale Croatia = new Locale("hr", "HR");
//    public static final Locale Ukraine = new Locale("uk", "UA");
//    public static final Locale US = new Locale("en", "US");
//    public static final Locale Chile = new Locale("es", "CL");
//    public static final Locale Argentina = new Locale("es", "AR");
//    public static final Locale Canada = new Locale("fr", "CA");
//    public static final Locale Italy = new Locale("it", "IT");
//    public static final Locale Australia = new Locale("en", "AU");
    public static final Locale Malaysia = new Locale("in", "MY");

    public static final Locale currentCountry = Malaysia;
    public static final String PACKAGE_NAME = ApplicationLoader.getAppContext().getPackageName();
    public static final String APP_SHARE_URL = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME;
}
