package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.lte.ConfigHelper;
import com.tky.lte.lte.ParamOperation;
import com.tky.lte.lte.TrainState;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import butterknife.BindView;

/**
 * Created by ttz on 2018/6/14.
 */

public class SettingAnswerActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.rbOpen)
    RadioButton rbOpen;
    @BindView(R.id.rbClose)
    RadioButton rbClose;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("自定接听");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_setting_snswer;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        TrainState trainState = TrainState.getInstance();
        if(trainState.g_AutoAnswer.equals("1"))
            rbOpen.setChecked(true);
        else
            rbClose.setChecked(true);

        rbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ParamOperation aParamOperation = ParamOperation.getInstance();
                    TrainState trainState = TrainState.getInstance();
                    trainState.g_AutoAnswer = "1";
                    aParamOperation.SaveSpecialField("AutoAnswer", "1");
                    ConfigHelper aConfigHelper = ConfigHelper.getInstance();
                    aConfigHelper.SaveToBakfile();

                    ToastUtil.showShortToastSafe("打开自动接听");//
                    finish();
                }
            }
        });
        rbClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ParamOperation aParamOperation = ParamOperation.getInstance();
                    TrainState trainState = TrainState.getInstance();
                    trainState.g_AutoAnswer = "0";
                    aParamOperation.SaveSpecialField("AutoAnswer", "0");
                    ConfigHelper aConfigHelper = ConfigHelper.getInstance();
                    aConfigHelper.SaveToBakfile();

                    ToastUtil.showShortToastSafe("关闭自动接听");//
                    finish();
                }
            }
        });
    }
}
