package com.github.ma1co.openmemories.appstore;

import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

public class ExternalStoreApi {
    public static final String name = "APKPure";
    public static final String base = "https://api.pureapk.com/m/v1";

    public static App[] findApps(String query, int limit) throws IOException, JSONException {
        if (query == null || query.isEmpty())
            throw new IllegalArgumentException("Query is empty");

        String params = "key=" + URLEncoder.encode(query) + "&start=0&limit=" + limit + "&hl=en";
        Http.Response response = Http.get(base + "/search/query?" + params);
        String json = new String(response.getContentBytes());
        response.close();

        JSONObject result = new JSONObject(json);
        if (!"SUCCESS".equals(result.getString("error")))
            throw new IOException("Api error");

        JSONArray array = result.getJSONArray("result");
        App[] apps = new App[array.length()];
        for (int i = 0; i < apps.length; i++) {
            JSONObject a = array.getJSONObject(i);
            JSONObject r = a.optJSONObject("asset");
            if (r != null && !"APK".equals(r.getString("type")))
                r = null;
            apps[i] = new App(
                    a.getString("package_name"),
                    a.getString("developer"),
                    a.getString("title"),
                    (!a.isNull("description_short") ? Html.fromHtml(a.getString("description_short")).toString() : ""),
                    a.getInt("download_count"),
                    r != null ? a.getString("version_name") : "0",
                    r != null ? (!a.isNull("whatsnew") ? Html.fromHtml(a.getString("whatsnew")).toString() : "") : "Not available",
                    r != null ? r.getString("url") : null
            );
        }
        return apps;
    }
}
