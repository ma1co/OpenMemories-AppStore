package com.github.ma1co.openmemories.appstore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Http {
    public static HttpEntity request(HttpUriRequest request) throws IOException {
        request.setHeader("User-Agent", "openmemories.appstore");
        HttpResponse response = new DefaultHttpClient().execute(request);
        return response.getEntity();
    }

    public static HttpEntity get(String url) throws IOException {
        return request(new HttpGet(url));
    }

    public static String getString(String url) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        get(url).writeTo(bytes);
        return bytes.toString();
    }

    public static HttpEntity post(String url, JSONObject data) throws IOException {
        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(data.toString()));
        return request(request);
    }
}
