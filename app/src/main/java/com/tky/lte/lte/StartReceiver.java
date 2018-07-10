package com.tky.lte.lte;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import com.tky.lte.MainActivity;
import com.tky.lte.R;
import com.tky.lte.util.ToastUtil;

public class StartReceiver extends BroadcastReceiver {

    public static KeyguardManager mKeyguardManager;
    public static KeyguardLock kl;
    public static KeyguardLock kll;
    public static Thread x;// 保持通话时恒亮线程
    public static boolean activityCreated = false;// 主界面是否启动
    public static PowerManager.WakeLock wl;// 界面恒亮
    public static PowerManager.WakeLock wll;// 界面恒亮
    static android.os.Vibrator vibrator;// 震动
    public static Context mContext;

    public static volatile boolean shutdownFlag = false;// true:关机,false:开机
    public final static int MWI_NOTIFICATION = 1;
    public final static int CALL_NOTIFICATION = 2;// 呼叫
    public final static int MISSED_CALL_NOTIFICATION = 3;
    public final static int AUTO_ANSWER_NOTIFICATION = 4;//自动接听
    public final static int REGISTER_NOTIFICATION = 5;// 注册

    public static String location = ""; // 位置信息

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (mContext == null)
            mContext = context;

        String intentAction = intent.getAction();

        String caller = intent.getStringExtra("caller");
        String cellId = intent.getStringExtra("cellId");

        startKeppBrightByCallThd();// 启动保持屏幕恒亮

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent ootStartIntent = new Intent(context,MainActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }
        else if (intentAction.equals(Intent.ACTION_SHUTDOWN) || intentAction.equals(Intent.ACTION_REBOOT))  {// 关机
//            GlobalActivity mAnGlobal = GlobalActivity.getInstance();
//            int ret = mAnGlobal.getLteHandle().OffLine();
//            if (ret == 0)  {// 注销成功
//               ToastUtil.showShortToastSafe("注销成功");
//            }
//            else
//            {
//                StartReceiver.onText(StartReceiver.REGISTER_NOTIFICATION, null, 0, 0);
//            }
            shutdownFlag = true;
        }
    }


    /**
     * 启动保持屏幕恒亮
     */
    private void startKeppBrightByCallThd()
    {
        if (x == null)
        {
            x = new Thread()
            {
                public void run()
                {
                    for (;;)
                    {
                        if (kll == null)
                        {
                            try
                            {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e)
                            {
                                LogInstance.debug(GlobalPara.Tky, "Receiver.onReceive.sleep:"+e.getMessage());
                            }
                            continue;
                        }
                        else
                        {
                            if (activityCreated && mContext != null && isApplicationBroughtToBackground(mContext))
                            {
                                activityCreated = false;
                                try
                                {
                                    if (kll != null)
                                    {
                                        Log.d("showPoxDroid", "reenableKeyguard");
                                        kll.reenableKeyguard();
                                        kll = null;
                                    }
                                }
                                catch (Exception ex)
                                {
                                    Log.e("reenableKeyguard", ex.getMessage());
                                }
                                try
                                {
                                    if (wll != null)
                                    {
                                        wll.release();
                                        wll = null;
                                    }
                                }
                                catch (Exception ex)
                                {
                                    Log.e("release", ex.getMessage());
                                }
                            }
                            try
                            {
                                Thread.sleep(200);
                            }
                            catch (InterruptedException e)
                            {
                                Log.e("sleep", e.getMessage());
                            }
                        }
                    }
                }
            };
            x.start();
        }
    }

    /**
     * 判断程序界面是否操作系统当前显示界面
     */
    public static boolean isApplicationBroughtToBackground(final Context context)
    {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty())
        {
            ComponentName topActivity = tasks.get(0).topActivity;

            if (!topActivity.getPackageName().equals(context.getPackageName()))
            {
                return true;
            }
        }
        return false;

    }

    /**
     * Show notification on the title bar.
     *
     * @param type
     * @param text
     * @param mInCallResId
     * @param base
     */
    public static void onText(int type, String text, int mInCallResId, long base)
    {

    }


}  
