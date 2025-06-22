package com.vnpt.prod.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JsonUtil {
    private static final SimpleDateFormat isoFormat;

    static {
        isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String toIsoDate(Date date) {
        return isoFormat.format(date);
    }
}
