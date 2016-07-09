package com.github.ma1co.openmemories.appstore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    public static class Response implements Closeable {
        private final HttpURLConnection connection;

        public Response(HttpURLConnection connection) {
            this.connection = connection;
        }

        public int getContentLength() {
            return connection.getContentLength();
        }

        public InputStream getContent() throws IOException {
            return connection.getInputStream();
        }

        public byte[] getContentBytes() throws IOException {
            InputStream is = getContent();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            while ((n = is.read(buffer)) != -1)
                os.write(buffer, 0, n);
            is.close();
            return os.toByteArray();
        }

        @Override
        public void close() {
            connection.disconnect();
        }
    }

    private static HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "openmemories.appstore");
        return connection;
    }

    private static Response toResponse(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException("HTTP error " + connection.getResponseCode() + " (" + connection.getResponseMessage() + ")");
        return new Response(connection);
    }

    public static Response get(String url) throws IOException {
        return toResponse(createConnection(url));
    }

    public static Response post(String url, JSONObject data) throws IOException {
        HttpURLConnection connection = createConnection(url);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(data.toString().getBytes());
        os.close();
        return toResponse(connection);
    }
}
