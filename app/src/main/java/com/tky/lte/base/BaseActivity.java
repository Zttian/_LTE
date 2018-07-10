package com.tky.lte.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lzy.imagepicker.view.SystemBarTintManager;
import com.tky.lte.R;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.LogInstance;

import butterknife.ButterKnife;

/**
 * Created by ZhengTiantian
 * @Date: 2017/8/7
 * @version: 1.0
 * @Description:Activity基类
 */

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 获取布局ID
     *
     * @return  布局id
     */
    protected abstract int getContentViewLayoutID();
    /**
     * 初始化布局以及View控件
     */
    protected abstract void init(Bundle savedInstanceState);

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        initSystemBarTint();
        if(getContentViewLayoutID()!=0){
            setContentView(getContentViewLayoutID());
            init(savedInstanceState);
        }
    }


    public void launchActivity(Class<?> c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    public void launchActivity(Class<?> c, Bundle bundle) {
        if (bundle != null) {
            Intent intent = new Intent(this, c);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void launchActivityForResult(Class<?> c,int requestCode) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent,requestCode);
    }

    public void launchActivityForResult(Class<?> c, Bundle bundle,int requestCode) {
        if (bundle != null) {
            Intent intent = new Intent(this, c);
            intent.putExtras(bundle);
            startActivityForResult(intent,requestCode);
        }
    }

    /** 子类可以重写改变状态栏颜色 */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }

    /** 子类可以重写决定是否使用透明状态栏 */
    protected boolean translucentStatusBar() {
        return false;
    }

    /** 设置状态栏颜色 */
    protected void initSystemBarTint() {
        Window window = getWindow();
        if (translucentStatusBar()) {// 设置状态栏全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 沉浸式状态栏 5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(setStatusBarColor());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManagers = new SystemBarTintManager(this);
            tintManagers.setStatusBarTintEnabled(true);
            tintManagers.setStatusBarTintColor(setStatusBarColor());
        }
    }

    /** 获取主题色 */
    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //
        //LogInstance.debug(GlobalPara.Tky, "keyCode："+keyCode);
        if (keyCode == event.KEYCODE_BACK){
            return true;
        }
        else if (keyCode == event.KEYCODE_HOME){
            return true;
        }
        else if (keyCode == event.KEYCODE_MENU){
            return true;
        }
        else if (keyCode == event.KEYCODE_CAMERA){
            return true;
        }
        else if (keyCode == event.KEYCODE_SEARCH){
            return true;
        }else if (keyCode == 27){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //
        //LogInstance.debug(GlobalPara.Tky, "keyCode："+keyCode);
        if (keyCode == event.KEYCODE_BACK){
            return true;
        }
        else if (keyCode == event.KEYCODE_HOME){
            return true;
        }
        else if (keyCode == event.KEYCODE_MENU){
            return true;
        }
        else if (keyCode == event.KEYCODE_CAMERA){
            return true;
        }
        else if (keyCode == event.KEYCODE_SEARCH){
            return true;
        }
        else if (keyCode == 27){
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
        LogInstance.debug(GlobalPara.Tky, "moveTaskToFront");
    }
}
