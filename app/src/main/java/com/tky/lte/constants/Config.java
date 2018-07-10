package com.tky.lte.constants;

import android.os.Environment;

/**
 * @author ZhengTiantian
 * @version V1.0
 * @Description: 配置
 */
public class Config {

    public static final String BASE_SD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lte";
    public static final String KEY_CRASH = "crash";
    public static final String CRASH_PATH = BASE_SD_DIR + "/crash/";
    /**通话状态*/
    public static final String Calling = "Calling";
    /**个呼状态*/
    public static final String GH = "gh";
    /**组呼状态*/
    public static final String ZH = "zh";
    /**空闲状态*/
    public static final String KX = "kx";
    /**占用状态*/
    public static final String ZY = "zy";
    /**失败状态*/
    public static final String SB = "sb";


    /**上线状态*/
    public static final int OnlineCode = 101;
    /**注册状态*/
    public static final int FunctionRegisterCode = 102;
    /**通话状态*/
    public static final int CallingCode = 103;
    /**注销状态*/
    public static final int CancelCode = 104;
    /**解组状态*/
    public static final int JieZuCode = 105;
    /**ptt状态*/
    public static final int PttStateCode = 106;

    /**列表更新状态*/
    public static final int LszhUpdateCode = 107;//

    /**功能号码查询*/
    public static final int QueryFunctionRegisterIsUsedCode = 108;//

    /**功能号码强制注销*/
    public static final int QueryFunctionForceUnRegisterIsUsedCode = 109;//

    /**主ip*/
    public static final String ServerZip = "ServerZip";
    /**备ip*/
    public static final String ServerBip = "ServerZip";
}
