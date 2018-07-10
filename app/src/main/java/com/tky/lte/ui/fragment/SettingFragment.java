package com.tky.lte.ui.fragment;

import android.view.View;
import android.widget.LinearLayout;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.ui.activity.SettingAnswerActivity;
import com.tky.lte.ui.activity.SettingFwqActivity;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/5/29.
 * 设置
 */

public class SettingFragment extends BaseFragment {

    @BindView(R.id.rl_Fwqsz)
    LinearLayout rlFwqsz;
    @BindView(R.id.rl_Zdjtsz)
    LinearLayout rlZdjtsz;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init() {

    }


    @OnClick({R.id.rl_Fwqsz, R.id.rl_Zdjtsz})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_Fwqsz:
                launchActivity(SettingFwqActivity.class);
                break;
            case R.id.rl_Zdjtsz:
                launchActivity(SettingAnswerActivity.class);
                break;
        }
    }
}
