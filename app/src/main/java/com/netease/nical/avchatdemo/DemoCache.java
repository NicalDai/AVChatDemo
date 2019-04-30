package com.netease.nical.avchatdemo;

import android.content.Context;

public class DemoCache {

    private static Context context;
    private static String  account;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DemoCache.context = context;
    }
}
