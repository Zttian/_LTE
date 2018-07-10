package com.tky.lte.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.ui.adapter.TabLayoutFmPageAdapter;
import java.util.ArrayList;
import butterknife.BindView;

/**
 * Created by ttz on 2018/5/29.
 * 功能号
 */

public class FunctionNumberFragment extends BaseFragment {

    @BindView(R.id.tabLayoutFunction)
    SlidingTabLayout tabLayoutFunction;
    @BindView(R.id.viewPageFunction)
    ViewPager viewPageFunction;
    private String[] mTitles = {"注册", "注销"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private TabLayoutFmPageAdapter fragmentPageAdapter;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_function_number;
    }

    @Override
    protected void init() {
        mFragments.add(new RegisterFragment());
        mFragments.add(new CancelFragment());
//        mFragments.add(new QueryFragment());
//        mFragments.add(new QzCancelFragment());

        fragmentPageAdapter = new TabLayoutFmPageAdapter(getActivity().getSupportFragmentManager(), getActivity(), mTitles, mFragments);
        viewPageFunction.setAdapter(fragmentPageAdapter);
        tabLayoutFunction.setViewPager(viewPageFunction);
        tabLayoutFunction.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPageFunction.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        viewPageFunction.setCurrentItem(0);
    }
}
