package com.tky.lte.ui.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.constants.Config;
import com.tky.lte.db.ABookDao;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.adapter.CancelAdapter;
import com.tky.lte.ui.event.FunctionNumListBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.LoadingLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by ttz on 2018/5/30.
 * 注销
 */

public class CancelFragment extends BaseFragment implements SwipeItemClickListener {


    @BindView(R.id.swipeRecycler)
    SwipeMenuRecyclerView swipeRecycler;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    //滑动删除
    private int direction;
    private int adapterPosition;
    private int menuPosition;
    private CancelAdapter cancelAdapter;
    private ArrayList<String> list;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_cancel;
    }

    @Override
    protected void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        swipeRecycler.setHasFixedSize(true);
        swipeRecycler.setSwipeMenuCreator(swipeMenuCreator);
        swipeRecycler.setItemAnimator(new DefaultItemAnimator());
        swipeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRecycler.setSwipeItemClickListener(this);
        swipeRecycler.setSwipeMenuItemClickListener(menuItemClickListener);
        cancelAdapter = new CancelAdapter(null);
        cancelAdapter.openLoadAnimation();
        swipeRecycler.setAdapter(cancelAdapter);
        loadingLayout.showEmpty();
    }

    @Override
    public void onItemClick(View itemView, int position) {

    }

    private Handler  handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.CancelCode:
                    FunctionNumListBean bean = (FunctionNumListBean)msg.obj;
                    int resultCode = bean.getResultCode();
                    list = bean.getFuncNumList();
                    if (resultCode == 0){
                        cancelAdapter.setNewData(list);
                        loadingLayout.showContent();
                    }else{
                        loadingLayout.showEmpty();
                    }
                    break;
            }
        }
    };


    @Subscribe
    public void onEventBus(FunctionNumListBean bean) {
       if (bean != null){
           Message msg = new Message();
           msg.what = Config.CancelCode;
           msg.obj = bean;
           handler.sendMessage(msg);
       }
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用
     **/
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.common_dimen_90dp);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem editItem = new SwipeMenuItem(getActivity())
                    .setBackground(R.drawable.selector_red)
                    .setText("注销")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(editItem);// 添加菜单到右侧
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
                     LteHandle.getInstance().FunctionNumberRegisterOrNot(list.get(adapterPosition).toString(),false);
                    LteHandle.getInstance().QueryFunctionNumberList();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (LteHandle.getInstance().QueryFunctionNumberList() == -1){
            loadingLayout.showError();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
