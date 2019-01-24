package com.mahainc.videoplayer.utils;

import android.annotation.SuppressLint;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by cr4ck3r
 * Date: 12/6/18
 * Owner: Raisul Islam
 * Copyright (c) 2018 . All rights reserved.
 */
public class MyUtils {
    @SuppressLint("DefaultLocale")
    public static String milisecondToHour(Long millis) {
        return String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public static String getParentDirPath(String fileOrDirPath) {
        boolean endsWithSlash = fileOrDirPath.endsWith(File.separator);
        return fileOrDirPath.substring(0, fileOrDirPath.lastIndexOf(File.separatorChar,
                endsWithSlash ? fileOrDirPath.length() - 2 : fileOrDirPath.length() - 1));
    }
}
