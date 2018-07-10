package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.constants.Config;
import com.tky.lte.entity.UserInfo;
import com.tky.lte.lte.ConfigHelper;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.lte.ParamOperation;
import com.tky.lte.lte.TrainState;
import com.tky.lte.util.PreferenceUtil;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/6/14.
 */

public class SettingFwqActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.et_ZIP)
    ClearEditText etZIP;
    @BindView(R.id.et_BIP)
    ClearEditText etBIP;
    @BindView(R.id.btRegister)
    Button btRegister;

    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("服务器设置");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_setting_fwq;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!TextUtils.isEmpty(UserInfo.getSeverZIP())){
            etZIP.setText(UserInfo.getSeverZIP());
        }else{
            etZIP.setText("");
        }

        TrainState trainState = TrainState.getInstance();
        etZIP.setText(trainState.mLTE_SIP_IP);
        etBIP.setText(trainState.mLTE_SIP_IP2);
    }



    @OnClick(R.id.btRegister)
    public void onViewClicked() {
        ParamOperation aParamOperation = ParamOperation.getInstance();
        String strZIP = etZIP.getText().toString().trim();
        String strBIP = etBIP.getText().toString().trim();
        if(strZIP.equals("") || strBIP.equals("") ){
            ToastUtil.showShortToastSafe("主备服务器地址不能为空");
            return;
        }

        PreferenceUtil.getInstance().putPreferences(Config.ServerZip,strZIP);
        PreferenceUtil.getInstance().putPreferences(Config.ServerBip,strBIP);
        UserInfo.refresh();

        TrainState trainState = TrainState.getInstance();
        trainState.mLTE_SIP_IP = strZIP;
        trainState.mLTE_SIP_IP2 = strBIP;
        aParamOperation.SaveSpecialField("Sip_Ip", strZIP);
        aParamOperation.SaveSpecialField("Sip_Ip2", strBIP);
        ConfigHelper aConfigHelper = ConfigHelper.getInstance();
        aConfigHelper.SaveToBakfile();

        LteHandle lteHandle = LteHandle.getInstance();
        if (lteHandle != null && !lteHandle.mblPOCRegister)
            lteHandle.UpLine();

        ToastUtil.showShortToastSafe("服务器地址设置成功");
        finish();
    }
}
