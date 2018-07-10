package com.tky.lte.ui.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.ui.activity.JiTrainActivity;
import com.tky.lte.ui.activity.ShortNumActivity;
import com.tky.lte.ui.activity.TrainNumActivity;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/5/29.
 * 单呼
 */

public class CallSingleFragment extends BaseFragment {


    @BindView(R.id.rl_Ccgnh)
    RelativeLayout rlCcgnh;
    @BindView(R.id.rl_Jcgnh)
    RelativeLayout rlJcgnh;
    @BindView(R.id.rl_Dhm)
    RelativeLayout rlDhm;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_call_single;
    }

    @Override
    protected void init() {

    }

    @OnClick({R.id.rl_Ccgnh, R.id.rl_Jcgnh, R.id.rl_Dhm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_Ccgnh:
                launchActivity(TrainNumActivity.class);
                break;
            case R.id.rl_Jcgnh:
                launchActivity(JiTrainActivity.class);
                break;
            case R.id.rl_Dhm:
                launchActivity(ShortNumActivity.class);
                break;
        }
    }
}
