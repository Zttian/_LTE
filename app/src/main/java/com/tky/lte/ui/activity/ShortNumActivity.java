package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/6/11.
 */

public class ShortNumActivity extends BaseActivity implements CTitleBar.CTitleBarContainer{

    @BindView(R.id.et_Dhm)
     ClearEditText etDhm;

    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("短号码呼叫");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_short_num;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @OnClick(R.id.btCalling)
    public void onViewClicked() {

        String strDhm = etDhm.getText().toString().trim();

        if (TextUtils.isEmpty(strDhm)){
            ToastUtil.showShortToastSafe("短号码不能为空");
            return;
        }

        LogInstance.debug(GlobalPara.Tky,"呼叫:"+strDhm);

        LteHandle mLteHandle = LteHandle.getInstance();

        //
        if(!mLteHandle.mblPOCRegister){
            ToastUtil.showShortToastSafe("网络受限,无法进行操作");
            return;
        }

        int ret = mLteHandle.StartCall(0x01, -1 ,strDhm);
        if(ret == -1) {
            ToastUtil.showShortToast("呼叫失败");
        }else{
            launchActivity(DialingActivity.class);
        }
    }
}
