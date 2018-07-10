package com.tky.lte.util;

import android.util.Log;

/**
 * Created by ZhengTiantian
 *
 * @Date: 2018/8/7
 * @version: 1.0
 * @Description: 日志工具类
 */
public class LogUtils {
    
    public static final boolean DEBUG = true;

    public static void v(String tag, String message) {
        if(DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if(DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if(DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if(DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if(DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable e) {
        if(DEBUG) {
            Log.e(tag, message, e);
        }
    }
}
