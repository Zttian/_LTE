package com.tky.lte.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ZhengTiantian
 * @Date: 2017/8/7
 * @version: 1.0
 * @Description:Fragment基类
 */


public abstract class BaseFragment extends Fragment {

    protected static final int REQUEST_CODE = 10000;
    private Unbinder unbinder;
    protected LayoutInflater inflater;


    /**
     * 获取布局ID
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 界面初始化
     */
    protected abstract void init();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        if(getContentViewLayoutID()!=0){
            return inflater.inflate(getContentViewLayoutID(),container,false);
        }else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder= ButterKnife.bind(this,view);
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    protected void launchActivity(Class<?> c, Bundle b, boolean isCloseSelf) {
        Intent i = new Intent(getActivity(), c);
        if (b != null) {
            i.putExtras(b);
        }
        startActivity(i);
        if (isCloseSelf)
            getActivity().finish();
    }


    protected void launchActivity(Class<?> c) {
        launchActivity(c, null, false);
    }

    protected void launchActivity(Class<?> c, Bundle b) {
        launchActivity(c, b, false);
    }

    protected void launchActivity(Class<?> c, boolean isCloseSelf) {
        launchActivity(c, null, isCloseSelf);
    }

    protected void launchActivityForResult(Class<?> c, Bundle b) {
        Intent i = new Intent(getActivity(), c);
        if (b != null) {
            i.putExtras(b);
        }
        startActivityForResult(i, REQUEST_CODE);
    }

    public void launchActivityForResult(Class<?> c) {
        launchActivityForResult(c, null);
    }


}