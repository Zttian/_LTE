package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.constants.Config;
import com.tky.lte.lte.GlobalFunc;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.event.ForceUnregisterResponseBean;
import com.tky.lte.ui.event.FunctionNumIsUsedBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.util.Utils;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;
import com.tky.lte.widget.MyAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by I am on 2018/6/14.
 */

public class RegisterJiCheActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.et_Jch)
    ClearEditText etJch;
    @BindView(R.id.btRegister)
    Button btRegister;
    @BindView(R.id.btQuery)
    Button btQuery;
    @BindView(R.id.tv_Jcm)
    TextView tvJcm;
    @BindView(R.id.tv_Gnm)
    TextView tvGnm;
    private List<String> jicheList = new ArrayList<>();
    private List<String> gnengList = new ArrayList<>();

    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("机车功能号");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register_ji_che;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        jicheList.add("225");
        jicheList.add("231");
        jicheList.add("232");

        gnengList.add("本务机司机(车载台)");
        gnengList.add("补机司机1(车载台)");
        gnengList.add("补机司机2(车载台)");
        gnengList.add("补机司机3(车载台)");
        gnengList.add("补机司机4(车载台)");
        gnengList.add("列车长1");
        gnengList.add("列车长2");
        gnengList.add("乘警长1");
        gnengList.add("乘警长2");
        gnengList.add("ETCS/CTCS使用");

        gnengList.add("本务机司机(手持终端)");
        gnengList.add("补机1司机(手持终端)");
        gnengList.add("补机2司机(手持终端)");
        gnengList.add("补机3司机(手持终端)");
        gnengList.add("补机4司机(手持终端)");

        gnengList.add("运转车长1随车机械师1");
        gnengList.add("运转车长2随车机械师2");

        //
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 功能码输入进入后包含 01本务机司机（车载台）、
     * 02补机司机1（车载台）、03补机司机2（车载台）、
     * 04补机司机3（车载台）、05补机司机4（车载台）、
     * 10列车长1、11列车长2、31乘警长1、32乘警长2、
     * 40ETCS/CTCS使用、81本务机司机（手持终端）、
     * 82补机1司机（手持终端）、83补机2司机（手持终端）、
     * 84补机3司机（手持终端）、85补机4司机（手持终端）、
     * 86运转车长1随车机械师1、87运转车长2随车机械师2
     * @param str
     * @return
     */
    private String getGnm(String str){
        String gnm = "";
        if (str.equals("本务机司机(车载台)")){
            gnm = "01";
        }else if(str.equals("补机司机1(车载台)")){
            gnm = "02";
        }else if(str.equals("补机司机2(车载台)")){
            gnm = "03";
        }else if(str.equals("补机司机3(车载台)")){
            gnm = "04";
        }else if(str.equals("补机司机4(车载台)")){
            gnm = "05";
        }else if(str.equals("列车长1")){
            gnm = "10";
        }else if(str.equals("列车长2")){
            gnm = "11";
        }else if(str.equals("乘警长1")){
            gnm = "31";
        }else if(str.equals("乘警长2")){
            gnm = "32";
        }else if(str.equals("ETCS/CTCS使用")){
            gnm = "40";
        }else if(str.equals("本务机司机(手持终端)")){
            gnm = "81";
        }else if(str.equals("补机1司机(手持终端)")){
            gnm = "82";
        }else if(str.equals("补机2司机(手持终端)")){
            gnm = "83";
        }else if(str.equals("补机3司机(手持终端)")){
            gnm = "84";
        }else if(str.equals("补机4司机(手持终端)")){
            gnm = "85";
        }else if(str.equals("运转车长1随车机械师1")){
            gnm = "86";
        }else if(str.equals("运转车长2随车机械师2")){
            gnm = "87";
        }
        return gnm;
    }
    //
    public void showForceUnregister(int resultCode, final String szFn, final String szUn, String szReason){
        MyAlertDialog dialog = new MyAlertDialog(this).builder();
        dialog.setCancelable(false);
        dialog.setTitle("功能号码：" + szFn);
        dialog.setMsg("用户号码：" + szUn);
        dialog.setPositiveButton("强制注销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(szUn.equals(GlobalPara.strPhoneId)) {
                    ToastUtil.showLongToast("该功能号已被本机注册,无需强制注销");
                    return;
                }
                LteHandle mLteHandle = LteHandle.getInstance();
                if(!mLteHandle.mblPOCRegister){
                    ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                    return;
                }
                int ret = mLteHandle.ForceUnRegisterFunctionNumber(szFn,szUn);
                if (ret == -1) {
                    ToastUtil.showLongToast("车次功能号强制注销失败");
                }
            }
        }).setNegativeButton("取消",null).show();
    }

    //
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.QueryFunctionRegisterIsUsedCode: {
                    FunctionNumIsUsedBean bean = (FunctionNumIsUsedBean) msg.obj;
                    int resultCode = bean.getnResult();
                    String szFn = bean.getSzFN();
                    if(szFn == null)
                        szFn = "";
                    String szUn = bean.getSzUN();
                    if(szUn == null)
                        szUn = "";
                    String szReason = bean.getSzReason();
                    if(szReason == null)
                        szReason = "";
                    if(resultCode == 0) {
                        showForceUnregister(resultCode, szFn, szUn, szReason);
                    }else{
                        if(szReason.equals("FN_QUERY_FAILED_FN_UNREGISTER"))
                            ToastUtil.showLongToast("功能号码:"+szFn+"未注册");
                        else if(szReason.equals("FN_QUERY_FAILED_UNAUTHORIZED"))
                            ToastUtil.showLongToast("用户未授权,不允许查询");
                        else if(szReason.equals("FN_QUERY_FAILED"))
                            ToastUtil.showLongToast("功能号查询失败");
                        else
                            ToastUtil.showLongToast("功能号查询失败 "+szReason);
                    }
                }
                break;
                case Config.QueryFunctionForceUnRegisterIsUsedCode: {
                    ForceUnregisterResponseBean bean = (ForceUnregisterResponseBean) msg.obj;
                    int resultCode = bean.getnResult();
                    String szFn = bean.getSzFN();
                    if(szFn == null)
                        szFn = "";
                    String szReason = bean.getSzReason();
                    if(szReason == null)
                        szReason = "";
                    if(resultCode == 0){
                        ToastUtil.showLongToast("功能号码:"+szFn+"强制注销成功");
                    }else{
                        if(szReason.equals("force_fnDeregFailed_failed"))
                            ToastUtil.showLongToast("功能号码:" + szFn + "强制注销失败");
                        else if(szReason.equals("force_fnDeregFailed_fn_unregister"))
                            ToastUtil.showLongToast("功能号码:" + szFn + "强制注销失败,功能号未注册");
                        else if(szReason.equals("force_fnDeregFailed_unAuthorized"))
                            ToastUtil.showLongToast("功能号码:" + szFn + "强制注销失败,用户未授权");
                        else if(szReason.equals("force_fnDeregFailed_timeout"))
                            ToastUtil.showLongToast("功能号码:" + szFn + "强制注销失败,超时");
                        else if(szReason.equals("force_fnDeregFailed_user_unregister"))
                            ToastUtil.showLongToast("功能号码:" + szFn + "强制注销失败,功能号不属于该用户");
                        else if(szReason.equals("force_fnDeregFailed_user_fail"))
                            ToastUtil.showLongToast("功能号码:" + szFn + "强制注销失败,用户错误");
                        else
                            ToastUtil.showLongToast("功能号码:"+szFn+"强制注销失败 "+szReason);
                    }
                }
                break;
            }
        }
    };

    //
    @Subscribe
    public void onEventBus(FunctionNumIsUsedBean bean) {
        if (bean != null){
            Message msg = new Message();
            msg.what = Config.QueryFunctionRegisterIsUsedCode;
            msg.obj = bean;
            handler.sendMessage(msg);
        }
    }

    //
    @Subscribe
    public void onEventBus(ForceUnregisterResponseBean bean) {
        if (bean != null){
            Message msg = new Message();
            msg.what = Config.QueryFunctionForceUnRegisterIsUsedCode;
            msg.obj = bean;
            handler.sendMessage(msg);
        }
    }

    //
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.btQuery, R.id.btRegister,R.id.tv_Jcm, R.id.tv_Gnm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btQuery: {
                //
                if (TextUtils.isEmpty(tvJcm.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("机车码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etJch.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("机车号不能为空");
                    return;
                }

                if (TextUtils.isEmpty(tvGnm.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("功能码不能为空");
                    return;
                }

                String strJcm = tvJcm.getText().toString().trim();
                String strJch = etJch.getText().toString().trim();
                String strGnm = getGnm(tvGnm.getText().toString().trim());
                LogInstance.debug(GlobalPara.Tky, "查询机车功能号:" + strJcm + "、" + strJch + "、" + strGnm);

                if (!TextUtils.isEmpty(strJch)) {
                    if (strJch.length() < 5) {
                        ToastUtil.showShortToastSafe("机车号不足5位,前面补0");
                        return;
                    }
                }
                String strFn = strJcm + strJch;
                int iStatus = Integer.parseInt(strGnm);

                LteHandle mLteHandle = LteHandle.getInstance();

                //
                if (!mLteHandle.mblPOCRegister) {
                    ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                    return;
                }

                String runnumberstatus = "";
                if (iStatus < 10){
                    runnumberstatus = "0" + String.valueOf(iStatus);
                }else{
                    runnumberstatus = String.valueOf(iStatus);
                }

                strFn = GlobalPara.strPreEfnNumber + strFn + runnumberstatus;

                int ret = mLteHandle.QueryFunctionNumberIsUsed(strFn);
                if (ret == -1) {
                    ToastUtil.showLongToast("机车功能号查询失败");
                }

            }
                break;
            case R.id.btRegister: {
                if (TextUtils.isEmpty(tvJcm.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("机车码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etJch.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("机车号不能为空");
                    return;
                }

                if (TextUtils.isEmpty(tvGnm.getText().toString().trim())) {
                    ToastUtil.showShortToastSafe("功能码不能为空");
                    return;
                }

                String strJcm = tvJcm.getText().toString().trim();
                String strJch = etJch.getText().toString().trim();
                String strGnm = getGnm(tvGnm.getText().toString().trim());
                LogInstance.debug(GlobalPara.Tky, "注册机车功能号:" + strJcm + "、" + strJch + "、" + strGnm);

                if (!TextUtils.isEmpty(strJch)) {
                    if (strJch.length() < 5) {
                        ToastUtil.showShortToastSafe("机车号不足5位,前面补0");
                        return;
                    }
                }
                String strFn = strJcm + strJch;
                int iStatus = Integer.parseInt(strGnm);

                LteHandle mLteHandle = LteHandle.getInstance();

                //
                if (!mLteHandle.mblPOCRegister) {
                    ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                    return;
                }

                int ret = mLteHandle.EngineNumberRegisterOrNot(strFn, iStatus, true);
                if (ret == -1) {
                    ToastUtil.showLongToast("机车功能号注册失败");
                } else {
                    finish();
                }
            }
                break;
            case R.id.tv_Jcm:
                Utils.initOptionsPickerView(this,jicheList,tvJcm);
                break;
            case R.id.tv_Gnm:
                Utils.initOptionsPickerView(this,gnengList,tvGnm);
                break;
        }
    }
}
