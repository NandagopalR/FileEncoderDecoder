package com.nanda.fileencoder.utils;

import android.util.Base64;

public class EncoderUtils {

    public static String encode(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT).replaceAll("\n", "");
    }

    public static byte[] decode(String encodedString) {
        return Base64.decode(encodedString.getBytes(), Base64.DEFAULT);
    }

}
