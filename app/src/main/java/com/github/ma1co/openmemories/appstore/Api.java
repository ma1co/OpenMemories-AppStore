package com.github.ma1co.openmemories.appstore;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.sony.scalar.sysutil.ScalarProperties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Api {
    public static final String base = "https://sony-pmca.appspot.com";

    public static App[] loadApps() throws IOException, JSONException {
        Http.Response response = Http.get(base + "/api/apps");
        String json = new String(response.getContentBytes());
        response.close();
        JSONArray array = new JSONArray(json);
        App[] apps = new App[array.length()];
        for (int i = 0; i < apps.length; i++) {
            JSONObject a = array.getJSONObject(i);
            JSONObject r = a.optJSONObject("release");
            apps[i] = new App(
                    a.getString("package"),
                    a.getString("author"),
                    a.getString("name"),
                    a.getString("desc").replace("\r\n", "\n"),
                    a.getInt("rank"),
                    r != null ? r.getString("version") : "0",
                    r != null ? r.getString("desc").replace("\r\n", "\n") : "Not available",
                    r != null ? r.getString("url") : null
            );
        }
        return apps;
    }

    public static void sendStats(PackageManager pm) throws IOException, JSONException {
        Http.Response response = Http.post(base + "/api/stats", buildStatData(pm));
        response.close();
    }

    public static JSONObject buildStatData(PackageManager pm) throws JSONException {
        JSONObject device = new JSONObject();
        device.put("name", ScalarProperties.getString(ScalarProperties.PROP_MODEL_NAME));
        device.put("productcode", ScalarProperties.getString(ScalarProperties.PROP_MODEL_CODE));
        device.put("deviceid", ScalarProperties.getString(ScalarProperties.PROP_MODEL_SERIAL_CODE));
        device.put("fwversion", ScalarProperties.getFirmwareVersion());

        JSONArray apps = new JSONArray();
        for (PackageInfo info : pm.getInstalledPackages(0)) {
            JSONObject app = new JSONObject();
            app.put("name", info.packageName);
            app.put("version", info.versionName);
            apps.put(app);
        }

        JSONObject stats = new JSONObject();
        stats.put("deviceinfo", device);
        stats.put("applications", apps);
        return stats;
    }
}
