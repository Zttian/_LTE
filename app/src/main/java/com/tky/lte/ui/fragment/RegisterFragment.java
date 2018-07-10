package com.tky.lte.ui.fragment;

import android.view.View;
import android.widget.LinearLayout;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.ui.activity.RegisterCheCiActivity;
import com.tky.lte.ui.activity.RegisterJiCheActivity;
import com.tky.lte.ui.activity.RegisterKsActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/5/30.
 */

public class RegisterFragment extends BaseFragment {

    @BindView(R.id.ll_Ccgnhzc)
    LinearLayout llCcgnhzc;
    @BindView(R.id.ll_Jcgnhzc)
    LinearLayout llJcgnhzc;
    @BindView(R.id.ll_Kszc)
    LinearLayout llKszc;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_register;
    }

    @Override
    protected void init() {

    }


    @OnClick({R.id.ll_Ccgnhzc, R.id.ll_Jcgnhzc, R.id.ll_Kszc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_Ccgnhzc:
                launchActivity(RegisterCheCiActivity.class);
                break;
            case R.id.ll_Jcgnhzc:
                launchActivity(RegisterJiCheActivity.class);
                break;
            case R.id.ll_Kszc:
                launchActivity(RegisterKsActivity.class);
                break;
        }
    }
}
