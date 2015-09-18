package com.mzywx.liao.android.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    final static DateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static long getLocalServerTimeDiff() {
        return localServerTimeDiff;
    }

    public static void setLocalServerTimeDiff(long localServerTimeDiff) {
        TimeUtil.localServerTimeDiff = localServerTimeDiff;
    }

    public static long localServerTimeDiff;

    public static Date getAdjustDateTime(Date date) {
        return new Date(getAdjustDateTimeVal(date.getTime()));
    }

    public static long getAdjustDateTimeVal(long datetime) {
        return datetime - localServerTimeDiff;
    }

    public static Date toUTC(Date date) {
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getRawOffset();
        long dateVal = date.getTime();
        Date utcDate = new Date(dateVal - offset);
        return utcDate;

    }

    public static Date getCurrentTimeInUTC() {
        return toUTC(new Date());
    }

    //主要用于把以固定格式存储到数据库的UTC日期转换成为日期对象，openfire 服务器IQ时间也进行同样的操作！
    public static Date getUtcDateTime(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) {
                return null;
            }
            if (!dateString.contains("-"))
                return null;
            SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleFormatter.parse(dateString);
            //All date in UTC timezone
            return date;
        } catch (Exception ex) {
            return null;
        }
    }

    //仅仅用于把已经转化成为UTC的日期以固定格式存储到数据库
    public static String formatToUtcDateTime(Date date) {
        try {
            if (date == null) {
                return null;
            }
            //All date in UTC timezone
            return simpleFormatter.format(date);
        } catch (Exception ex) {
            return null;
        }
    }
}
