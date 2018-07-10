package com.tky.lte.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.tky.lte.LTEApp;

/**
 * Created by ZhengTiantian
 * @Date: 2018/8/7
 * @version: 1.0
 * @Description: 网络
 */
public class NetUtils {

    static int NO_NETWORK = 0;
    static int NETWORK_WIFI = 1;
    static int NETWORK_MOBILE = 2;

    public static int checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) LTEApp.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi != null && wifi.isConnected()) {
            return NETWORK_WIFI;
        }
        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile != null && mobile.isConnected()) {
            return NETWORK_MOBILE;
        }
        return NO_NETWORK;
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */

    public static boolean isNetworkConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) LTEApp.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
