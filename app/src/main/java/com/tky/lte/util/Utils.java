package com.tky.lte.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.github.clans.fab.FloatingActionMenu;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;
import com.tky.lte.lte.LogParam;
import com.tky.lte.lte.ParamOperation;
import com.tky.lte.widget.MyAlertDialog;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttz on 2017/8/17.
 * 初始化相关
 */
public class Utils {

    private static Context context;
    public static ParamOperation mParamOperation = null;
    private static final int COLOR = 0xFF323334;


    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    public static boolean CheckParam(Activity ac)    {
        mParamOperation = ParamOperation.getInstance();
        try        {
            String strPath = null;
            File aFile = null;
            strPath = LogParam.LOG_FILEPATH;//
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                // *
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
                // */
            }
            strPath = LogParam.strShuntingOperationPath;//"/LocomotiveMain/ShuntingOperation/"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
            }
            strPath = LogParam.strDispatchCommandPath;//"/LocomotiveMain/DispatchCommand/"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
            }
            strPath = LogParam.strAdvanceNoticePath;//"/LocomotiveMain/AdvanceNotice/"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
            }
            strPath = LogParam.strOtherCommandPath;//"/LocomotiveMain/OtherRecord/"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
            }
            strPath = LogParam.strOtherCommandPath;//"/LocomotiveMain/OtherCommand/"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
            }
            strPath = LogParam.strLossPackagePath;//"/LocomotiveMain/LossPackage/"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
            }
            strPath = LogParam.strRuningTokenPath;//"/LocomotiveMain/RuningToken/"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                if (!aFile.mkdirs())// 目录创建未成功
                {
                    LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                    ac.finish();
                }
                else
                {
                }
            }
            else
            {
                if (!aFile.isDirectory())
                {
                    if (!aFile.delete())
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't delete " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    if (!aFile.mkdirs())// 目录创建未成功
                    {
                        LogInstance.error(GlobalPara.Tky, "68." + "" + ": can't creat " + strPath + " dir, so finish");
                        ac.finish();
                    }
                    else
                    {
                    }
                }
            }


            strPath = LogParam.LOG_FILEPATH + LogParam.LOG_NAME ;//"/LocomotiveMain/cir.log"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                RandomAccessFile file = new RandomAccessFile(aFile, "rw");
                file.close();
                file = null;
            }

            strPath = LogParam.LOG_FILEPATH + LogParam.LOG_NAME_POC ;//"/LocomotiveMain/pocdroid.log"
            aFile = new File(strPath);
            if (!aFile.exists())
            {
                RandomAccessFile file = new RandomAccessFile(aFile, "rw");
                file.close();
                file = null;
            }

            boolean bHandleParamFile = mParamOperation.HandleParamFile();
            if (!bHandleParamFile)
            {
                LogInstance.error(GlobalPara.Tky,  "69." + "" + ": can't handle param file");
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage() != "")
            {
                LogInstance.exception(GlobalPara.Tky, e);
            }
            LogInstance.error(GlobalPara.Tky, "0." + "" + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
                    + Thread.currentThread().getStackTrace()[2].getLineNumber());
            return false;
        }
        return true;
    }


    /**
     * 播放音频文件
     * @param context
     * @param mediaPlayer 播放器
     * @param fileName 文件名
     */
    public static void setVoicePrompt(Context context,MediaPlayer mediaPlayer,String fileName){
        AssetManager assetManager = context.getApplicationContext().getAssets();
        try {
            AssetFileDescriptor fd = assetManager.openFd(fileName);
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 检查屏幕是否变暗
     * @param context
     */
    public static void CheckScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);//获取电源管理器对象
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            LogInstance.error(GlobalPara.Tky, "CheckScreen be called");
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");//获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            wl.acquire(); //点亮屏幕
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE); //得到键盘锁管理器对象
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");   //参数是LogCat里用的Tag
            kl.disableKeyguard(); //解锁
        }
    }

    /**
     * 判断某个Activity 界面是否在前台
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public static boolean  isForeground(Context context, String className) {
        boolean isClass = false;
        if (context == null || TextUtils.isEmpty(className)) {
            isClass = false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                isClass = true;
            }
        }
        return isClass;
    }

    /**
     * 选择器
     * @param context
     * @param list
     * @param tvOption
     */
    public static void initOptionsPickerView(Context context, final List<String> list, final TextView tvOption){
        OptionsPickerView optionPicker = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                tvOption.setText(list.get(options1));
            }
        }).setContentTextSize(18).setSubmitText("确定").setCancelText("取消")
                .setSubmitColor(COLOR).setCancelColor(COLOR).setLineSpacingMultiplier(2.0f).build();
        optionPicker.setPicker(list);
        if (optionPicker != null){
            optionPicker.show();
        }
    }
    /**
     * 初始化float
     * @param menuFloat
     */
    public static void initFloatMenu(final FloatingActionMenu menuFloat){
        List<FloatingActionMenu> menus = new ArrayList<>();
        Handler mUiHandler = new Handler();
        menuFloat.hideMenuButton(false);
        menus.add(menuFloat);
        int delay = 400;
        for (final FloatingActionMenu menu : menus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }
        menuFloat.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFloat.toggle(true);
            }
        });
        menuFloat.setClosedOnTouchOutside(true);
    }


    public static void initAlertDialog(final Activity activity,String gnhm,String yhhm){
        MyAlertDialog dialog = new MyAlertDialog(activity).builder();
        dialog.setCancelable(false);
        dialog.setTitle("功能号码：" + gnhm);
        dialog.setMsg("用户号码：" + yhhm);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        }).show();
    }

}
