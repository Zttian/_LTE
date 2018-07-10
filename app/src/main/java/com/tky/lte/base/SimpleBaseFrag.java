package com.tky.lte.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by I am on 2018/6/27.
 */

public abstract class SimpleBaseFrag extends Fragment implements View.OnClickListener {
    protected Activity mContext;
    protected ImageButton mButton;
    protected View mFragment;
    protected LayoutInflater mInflater;
    protected ViewGroup container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mContext = getActivity();
        this.mInflater = inflater;
        this.container = container;
        mFragment = getFragment();
        this.mButton = getButton();
        if (mButton != null) mButton.setOnClickListener(this);
        bindEvent();
        return mFragment;
    }

    public abstract void bindEvent();

    public abstract BasePopupWindow getPopup();

    public abstract ImageButton getButton();

    public abstract View getFragment();

    protected final View findViewById(int id) {
        return mFragment.findViewById(id);
    }

    @Override
    public void onClick(View v) {
        if (v == mButton) {
            if (getPopup() != null) {
                getPopup().showPopupWindow();
            }
        }
    }
}

