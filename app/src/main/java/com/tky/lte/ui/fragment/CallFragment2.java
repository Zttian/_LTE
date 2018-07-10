package com.tky.lte.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tky.lte.R;
import com.tky.lte.base.SimpleBaseFrag;
import com.tky.lte.ui.adapter.TabLayoutFmPageAdapter;

import java.util.ArrayList;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by I am on 2018/6/27.
 */

public class CallFragment2 extends SimpleBaseFrag {
    private SlidingTabLayout tabLayoutCall;
    private ViewPager viewPageCall;

    private String[] mTitles = {"通话记录", "单呼", "组呼"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private TabLayoutFmPageAdapter fragmentPageAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void bindEvent() {
        tabLayoutCall = (SlidingTabLayout)findViewById(R.id.tabLayoutCall);
        viewPageCall = (ViewPager)findViewById(R.id.viewPageCall);

        mFragments.add(new CallJilFragment());
        mFragments.add(new CallSingleFragment());
        mFragments.add(new CallGroupFragment());

        fragmentPageAdapter = new TabLayoutFmPageAdapter(getActivity().getSupportFragmentManager(), getActivity(), mTitles, mFragments);
        viewPageCall.setAdapter(fragmentPageAdapter);
        tabLayoutCall.setViewPager(viewPageCall);
        tabLayoutCall.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPageCall.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        viewPageCall.setCurrentItem(0);
    }

    @Override
    public BasePopupWindow getPopup() {
        final BlurSlideFromBottomPopup popup = new BlurSlideFromBottomPopup(getActivity());
        popup.setOnBeforeShowCallback(new BasePopupWindow.OnBeforeShowCallback() {
            @Override
            public boolean onBeforeShow(View popupRootView, View anchorView, boolean hasShowAnima) {
                popup.setBlurBackgroundEnable(true);
                return true;
            }
        });
        return popup;
    }

    @Override
    public ImageButton getButton() {
        return (ImageButton)mFragment.findViewById(R.id.floating_action_button);
    }

    @Override
    public View getFragment() {
        return mInflater.inflate(R.layout.fragment_call2, container, false);
    }
}
