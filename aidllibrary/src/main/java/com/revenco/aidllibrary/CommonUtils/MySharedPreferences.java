package com.revenco.aidllibrary.CommonUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MySharedPreferences {
    public static int getIntegerPreference(Context context, String tag, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(tag, defaultValue);
    }

    public static void setIntegerPreference(Context context, String tag, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putInt(tag, value);
        editor.commit();
    }

    public static String getStringPreference(Context context, String tag) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(tag, "");
    }

    public static void setStringPreference(Context context, String tag, String value) {
        if (context == null)
            return;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString(tag, value);
        editor.commit();
    }

    public static boolean getBooleanPreference(Context context, String tag, boolean defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(tag, defaultValue);
    }

    public static void setBooleanPreference(Context context, String tag, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putBoolean(tag, value);
        editor.commit();
    }
}
