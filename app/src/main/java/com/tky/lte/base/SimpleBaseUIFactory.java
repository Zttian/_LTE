package com.tky.lte.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tky.lte.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by ttz on 2017/10/25.
 * 加载动画
 */

public class SimpleBaseUIFactory extends BaseUIFactory{

    public SimpleBaseUIFactory(Context context){
        super(context);
    }

    @Override
    public ProgressDialog CreateProgressDialog(String title) {
        ProgressDialog mProgressBar = new ProgressDialog(mContext);
        mProgressBar.setTitle(title);
        mProgressBar.show();
        return mProgressBar;
    }

    @Override
    public Dialog CreateLoadingDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_base,null);
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView)view.findViewById(R.id.aviLoading);
//        avi.setIndicator("BallSpinFadeLoaderIndicator");
        avi.setIndicator("BallPulse");
        avi.setIndicatorColor(mContext.getResources().getColor(R.color.white));
        TextView loadText = (TextView) view.findViewById(R.id.tvLoadingText);
        loadText.setText("请稍后");
        Dialog mDialog = new Dialog(mContext,R.style.loading_dialog_style);
        mDialog.setCancelable(false);
        mDialog.setContentView(view,new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return mDialog;
    }
}
