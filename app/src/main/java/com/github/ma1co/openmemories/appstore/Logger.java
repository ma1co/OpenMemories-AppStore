package com.github.ma1co.openmemories.appstore;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
    public static File getFile() {
        return new File(Environment.getExternalStorageDirectory(), "APPSTORE.LOG");
    }

    protected static void log(String msg) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(), true));
            writer.append(msg);
            writer.newLine();
            writer.close();
        } catch (IOException e) {}
    }

    protected static void log(String type, String msg) {
        log("[" + type + "] " + msg);
    }

    public static void info(String msg) {
        Log.i("Logger", msg);
        log("INFO", msg);
    }

    public static void error(String msg) {
        Log.e("Logger", msg);
        log("ERROR", msg);
    }

    public static void error(String msg, Throwable exp) {
        StringWriter sw = new StringWriter();
        if (!msg.isEmpty()) {
            sw.append(msg);
            sw.append(": ");
        }
        exp.printStackTrace(new PrintWriter(sw));
        error(sw.toString().trim());
    }
}
