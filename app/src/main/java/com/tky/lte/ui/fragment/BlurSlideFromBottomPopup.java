package com.tky.lte.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import com.tky.lte.R;
import com.tky.lte.db.CallJiDao;
import com.tky.lte.lte.GlobalFunc;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.activity.DialingActivity;
import com.tky.lte.ui.entity.CallJilEntity;
import com.tky.lte.util.ToastUtil;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by I am on 2018/6/27.
 */

public class BlurSlideFromBottomPopup extends BasePopupWindow implements View.OnClickListener {

    private View popupView;
    private StringBuffer stringBuffer;
    private TextView tvNumber;
    private Activity context;


    public BlurSlideFromBottomPopup(Activity context) {
        super(context);
        this.context = context;
        bindEvent();
    }

    @Override
    protected Animation initShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0f, 300);
    }

    @Override
    protected Animation initExitAnimation() {
        return getTranslateVerticalAnimation(0f, 1f, 300);
    }

    @Override
    public View getClickToDismissView() {
        return popupView.findViewById(R.id.click_to_dismiss);
    }

    @Override
    public View onCreatePopupView() {
        popupView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dial, null);
        return popupView;
    }

    @Override
    public View initAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }


    private void bindEvent() {
        stringBuffer = new StringBuffer();
        if (popupView != null) {
            tvNumber = popupView.findViewById(R.id.tvNumber);
            popupView.findViewById(R.id.deleteBut).setOnClickListener(this);
            popupView.findViewById(R.id.ll_1).setOnClickListener(this);
            popupView.findViewById(R.id.ll_2).setOnClickListener(this);
            popupView.findViewById(R.id.ll_3).setOnClickListener(this);
            popupView.findViewById(R.id.ll_4).setOnClickListener(this);
            popupView.findViewById(R.id.ll_5).setOnClickListener(this);
            popupView.findViewById(R.id.ll_6).setOnClickListener(this);
            popupView.findViewById(R.id.ll_7).setOnClickListener(this);
            popupView.findViewById(R.id.ll_8).setOnClickListener(this);
            popupView.findViewById(R.id.ll_9).setOnClickListener(this);
            popupView.findViewById(R.id.ll_0).setOnClickListener(this);
            popupView.findViewById(R.id.ll_Calling).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_1:
                stringBuffer.append("1");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_2:
                stringBuffer.append("2");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_3:
                stringBuffer.append("3");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_4:
                stringBuffer.append("4");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_5:
                stringBuffer.append("5");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_6:
                stringBuffer.append("6");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_7:
                stringBuffer.append("7");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_8:
                stringBuffer.append("8");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_9:
                stringBuffer.append("9");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.ll_0:
                stringBuffer.append("0");
                tvNumber.setText(stringBuffer.toString());
                break;
            case R.id.deleteBut:
                if (stringBuffer.length() != 0){
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    tvNumber.setText(stringBuffer.toString());
                }
                if (stringBuffer.length() == 0){
                    tvNumber.setText("");
                }
                break;
            case R.id.ll_Calling:
                if (!TextUtils.isEmpty(tvNumber.getText().toString())){
                    LteHandle mLteHandle = LteHandle.getInstance();

                    if(!mLteHandle.mblPOCRegister){
                        ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                        return;
                    }

                    int ret = mLteHandle.StartCall(0x01, -1, stringBuffer.toString());
                    if (ret == -1) {
                        ToastUtil.showShortToast("呼叫失败");
                    } else {
                        Intent intent = new Intent(context,DialingActivity.class);
                        context.startActivity(intent);
                    }
                }
                break;
        }
    }
}
