package com.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.stickercamera.App;
import java.util.HashSet;
import java.util.Set;

public final class DataUtils {

    private final static String SHARED_PREFERENCE_NAME = "SC_SHARED_PREFERENCE";

    public static <T extends Object> T getObject(Class<T> c, String name) {
        T t = null;
        try {
            String str = DataUtils.getStringPreferences(App.getApp(), name);
            if (StringUtils.isNotBlank(str)) {
                t = (T) JSON.parseObject(str, c);
            }
        } catch (Exception e) {
            Log.e("DataUtils", "解析信息失败");
            DataUtils.setStringPreferences(App.getApp(), name, "");
        }
        return t;
    }

    public static String getStringPreferences(Context context, String name) {
        try {
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(name, "");
        } catch (Exception e) {
            Log.e("datautils", e.getMessage() + "");
            remove(context, name);
        }
        return "";
    }

    public static void setStringPreferences(Context context, String name, String value) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit();
            editor.putString(name, value);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage() + "");
            remove(context, name);
        }
    }
    

    public static boolean getBooleanPreferences(Context context, String name) {
        try {
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getBoolean(name, false);
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
        return false;
    }

    public static void setBooleanPreferences(Context context, String name, boolean value) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit();
            editor.putBoolean(name, value);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
    }

    public static void remove(Context context, String name) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit();
            editor.remove(name);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage() + "");
        }
    }

    public static long getLongPreferences(Context context, String name) {
        try {
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getLong(name, 0);
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
        return 0;
    }

    public static void setLongPreferences(Context context, String name, long value) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE).edit();
            editor.putLong(name, value);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
    }
}
