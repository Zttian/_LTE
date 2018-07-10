package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.event.AddNumberBean;
import com.tky.lte.ui.event.TmpGroupBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.util.Utils;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/6/23.
 * 创建临时组呼
 */

public class CreateZhActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.et_Mc)
    ClearEditText etMc;
    @BindView(R.id.et_Yxj)
    ClearEditText etYxj;
    @BindView(R.id.et_tian)
    ClearEditText etTian;
    @BindView(R.id.et_xs)
    ClearEditText etXs;
    @BindView(R.id.et_wjzsfsj)
    ClearEditText etWjzsfsj;
    @BindView(R.id.btSubmit)
    Button btSubmit;
    @BindView(R.id.fbTxlAdd)
    FloatingActionButton fbTxlAdd;
    @BindView(R.id.fbSdAdd)
    FloatingActionButton fbSdAdd;
    @BindView(R.id.menu_Float)
    FloatingActionMenu menuFloat;
    public static final int ResultCode = 105;
    private  LteHandle mLteHandle;
    private ArrayList<String> objMemberList;


    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("创建临时组呼");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_create_zh;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Utils.initFloatMenu(menuFloat);
        mLteHandle = LteHandle.getInstance();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ResultCode:
                    TmpGroupBean bean = (TmpGroupBean)msg.obj;
                    int result = bean.getnResult();
                    String reason = bean.getSzReason();
                    if (result == 0){
                        finish();
                    }else{
                        ToastUtil.showLongToastSafe(reason);
                    }
                    break;
            }
        }
    };


    @Subscribe
    public void onEventBus(TmpGroupBean bean) {
        if (bean != null){
            Message msg = new Message();
            msg.what = ResultCode;
            msg.obj = bean;
            handler.sendMessage(msg);
        }
    }

    @Subscribe
    public void onEventBus(AddNumberBean bean) {
        if (bean != null){
            objMemberList = bean.getNumbers();
        }
    }

    @OnClick({R.id.fbTxlAdd, R.id.fbSdAdd,R.id.btSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fbTxlAdd:
                menuFloat.close(true);
                launchActivity(SelectBookActivity.class);
                break;
            case R.id.fbSdAdd:
                menuFloat.close(true);
                launchActivity(SDAddNumberActivity.class);
                break;
            case R.id.btSubmit:
                if (TextUtils.isEmpty(etMc.getText().toString())){
                    ToastUtil.showShortToastSafe("名称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etYxj.getText().toString())){
                    ToastUtil.showShortToastSafe("优先级不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etTian.getText().toString())){
                    ToastUtil.showShortToastSafe("天数不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etXs.getText().toString())){
                    ToastUtil.showShortToastSafe("小时不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etWjzsfsj.getText().toString())){
                    ToastUtil.showShortToastSafe("无讲者释放时长不能为空");
                    return;
                }

                //
                if (objMemberList == null || objMemberList.size() == 0){
                    ToastUtil.showShortToastSafe("请先添加临时组呼成员");
                    return;
                }

                String mc = etMc.getText().toString();
                int yxj = Integer.valueOf(etYxj.getText().toString());
                int tian = Integer.valueOf(etTian.getText().toString());
                int xs = Integer.valueOf(etXs.getText().toString());
                int wjzsfsc = Integer.valueOf(etWjzsfsj.getText().toString());
                int bzsc = tian * 24 + xs;
                mLteHandle = LteHandle.getInstance();

                //
                if(!mLteHandle.mblPOCRegister){
                    ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                    return;
                }

                int ret = mLteHandle.CreateTmpGroup(mc,yxj,wjzsfsc,bzsc, objMemberList);
                if (ret == -1) {
                    ToastUtil.showShortToast("创建临时组呼失败");
                }
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
