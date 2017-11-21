package com.android.sanjayrajput.phone_music_cloud_syncer;

import java.text.DecimalFormat;

/**
 * Created by sanjay.rajput on 02/10/17.
 */
public class Utils {

    private static final DecimalFormat df = new DecimalFormat(".##");

    public static String getFileSize(long length) {
        StringBuilder size = new StringBuilder();
        if (length > 1024) {    //kb
            double s = length / 1024.0;
            if (s > 1024) {     //mb
                s = s / 1024.0;
                if (s > 1024) { //gb
                    s = s / 1024.0;
                    size.append(df.format(s)).append(" GB");
                } else {
                    size.append(df.format(s)).append(" MB");
                }
            } else {
                size.append(df.format(s)).append(" KB");
            }
        }
        return size.toString();
    }

}
