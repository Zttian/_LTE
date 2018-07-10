package com.tky.lte.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.ui.adapter.SDAddNumberAdapter;
import com.tky.lte.ui.event.AddNumberBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by I am on 2018/6/30.
 */

public class SeeGroupNumberActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.et_Jcm)
    ClearEditText etJcm;
    @BindView(R.id.btAdd)
    Button btAdd;
    @BindView(R.id.swipeRecycler)
    SwipeMenuRecyclerView swipeRecycler;
    @BindView(R.id.btSubmit)
    Button btSubmit;
    private ArrayList<String> numbers = new ArrayList<>();
    private String number;
    private SDAddNumberAdapter addNumberAdapter;

    private int direction;
    private int adapterPosition;
    private int menuPosition;


    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("添加组员");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_sd_add;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        btSubmit.setVisibility(View.GONE);
        swipeRecycler.setHasFixedSize(true);
        swipeRecycler.setSwipeMenuCreator(swipeMenuCreator);
        swipeRecycler.setItemAnimator(new DefaultItemAnimator());
        swipeRecycler.setLayoutManager(new LinearLayoutManager(this));
        swipeRecycler.setSwipeMenuItemClickListener(menuItemClickListener);
        addNumberAdapter = new SDAddNumberAdapter(null);
        addNumberAdapter.openLoadAnimation();
        swipeRecycler.setAdapter(addNumberAdapter);
    }


    @OnClick({R.id.btAdd, R.id.btSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btAdd:
                number = etJcm.getText().toString();
                if (TextUtils.isEmpty(number)){
                    ToastUtil.showShortToastSafe("请输入号码");
                    return;
                }
                Message message = new Message();
                message.what = 1;
                message.obj = number;
                handler.sendMessage(message);
                break;
            case R.id.btSubmit:
                AddNumberBean bean = new AddNumberBean();

                bean.setNumbers(numbers);
                EventBus.getDefault().post(bean);
                finish();
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String number = (String)msg.obj;

                    //
                    if(numbers.contains(number)){
                        ToastUtil.showShortToastSafe("新增成员号码重复");
                        return;
                    }else{
                        numbers.add(number);
                    }

                    addNumberAdapter.setNewData(numbers);
                    btSubmit.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    if (numbers != null && numbers.size() > 0){
                        btSubmit.setVisibility(View.VISIBLE);
                    }else{
                        btSubmit.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    /**
     * 菜单创建器，在Item要创建菜单的时候调用
     **/
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.common_dimen_90dp);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem deleteItem = new SwipeMenuItem(SeeGroupNumberActivity.this)
                    .setBackground(R.drawable.selector_red)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听
     **/
    private SwipeMenuItemClickListener menuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {//判断左滑还是右滑
                if (menuPosition == 0) {
                    addNumberAdapter.remove(adapterPosition);
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
            }
        }
    };
}
