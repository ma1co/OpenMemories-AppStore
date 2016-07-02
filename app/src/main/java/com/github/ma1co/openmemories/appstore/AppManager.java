package com.github.ma1co.openmemories.appstore;

import android.content.pm.PackageManager;

public class AppManager {
    public enum Status {
        INSTALLED("Installed"),
        UPDATE_AVAILABLE("Update"),
        NOT_INSTALLED("Install");

        private String label;

        Status(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static String getInstalledVersion(PackageManager pm, String pkg) {
        try {
            return pm.getPackageInfo(pkg, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static Status getStatus(PackageManager pm, App app) {
        String installedVersion = getInstalledVersion(pm, app.id);
        if (installedVersion != null) {
            if (installedVersion.equals(app.releaseVersion))
                return Status.INSTALLED;
            else
                return Status.UPDATE_AVAILABLE;
        } else {
            return Status.NOT_INSTALLED;
        }
    }
}
