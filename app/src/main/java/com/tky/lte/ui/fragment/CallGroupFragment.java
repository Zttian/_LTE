package com.tky.lte.ui.fragment;

import android.view.View;
import android.widget.LinearLayout;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.activity.DialingActivity;
import com.tky.lte.ui.activity.LSZHActivity;
import com.tky.lte.util.ToastUtil;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/5/29.
 * 组呼
 */

public class CallGroupFragment extends BaseFragment {

    @BindView(R.id.ll_Znzh)
    LinearLayout llZnzh;
    @BindView(R.id.ll_Lzzh)
    LinearLayout llLzzh;
    @BindView(R.id.ll_jjhj)
    LinearLayout llJjhj;
    @BindView(R.id.ll_llzh)
    LinearLayout llLlzh;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_call_group;
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.ll_Znzh, R.id.ll_Lzzh, R.id.ll_jjhj, R.id.ll_llzh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_Znzh://210
                startCall("50210");
                break;
            case R.id.ll_Lzzh://220
                startCall("50220");
                break;
            case R.id.ll_jjhj://299
                startCall("50299");
                break;
            case R.id.ll_llzh://890
                launchActivity(LSZHActivity.class);
                break;
        }
    }

    private void startCall(String number){
        LteHandle mLteHandle = LteHandle.getInstance();

        //
        if(!mLteHandle.mblPOCRegister){
            ToastUtil.showShortToastSafe("网络受限,无法进行操作");
            return;
        }

        int ret = mLteHandle.StartCall(0x02, -1, number);//
        if (ret == -1) {
            ToastUtil.showShortToast("呼叫失败");
        } else {
            launchActivity(DialingActivity.class);
        }
    }
}
