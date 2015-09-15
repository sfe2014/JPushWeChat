package com.mzywx.liao.android.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeModule {
    
    private static SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm:ss",Locale.CHINA);
    
    public static String getTimeFormat() {
        Calendar calendar = Calendar.getInstance();
        return mFormat.format(calendar.getTime());
    }
}
