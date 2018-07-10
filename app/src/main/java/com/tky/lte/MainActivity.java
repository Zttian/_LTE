package com.tky.lte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.location.Location;//
//import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.constants.Config;
import com.tky.lte.lte.AppExcepitonHandler;
import com.tky.lte.lte.ConfigHelper;
import com.tky.lte.lte.DataProcesser;
import com.tky.lte.lte.GlobalFunc;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;
import com.tky.lte.lte.LogParam;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.lte.ParamOperation;
import com.tky.lte.lte.TrainState;
import com.tky.lte.ui.activity.DialingActivity;
import com.tky.lte.ui.entity.TabEntity;
import com.tky.lte.ui.event.FunctionRegisterResponseBean;
import com.tky.lte.ui.event.MainBean;
import com.tky.lte.ui.fragment.AddressBookFragment;
import com.tky.lte.ui.fragment.CallFragment2;
import com.tky.lte.ui.fragment.FunctionNumberFragment;
import com.tky.lte.ui.fragment.HomePageFragment;
import com.tky.lte.ui.fragment.SettingFragment;
import com.tky.lte.util.ActivityManagerUtils;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.util.Utils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import butterknife.BindView;

/**
 * 主页面
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.tabLayout)
    CommonTabLayout tabLayout;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    private long mExitTime;

    private String[] mTitles = {"功能号", "呼叫", "首页", "通讯录", "设置"};
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private int[] mIconUnSelectIds = {R.drawable.ic_call_note_white_24dp, R.drawable.ic_call_24dp, R.drawable.ic_content_copy_24dp, R.drawable.ic_people_24dp, R.drawable.ic_settings_white_24dp};
    private int[] mIconSelectIds = {R.drawable.ic_call_note_white_24dp, R.drawable.ic_call_24dp, R.drawable.ic_content_copy_24dp, R.drawable.ic_people_24dp, R.drawable.ic_settings_white_24dp};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();


    public LteHandle mLteHandle = null; // 处理LTE,相当于前GRPS模块+话音单元
    public ParamOperation mParamOperation;
    public TrainState mTrainState;
    public DataProcesser mDataProcesser = null;

    private TelephonyManager tm;
    //private LocationManager lm;//
    private HomeWatcherReceiver mHomeWatcherReceiver = null;

    private void registerReceiver() {
        mHomeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeWatcherReceiver, filter);
        IntentFilter filter2 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mHomeWatcherReceiver, filter2);
    }



    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ActivityManagerUtils.getInstance().addActivity(MainActivity.this);
        fragments.add(new FunctionNumberFragment());
        fragments.add(new CallFragment2());
        fragments.add(new HomePageFragment());
        fragments.add(new AddressBookFragment());
        fragments.add(new SettingFragment());
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }
        tabLayout.setTabData(mTabEntities, this, R.id.frameLayout, fragments);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        tabLayout.setCurrentTab(2);

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);//

        InitSoft();
        mTrainState = TrainState.getInstance();
        mParamOperation = ParamOperation.getInstance();
        mDataProcesser = new DataProcesser();
        mLteHandle = LteHandle.getInstance();
        mLteHandle.SetApplication(LTEApp.getInstance());
        mDataProcesser.mLteHandle = mLteHandle;

        // 检查参数
        if (!Utils.CheckParam(MainActivity.this)) {
            ToastUtil.showShortToastSafe("检查失败");
        }

        GlobalPara.strPhoneId = tm.getLine1Number();
        GlobalPara.strPhoneImsi = tm.getSubscriberId();

        if(GlobalPara.strPhoneId == null)//
            GlobalPara.strPhoneId = "";
        if(GlobalPara.strPhoneImsi == null)
            GlobalPara.strPhoneImsi = "";
        LogInstance.debug(GlobalPara.Tky, "Phone_Number=" + GlobalPara.strPhoneId + ", Phone_Imsi=" + GlobalPara.strPhoneImsi);//  error->debug

        tvAddress.setText("本机号码：" + GlobalPara.strPhoneId);//

        mLteHandle.initDataProcesser();
        registerReceiver();
        bRunStateThread = true;// 程序运行状态线程
        mRunStateThread = new RunStateThread();
        mRunStateThread.start();
    }

    public void InitSoft() {

        GlobalPara.cStartCalendar = Calendar.getInstance();
        boolean bSdcardExist = false;
        File fTmpLogFilePath = new File("/mnt/sdcard/");
        if (fTmpLogFilePath != null && fTmpLogFilePath.exists()) {
            Log.d(GlobalPara.Tky, "sdcard exist");//
            bSdcardExist = true;
        }
        if (!bSdcardExist) {
            Log.d(GlobalPara.Tky, "LOG_FILEPATH = /LocomotiveMain/, pelease attention");//
            LogParam.LOG_FILEPATH = "/LocomotiveMain/";
            LogParam.LOG_FILEPATH_UPDATE = "/UpdateInfo/";

            LogParam.PARAMETERFILE = LogParam.LOG_FILEPATH + "para.bak"; // 参数记录
            LogParam.PARA_LOG_NET = LogParam.LOG_FILEPATH + "para_log_net.bak"; // 参赛日志
            LogParam.PARAMETERFILE_UPDATE = LogParam.LOG_FILEPATH_UPDATE + "para.bak";
            LogParam.VERSIONFILE_UPDATE = LogParam.LOG_FILEPATH_UPDATE + "version.txt";
            LogParam.strExceptionLogPath = LogParam.LOG_FILEPATH + "exception.log";
            LogParam.strAllCommandPath = LogParam.LOG_FILEPATH + "AllCommand.txt";
            LogParam.strDispatchCommandPath = LogParam.LOG_FILEPATH + "DispatchCommand/";
            LogParam.strRuningTokenPath = LogParam.LOG_FILEPATH + "RuningToken/";
            LogParam.strShuntingOperationPath = LogParam.LOG_FILEPATH + "ShuntingOperation/";
            LogParam.strAdvanceNoticePath = LogParam.LOG_FILEPATH + "AdvanceNotice/";
            LogParam.strOtherCommandPath = LogParam.LOG_FILEPATH + "OtherCommand/";
            LogParam.strLossPackagePath = LogParam.LOG_FILEPATH + "LossPackage/";
            LogParam.strOtherRecordPath = LogParam.LOG_FILEPATH + "OtherRecord/";

            LogParam.LOG_MAXSIZE_FOR_POC = LogParam.iLOG_MAXSIZE_FOR_POC_FLASH;
            LogParam.LOG_MAX_SIZE_FOR_SYSTEM = LogParam.iLOG_MAX_SIZE_FOR_SYSTEM_FLASH;
            LogParam.LOG_MAX_SIZE_FOR_CIR = LogParam.iLOG_MAX_SIZE_FOR_CIR_FLASH;
        } else {
            Log.d(GlobalPara.Tky, LogParam.LOG_FILEPATH);//
            LogParam.LOG_MAXSIZE_FOR_POC = LogParam.iLOG_MAXSIZE_FOR_POC_SDCARD;
            LogParam.LOG_MAX_SIZE_FOR_SYSTEM = LogParam.iLOG_MAX_SIZE_FOR_SYSTEM_SDCARD;
            LogParam.LOG_MAX_SIZE_FOR_CIR = LogParam.iLOG_MAX_SIZE_FOR_CIR_SDCARD;
        }

        GlobalPara.lStartServiceTime = System.currentTimeMillis();
        String strPath = null;
        File aFile = null;
        strPath = LogParam.LOG_FILEPATH;//"/LocomotiveMain/"
        aFile = new File(strPath);
        if (!aFile.exists()) {
            if (!aFile.mkdirs())// 目录创建未成功
            {
                Log.e(GlobalPara.Tky, "can't creat " + strPath + " dir, so finish");
                finish();
            }
        }

        strPath = LogParam.LOG_FILEPATH + LogParam.LOG_NAME;//"/LocomotiveMain/cir.log"
        aFile = new File(strPath);
        if (!aFile.exists()) {
            try {
                RandomAccessFile file = new RandomAccessFile(aFile, "rw");
                file.close();
                file = null;
            } catch (Exception e) {
                Log.e(GlobalPara.Tky, "onCreate exception: create cir.log");
                e.printStackTrace();
                if (e.getMessage() != null && e.getMessage() != "") {
                    Log.e(GlobalPara.Tky, e.getMessage());
                }
            }
        }

        strPath = LogParam.LOG_FILEPATH + LogParam.LOG_NAME_POC; //"/LocomotiveMain/pocdroid.log"
        aFile = new File(strPath);
        if (!aFile.exists()) {
            try {
                RandomAccessFile file = new RandomAccessFile(aFile, "rw");
                file.close();
                file = null;
            } catch (Exception e) {
                Log.e(GlobalPara.Tky, "onCreate exception: create pocdroid.log");
                e.printStackTrace();
                if (e.getMessage() != null && e.getMessage() != "") {
                    Log.e(GlobalPara.Tky, e.getMessage());
                }
            }
        }
        GlobalPara.iZhuKongFlag = 0x01;

        String strDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        LogInstance.startLogFile();
        LogInstance.debug(GlobalPara.Tky, "tieke_lte_start: " + strDate);

        AppExcepitonHandler aAppExcepitonHandler = AppExcepitonHandler.getInstance();
        aAppExcepitonHandler.SetUnExceptionHandler();

        ConfigHelper aConfigHelper = ConfigHelper.getInstance();
        aConfigHelper.setContext(this);
        aConfigHelper.GethtLogNetParameters();
        aConfigHelper.GethtParameters();
    }

    public boolean bRunStateThread = true;
    public RunStateThread mRunStateThread = null;
    public int iLac = 0;
    public int iCi = 0;


    public class RunStateThread extends Thread {
        @Override
        public void run() {
            try {
                super.run();
                while (!isInterrupted()) {
                    if (bRunStateThread) {
                        try {
                            //long tempTime = System.currentTimeMillis();//

                            GsmCellLocation cLocation = (GsmCellLocation) tm.getCellLocation();
                            int _iLac = cLocation.getLac();
                            int _iCi = cLocation.getCid();
                            LogInstance.debug(GlobalPara.Tky, "iLac=" + _iLac + ", iCi=" + _iCi);

                            if (iLac != _iLac || iCi != _iCi) {
                                if (mLteHandle != null && mLteHandle.mblPOCRegister) {
                                    iLac = _iLac;
                                    iCi = _iCi;
                                    String strLocaton = "" + iLac + iCi;
                                    mLteHandle.ReportLocation(strLocaton);

                                }
                            }

                            Thread.sleep(30000);//
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e.getMessage() != null && e.getMessage() != "") {
                                LogInstance.exception(GlobalPara.Tky, e);
                            }
                            LogInstance.error(GlobalPara.Tky, "0." + "" + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
                                    + Thread.currentThread().getStackTrace()[2].getLineNumber());
                        }
                    } else {
                        LogInstance.debug(GlobalPara.Tky, "79." + "" + ": bRunStateThread is false");
                        return;
                    }
                }
                LogInstance.debug(GlobalPara.Tky, "RunStateThread exit ok");
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage() != null && e.getMessage() != "") {
                    LogInstance.exception(GlobalPara.Tky, e);
                }
                LogInstance.error(GlobalPara.Tky, "0." + "" + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
                        + Thread.currentThread().getStackTrace()[2].getLineNumber());
            }
        }
    }

    @Subscribe
    public void onEventBus(MainBean bean) {
        if (bean != null) {
            Message msg = new Message();
            msg.what = Config.OnlineCode;
            msg.obj = bean;
            handler.sendMessage(msg);
        }
    }

    @Subscribe
    public void onEventBus(FunctionRegisterResponseBean bean) {
        if (bean != null) {
            Message msg = new Message();
            msg.what = Config.FunctionRegisterCode;
            msg.obj = bean;
            handler.sendMessage(msg);
        }
    }

    @Subscribe
    public void onEventBus(String calling) {
        if (!TextUtils.isEmpty(calling)) {
            if(calling.equals(Config.Calling)){
                Message msg = new Message();
                msg.what = Config.CallingCode;
                msg.obj = calling;
                handler.sendMessage(msg);
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.OnlineCode:
                    MainBean bean = (MainBean)msg.obj;
                    String strWhichChange = bean.getStrWhichChange();
                    String strChangeValue = bean.getStrChangeValue();
                    if (!TextUtils.isEmpty(strWhichChange)){
		    if (strWhichChange.equals("pocstate")){
		        if (!TextUtils.isEmpty(strChangeValue)){
                    if (strChangeValue.equals("on")) {
                        tvStatus.setText("上线");
                        ivImage.setImageResource(R.mipmap.ic_person_on);//ic_person_24dp

                        //
                        GsmCellLocation cLocation = (GsmCellLocation) tm.getCellLocation();
                        int _iLac = cLocation.getLac();
                        int _iCi = cLocation.getCid();
                        if (iLac != _iLac || iCi != _iCi) {
                            if (mLteHandle != null && mLteHandle.mblPOCRegister) {
                                iLac = _iLac;
                                iCi = _iCi;
                                String strLocaton = "" + iLac + iCi;
                                mLteHandle.ReportLocation(strLocaton);

                            }
                        }

                    } else if (strChangeValue.equals("off")) {//
                        tvStatus.setText("下线");
                        ivImage.setImageResource(R.mipmap.ic_person_off);//ic_call_online
//                        ivImage.setBackground(getDrawable(R.mipmap.ic_person_off));//ic_call_online
                        if(bean.getStrReason().equals("forbidUser"))
                            ToastUtil.showLongToast("非法用法,上线失败");
                        else if(bean.getStrReason().equals("accountOrPasswordError"))
                            ToastUtil.showLongToast("账号密码错误");
                        else if(bean.getStrReason().equals("accountOrPasswordError"))
                            ToastUtil.showLongToast("账号密码错误,上线失败");
                        else if(bean.getStrReason().equals("lockedUser"))
                            ToastUtil.showLongToast("账号被锁,上线失败");
                        else if(bean.getStrReason().equals("forbidUpline"))
                            ToastUtil.showLongToast("禁止登陆,上线失败");
                        else
                            ToastUtil.showLongToast(bean.getStrReason());
                    }
                }
            }else if(strWhichChange.equals("locationstate")){
                if (!TextUtils.isEmpty(strChangeValue)){
                    TextView tvLacci = (TextView)findViewById(R.id.lacci);//
                    tvLacci.setText( strChangeValue);
                }else {
                    ToastUtil.showLongToast(bean.getStrReason());//
                }
            }else if(strWhichChange.equals("phoneid")){
                if (!TextUtils.isEmpty(strChangeValue)){
                    tvAddress.setText("本机号码：" + strChangeValue);//
                }
            }
            else if(strWhichChange.equals("shengfenstate")){
                if (!TextUtils.isEmpty(strChangeValue)){
                    TextView tvShengfen = (TextView)findViewById(R.id.shengfen);//
                    String strShengfen = GlobalFunc.translateFN(strChangeValue, 0x01);
                    tvShengfen.setText( strShengfen);
                }
            }
        }
                    break;
                case Config.FunctionRegisterCode:
                    FunctionRegisterResponseBean register= (FunctionRegisterResponseBean)msg.obj;
                     int nFunctionNumberType = register.getnFunctionNumberType();
                     int nOperationType = register.getnOperationType();
                     int nResult = register.getnResult();
                     String szFN = register.getSzFN();
                     String szReason = register.getSzReason();

                     //
                    if((nFunctionNumberType == 2 ) && nOperationType == 1 && nResult == 0) {
                        ToastUtil.showLongToast("车次功能号:" + szFN + "注册成功");
                    }
                    else if(nFunctionNumberType == 2 && nOperationType == 1 && nResult == -1) {
                        if(szReason.equals("fnRegFailed_failed"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注册失败");
                        else if(szReason.equals("fnRegFailed_conflict"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注册失败,功能号冲突");
                        else if(szReason.equals("fnRegFailed_conflict"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注册失败,用户未授权");
                        else if(szReason.equals("fnRegFailed_tooManyRegister"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注册失败,注册功能号过多");
                        else if(szReason.equals("fnRegFailed_inadequateSeviceCapacity"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注册失败,超出服务能力");
                        else if(szReason.equals("fnRegFailed_locked"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注册失败,用户被锁定");
                        else if(szReason.equals("fnRegFailed_timeout"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注册失败,注册超时");
                        else
                            ToastUtil.showLongToast("车次功能号:"+szFN+"注册失败 "+szReason);
                    }
                    else if(nFunctionNumberType == 2 && nOperationType == 2 && nResult == 0) {
                        ToastUtil.showLongToast("车次功能号:" + szFN + "注销成功");
                    }
                    else if(nFunctionNumberType == 2 && nOperationType == 2 && nResult == -1) {
                        if(szReason.equals("fnDeregFailed_failed"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注销失败");
                        else if(szReason.equals("fnDeregFailed_cancelled"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注销失败,功能号已被注销");
                        else if(szReason.equals("fnDeregFailed_unAuthorized"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注销失败,用户未授权");
                        else if(szReason.equals("fnDeregFailed_not_owner"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注销失败,该用户未注册该功能号");
                        else if(szReason.equals("fnRegFailed_timeout"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "注销失败,注册超时");
                        else if(szReason.equals("force_fnDeregFailed_failed"))
                            ToastUtil.showLongToast("车次功能号:" + szFN+ "强制注销失败");
                        else if(szReason.equals("force_fnDeregFailed_fn_unregister"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "强制注销失败,功能号未注册");
                        else if(szReason.equals("force_fnDeregFailed_unAuthorized"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "强制注销失败,用户未授权");
                        else if(szReason.equals("force_fnDeregFailed_timeout"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "强制注销失败,超时");
                        else if(szReason.equals("force_fnDeregFailed_user_unregister"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "强制注销失败,功能号不属于该用户");
                        else if(szReason.equals("force_fnDeregFailed_user_fail"))
                            ToastUtil.showLongToast("车次功能号:" + szFN + "强制注销失败,用户错误");
                        else
                            ToastUtil.showLongToast("车次功能号:"+szFN+"注销失败 "+szReason);
                    }
                    else if(nFunctionNumberType == 3 && nOperationType == 1 && nResult == 0) {
                        ToastUtil.showLongToast("机车功能号:" + szFN + "注册成功");
                    }
                    else if(nFunctionNumberType == 3 && nOperationType == 1 && nResult == -1) {
                        if(szReason.equals("fnRegFailed_failed"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败");
                        else if(szReason.equals("fnRegFailed_conflict"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,功能号冲突");
                        else if(szReason.equals("fnRegFailed_conflict"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,用户未授权");
                        else if(szReason.equals("fnRegFailed_tooManyRegister"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,注册功能号过多");
                        else if(szReason.equals("fnRegFailed_inadequateSeviceCapacity"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,超出服务能力");
                        else if(szReason.equals("fnRegFailed_locked"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,用户被锁定");
                        else if(szReason.equals("fnRegFailed_timeout"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,注册超时");
                        else
                            ToastUtil.showLongToast("机车功能号:"+szFN+"注册失败 "+szReason);
                    }
                    else if(nFunctionNumberType == 3 && nOperationType == 2 && nResult == 0) {
                        ToastUtil.showLongToast("机车功能号:" + szFN + "注销成功");
                    }
                    else if(nFunctionNumberType == 3 && nOperationType == 2 && nResult == -1) {
                        if(szReason.equals("fnDeregFailed_failed"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注销失败");
                        else if(szReason.equals("fnDeregFailed_cancelled"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,功能号已被注销");
                        else if(szReason.equals("fnDeregFailed_unAuthorized"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,用户未授权");
                        else if(szReason.equals("fnDeregFailed_not_owner"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,该用户未注册该功能号");
                        else if(szReason.equals("fnRegFailed_timeout"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "注册失败,注册超时");
                        else if(szReason.equals("force_fnDeregFailed_failed"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "强制注册失败");
                        else if(szReason.equals("force_fnDeregFailed_fn_unregister"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "强制注册失败,功能号未注册");
                        else if(szReason.equals("force_fnDeregFailed_unAuthorized"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "强制注册失败,用户未授权");
                        else if(szReason.equals("force_fnDeregFailed_timeout"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "强制注册失败,超时");
                        else if(szReason.equals("force_fnDeregFailed_user_unregister"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "强制注册失败,功能号不属于该用户");
                        else if(szReason.equals("force_fnDeregFailed_user_fail"))
                            ToastUtil.showLongToast("机车功能号:" + szFN + "强制注册失败,用户错误");
                        else
                            ToastUtil.showLongToast("机车功能号:"+szFN+"注销失败 "+szReason);
                    }
                    else {
                        ToastUtil.showLongToast("未知功能号操作");
                    }
                    break;
                case Config.CallingCode:
                    String calling = (String)msg.obj;
                    if (calling.equals(Config.Calling)){
                        if (!Utils.isForeground(MainActivity.this,"DialingActivity")){
                            launchActivity(DialingActivity.class);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mHomeWatcherReceiver);
    }


    //
    public class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";

        @Override
        public void onReceive(Context context, Intent intent) {

            String intentAction = intent.getAction();
            LogInstance.debug(GlobalPara.Tky,  "intentAction =" + intentAction);
            if (intentAction.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                LogInstance.debug(GlobalPara.Tky, "reason =" + reason);//
            }
            else if (intentAction.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if(info != null && info.isAvailable())
                {
                    String strTmpPhoneId = tm.getLine1Number();//
                    String strTmpPhoneImsi = tm.getSubscriberId();
                    if(strTmpPhoneId != null)//
                        GlobalPara.strPhoneId = strTmpPhoneId;
                    if(strTmpPhoneImsi != null)
                        GlobalPara.strPhoneImsi = strTmpPhoneImsi;

                    String name = info.getTypeName();
                    LogInstance.debug(GlobalPara.Tky, "当前网络名称：" + name);
                    LteHandle.getInstance().UpLine();//注册

                }
            }
        }
    }
}
