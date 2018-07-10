package com.tky.lte.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.flipboard.bottomsheet.commons.BottomSheetFragment;
import com.tky.lte.R;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.activity.DialingActivity;
import com.tky.lte.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ttz on 2018/6/6.
 * 拨号盘
 */

public class DialFragment extends BottomSheetFragment {

    @BindView(R.id.tvNumber)
    TextView tvNumber;
    @BindView(R.id.deleteBut)
    ImageButton deleteBut;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.ll_6)
    LinearLayout ll6;
    @BindView(R.id.ll_7)
    LinearLayout ll7;
    @BindView(R.id.ll_8)
    LinearLayout ll8;
    @BindView(R.id.ll_9)
    LinearLayout ll9;
    @BindView(R.id.ll_0)
    LinearLayout ll0;
    @BindView(R.id.ll_Calling)
    LinearLayout llCalling;
    Unbinder unbinder;
    private StringBuffer stringBuffer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dial, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stringBuffer = new StringBuffer();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.deleteBut, R.id.ll_1, R.id.ll_2, R.id.ll_3, R.id.ll_4, R.id.ll_5, R.id.ll_6, R.id.ll_7, R.id.ll_8, R.id.ll_9, R.id.ll_0, R.id.ll_Calling})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                        Intent intent = new Intent(getActivity(),DialingActivity.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }
}
