package com.tky.lte.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.tky.lte.LTEApp;

/**
 * Created by ZhengTiantian
 *
 * @Date: 2018/8/7
 * @version: 1.0
 * @Description: SharedPreferences 存储类
 */
public class PreferenceUtil {

    private Context mContext;
    private static PreferenceUtil shareData;
    private SharedPreferences sharedPreferences;

    public static PreferenceUtil getInstance() {
        if (shareData == null) {
            shareData = new PreferenceUtil(LTEApp.getInstance());
        }
        return shareData;
    }

    private PreferenceUtil(Context context) {
        this.mContext = context;
        initPreferences();
    }

    public void initPreferences() {
        if (this.sharedPreferences == null)
            this.sharedPreferences = mContext.getSharedPreferences(
                    "fucai", Context.MODE_PRIVATE);
    }

    public void putPreferences(String paramString, float paramFloat) {
        Editor localEditor = this.sharedPreferences.edit();
        localEditor.putFloat(paramString, paramFloat);
        localEditor.commit();
    }

    public void putPreferences(String paramString, int paramInt) {
        Editor localEditor = this.sharedPreferences.edit();
        localEditor.putInt(paramString, paramInt);
        localEditor.commit();
    }

    public void putPreferences(String paramString, long paramLong) {
        Editor localEditor = this.sharedPreferences.edit();
        localEditor.putLong(paramString, paramLong);
        localEditor.commit();
    }

    public void putPreferences(String paramString1, String paramString2) {
        if ((paramString1 == null) || (paramString2 == null))
            return;
        Editor localEditor = this.sharedPreferences.edit();
        localEditor.putString(paramString1, paramString2);
        localEditor.commit();
    }

    public void putPreferences(String paramString, boolean paramBoolean) {
        Editor localEditor = this.sharedPreferences.edit();
        localEditor.putBoolean(paramString, paramBoolean);
        localEditor.commit();
    }

    public float getPreferences(String paramString, float paramFloat) {
        return this.sharedPreferences.getFloat(paramString, paramFloat);
    }

    public int getPreferences(String paramString, int paramInt) {
        return this.sharedPreferences.getInt(paramString, paramInt);
    }

    public long getPreferences(String paramString, long paramLong) {
        return this.sharedPreferences.getLong(paramString, paramLong);
    }

    public String getPreferences(String paramString1, String paramString2) {
        return this.sharedPreferences.getString(paramString1, paramString2);
    }

    public boolean getPreferences(String paramString, boolean paramBoolean) {
        return this.sharedPreferences.getBoolean(paramString, paramBoolean);
    }

    public void removePreferences(String paramString) {
        Editor localEditor = this.sharedPreferences.edit();
        localEditor.remove(paramString);
        localEditor.commit();
    }

    public Editor getPreferences() {
        Editor localEditor = this.sharedPreferences.edit();
        return localEditor;
    }
}