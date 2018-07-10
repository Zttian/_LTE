package com.tky.lte.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tky.lte.R;


/**
 * Created by ttz on 2017/8/11.
 * 通用标题栏
 */
public class CTitleBar extends RelativeLayout {

    private LinearLayout backLayout;
    private RelativeLayout bg_layout;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private TextView lblTitle;
    private TextView right_tv;
    private CTitleFinishListener cTitleFinishListener;

    public CTitleBar(Context context) {
        this(context, null);
    }

    public CTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFitsSystemWindows(true);
        LayoutInflater.from(context).inflate(R.layout.view_title_bar,
                this, true);
        btnLeft = (ImageButton) findViewById(R.id.back_btn);
        bg_layout = (RelativeLayout) findViewById(R.id.bg_layout);
        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindowToken(), 0);

                if (cTitleFinishListener != null)
                    cTitleFinishListener.finishActivity();
                else
                    ((Activity) getContext()).finish();
            }
        });
        btnRight = (ImageButton) findViewById(R.id.right_btn);
        lblTitle = (TextView) findViewById(R.id.title);
        right_tv = (TextView) findViewById(R.id.right_tv);
    }

    public void setBackground(int resId) {
        bg_layout.setBackgroundColor(resId);
    }

    public void setTitle(int resId) {
        lblTitle.setText(resId);
    }

    public TextView getLblTitle() {
        return lblTitle;
    }

    public void setTileBg(int resid) {
        lblTitle.setBackgroundResource(resid);
    }

    public void setTileOnClickListener(OnClickListener l) {
        lblTitle.setOnClickListener(l);
    }

    public void setTitle(CharSequence title) {
        lblTitle.setText(title);
    }

    public TextView getRightText() {
        right_tv.setVisibility(VISIBLE);
        return right_tv;
    }

    public void setRightButtonImage(int resId, OnClickListener l) {
        btnRight.setVisibility(VISIBLE);
        btnRight.setImageResource(resId);
        btnRight.setOnClickListener(l);
    }


    public void setTitleColor(int color) {
        lblTitle.setTextColor(color);
    }

    public void setLeftButtonImage(int resId) {
        btnLeft.setImageResource(resId);
    }

    public ImageButton getLeftButton() {
        return btnLeft;
    }


    public ImageButton getRightButton() {
        btnRight.setVisibility(View.VISIBLE);
        return btnRight;
    }

    public void setNoRightButton() {
        btnRight.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public interface CTitleBarContainer {
        void initializeTitleBar(CTitleBar titleBar);
    }

    public void setCTitleFinishListener(CTitleFinishListener cTitleFinishListener) {
        this.cTitleFinishListener = cTitleFinishListener;
    }

    public interface CTitleFinishListener {
        void finishActivity();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            ((CTitleBarContainer) getContext()).initializeTitleBar(this);
        }
    }
}
