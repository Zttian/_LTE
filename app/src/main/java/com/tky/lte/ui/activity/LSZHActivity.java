package com.tky.lte.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.constants.Config;
import com.tky.lte.lte.GroupTmpCall;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.adapter.GroupCallAdapter;
import com.tky.lte.ui.event.GroupTmpList;
import com.tky.lte.ui.event.TmpGroupBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
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
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by I am on 2018/6/23.
 * 临时群组
 */

public class LSZHActivity extends BaseActivity implements CTitleBar.CTitleBarContainer,SwipeItemClickListener {

    @BindView(R.id.swipeRecycler)
    SwipeMenuRecyclerView swipeRecycler;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    @BindView(R.id.floating_action_button)
    ImageButton floatingActionButton;
    @BindView(R.id.floating_action_button_container)
    FrameLayout floatingActionButtonContainer;
    //滑动删除
    private int direction;
    private int adapterPosition;
    private int menuPosition;
    private GroupCallAdapter groupCallAdapter;
    private ArrayList<GroupTmpCall> lstGroupTmpCalls;
    private LteHandle mLteHandle;


    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("临时群组");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_lszh;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mLteHandle = LteHandle.getInstance();
        swipeRecycler.setHasFixedSize(true);
        swipeRecycler.setSwipeMenuCreator(swipeMenuCreator);
        swipeRecycler.setItemAnimator(new DefaultItemAnimator());
        swipeRecycler.setLayoutManager(new LinearLayoutManager(this));
        swipeRecycler.setSwipeItemClickListener(this);
        swipeRecycler.setSwipeMenuItemClickListener(menuItemClickListener);

        groupCallAdapter = new GroupCallAdapter(null);
        swipeRecycler.setAdapter(groupCallAdapter);
        loadingLayout.showEmpty();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGroup();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        startCall(lstGroupTmpCalls.get(position).strGroupId);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Config.JieZuCode:
                    TmpGroupBean bean = (TmpGroupBean)msg.obj;
                    int result = bean.getnResult();
                    String szReason = bean.getSzReason();
                    if (result == 0){
                        getGroup();
                    }else{
                        ToastUtil.showLongToastSafe(szReason);
                    }
                    break;

                case Config.LszhUpdateCode://
                    GroupTmpList beanlist = (GroupTmpList)msg.obj;
                    if (beanlist != null){
                        getGroup();
                    }
                    break;
            }
        }
    };


    @Subscribe
    public void onEventBus(TmpGroupBean bean) {
        if (bean != null){
                Message msg = new Message();
                msg.what = Config.JieZuCode;
                msg.obj = bean;
                handler.sendMessage(msg);
        }
    }

    @Subscribe
    public void onEventBus(GroupTmpList bean) {
        if (bean != null){
            Message msg = new Message();
            msg.what = Config.LszhUpdateCode;
            msg.obj = bean;
            handler.sendMessage(msg);
        }
    }


    private void getGroup(){
                lstGroupTmpCalls = mLteHandle.GetTmpGroupCalls();
            if (lstGroupTmpCalls != null && lstGroupTmpCalls.size() > 0){
                groupCallAdapter.setNewData(lstGroupTmpCalls);
                loadingLayout.showContent();
            }else{
                loadingLayout.showEmpty();
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
            SwipeMenuItem seeDetailItem = new SwipeMenuItem(LSZHActivity.this)
                    .setBackground(R.drawable.selector_blue)
                    .setText("查看编辑")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(seeDetailItem);

            SwipeMenuItem deleteItem = new SwipeMenuItem(LSZHActivity.this)
                    .setBackground(R.drawable.selector_red)
                    .setText("解组")
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
                if (menuPosition == 1) {
                    lstGroupTmpCalls = (ArrayList<GroupTmpCall>) groupCallAdapter.getData();

                    //
                    if(!mLteHandle.mblPOCRegister){
                        ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                        return;
                    }

                        int ret = mLteHandle.RemoveTmpGroup(lstGroupTmpCalls.get(adapterPosition).strGroupId);
                        if (ret == -1) {
                            ToastUtil.showShortToast("删除临时组呼失败");
                        }
                }else if(menuPosition == 0){
                    Bundle b = new Bundle();
                    GroupTmpCall call = lstGroupTmpCalls.get(adapterPosition);
                    b.putSerializable("GroupTmpCall",call);
                    launchActivity(SeeGroupActivity.class,b);
                }
            }
        }
    };

    @OnClick(R.id.floating_action_button)
    public void onViewClicked() {
        launchActivity(CreateZhActivity.class);
    }


    private void startCall(String number){

        int ret = mLteHandle.StartCall(0x02, -1, number);//
        if (ret == -1) {
            ToastUtil.showShortToast("呼叫失败");
        } else {
            launchActivity(DialingActivity.class);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
