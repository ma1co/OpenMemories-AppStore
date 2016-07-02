package com.github.ma1co.openmemories.appstore;

import android.os.Build;

import java.io.File;

public class Environment {
    public static boolean isEmulator() {
        return Build.BRAND.contains("generic") && Build.DEVICE.contains("generic");
    }

    public static boolean isCamera() {
        return "sony".equals(Build.BRAND) && "ScalarA".equals(Build.MODEL) && "dslr-diadem".equals(Build.DEVICE);
    }

    public static File getTempDir() {
        return Environment.isCamera() ? new File("/data/local/tmp") : android.os.Environment.getExternalStorageDirectory();
    }
}
