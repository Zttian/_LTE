package com.tky.lte.ui.fragment;

import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.ui.adapter.TabLayoutFmPageAdapter;
import com.tky.lte.widget.CustomViewPager;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/5/29.
 * 呼叫
 */

public class CallFragment extends BaseFragment {

    @BindView(R.id.tabLayoutCall)
    SlidingTabLayout tabLayoutCall;
    @BindView(R.id.floating_action_button)
    ImageButton floatingActionButton;
    @BindView(R.id.floating_action_button_container)
    FrameLayout floatingActionButtonContainer;
    @BindView(R.id.bottomSheetLayout)
    BottomSheetLayout bottomSheetLayout;
    @BindView(R.id.viewPageCall)
    CustomViewPager viewPageCall;

    private String[] mTitles = {"通话记录", "单呼", "组呼"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private TabLayoutFmPageAdapter fragmentPageAdapter;



    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_call;
    }

    @Override
    protected void init() {
        mFragments.add(new CallJilFragment());
        mFragments.add(new CallSingleFragment());
        mFragments.add(new CallGroupFragment());
//        mFragments.add(new CallBookFragment());

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


    @OnClick(R.id.floating_action_button)
    public void onViewClicked() {
        new DialFragment().show(getActivity().getSupportFragmentManager(), R.id.bottomSheetLayout);
    }
}
