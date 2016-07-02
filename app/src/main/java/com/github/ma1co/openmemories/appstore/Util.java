package com.github.ma1co.openmemories.appstore;

public class Util {
    public static byte[] fromHex(String hex) {
        byte[] data = new byte[hex.length() / 2];
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*(i+1)), 16);
        return data;
    }
}
