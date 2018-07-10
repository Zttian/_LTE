package com.tky.lte.entity;


import com.tky.lte.constants.Config;
import com.tky.lte.util.PreferenceUtil;

/**
 * Created by ZhengTiantian
 *
 * @Date: 2017/8/7
 * @version: 1.0
 * @Description: 用户信息表
 */
public class UserInfo {

    private static String severZIP;
    private static String severBIP;


    public static String getSeverZIP() {
        return severZIP;
    }

    public static String getSeverBIP() {
        return severBIP;
    }

    static {
        init();
    }


    private static void init() {
        severZIP = PreferenceUtil.getInstance().getPreferences(Config.ServerZip, "");
        severBIP = PreferenceUtil.getInstance().getPreferences(Config.ServerBip,"");
    }

    /**
     * 刷新数据 （获取）
     */
    public static void refresh() {
        init();
    }
    /**
     * 退出登录 清除数据
     */
    public static void logout() {
        PreferenceUtil.getInstance().putPreferences(Config.ServerZip, "");
        PreferenceUtil.getInstance().putPreferences(Config.ServerBip,"");
        refresh();
    }
}

