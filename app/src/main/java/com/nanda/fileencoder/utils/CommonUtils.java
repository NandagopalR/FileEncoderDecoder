package com.nanda.fileencoder.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class CommonUtils {

    public static ProgressDialog showProgressDialog(Context context, String type) {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage(type);
        pDialog.setCancelable(false);
        return pDialog;
    }

}
