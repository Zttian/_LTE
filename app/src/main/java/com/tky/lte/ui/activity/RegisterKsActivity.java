package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.widget.Button;

import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/6/14.
 */

public class RegisterKsActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.et_gnm)
    ClearEditText etGnm;
    @BindView(R.id.btRegister)
    Button btRegister;

    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("快速注册");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register_ks;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }


    @OnClick(R.id.btRegister)
    public void onViewClicked() {
        String strGnm = etGnm.getText().toString().trim();
        LogInstance.debug(GlobalPara.Tky, "注册功能号:" + strGnm);

        if (strGnm.equals("")) {
            ToastUtil.showLongToast("功能号码不能为空");
            return;
        }

        if (!strGnm.startsWith("2") && !strGnm.startsWith("3") && !strGnm.startsWith("0862")&& !strGnm.startsWith("0863"))  {
            ToastUtil.showLongToast("功能号码需以2/3/0862/0863开始");
            return;
        }
        LteHandle mLteHandle = LteHandle.getInstance();

        //
        if(!mLteHandle.mblPOCRegister){
            ToastUtil.showShortToastSafe("网络受限,无法进行操作");
            return;
        }

        int ret = mLteHandle.FunctionNumberRegisterOrNot(strGnm, true);
        if (ret == -1) {
            ToastUtil.showLongToast("功能号注册失败");
        }else{
            finish();
        }
    }
}
