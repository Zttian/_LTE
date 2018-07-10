package com.tky.lte.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by ttz on 2017/10/25.
 */

public abstract  class BaseUIFactory {
    protected Context mContext;
    public BaseUIFactory(Context context){
        mContext = context;
    }
    public abstract ProgressDialog CreateProgressDialog(String title);
    public abstract Dialog CreateLoadingDialog();
}
