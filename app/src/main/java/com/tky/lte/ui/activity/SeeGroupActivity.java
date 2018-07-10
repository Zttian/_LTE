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
import android.widget.ImageButton;
import android.widget.TextView;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.constants.Config;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.lte.GroupTmpCall;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.adapter.SDAddNumberAdapter;
import com.tky.lte.ui.event.AddNumberBean;
import com.tky.lte.ui.event.TmpGroupBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;
import com.tky.lte.widget.LoadingLayout;
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
 * Created by I am on 2018/6/30.
 */

public class SeeGroupActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {


    @BindView(R.id.tvZhhm)
    TextView tvZhhm;
    @BindView(R.id.tvCreate)
    TextView tvCreate;
    @BindView(R.id.etYxj)
    ClearEditText etYxj;
    @BindView(R.id.swipeRecycler)
    SwipeMenuRecyclerView swipeRecycler;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    @BindView(R.id.floating_action_button)
    ImageButton floatingActionButton;
    @BindView(R.id.btSubmit)
    Button btSubmit;

    private SDAddNumberAdapter addNumberAdapter;

    private int direction;
    private int adapterPosition;
    private int menuPosition;
    private  ArrayList<String> lstMemberList;
    private GroupTmpCall groupTmpCall;


    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("查看成员");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_see_group;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        swipeRecycler.setHasFixedSize(true);
        swipeRecycler.setSwipeMenuCreator(swipeMenuCreator);
        swipeRecycler.setItemAnimator(new DefaultItemAnimator());
        swipeRecycler.setLayoutManager(new LinearLayoutManager(this));
        swipeRecycler.setSwipeMenuItemClickListener(menuItemClickListener);
        addNumberAdapter = new SDAddNumberAdapter(null);
        addNumberAdapter.openLoadAnimation();
        swipeRecycler.setAdapter(addNumberAdapter);
        loadingLayout.showEmpty();

        groupTmpCall = (GroupTmpCall)getIntent().getSerializableExtra("GroupTmpCall");
        tvZhhm.setText(groupTmpCall.strGroupId);
        tvCreate.setText(groupTmpCall.strRequester);
        etYxj.setText(groupTmpCall.iPriority + "");
        lstMemberList = groupTmpCall.lstMemberList;

        if (lstMemberList != null && lstMemberList.size() >0){
            addNumberAdapter.setNewData(lstMemberList);
            loadingLayout.showContent();
        }
    }

    @Subscribe
    public void onEventBus(AddNumberBean bean) {
        if (bean != null){
            ArrayList<String> numbers = bean.getNumbers();
            addNumberAdapter.addData(numbers);
        }
    }

    @Subscribe
    public void onEventBus(TmpGroupBean bean) {
        if (bean != null){
            int result= bean.getnResult();
            String sReason = bean.getSzReason();
            if (result == 0){
                Message message = new Message();
                message.obj = "";
                message.what = result;
                handler.sendMessage(message);
            }else{
                Message message = new Message();
                message.what = result;
                message.obj = sReason;
                handler.sendMessage(message);
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int result = msg.what;
            String sReason = (String)msg.obj;
            if (result == 0){
                ToastUtil.showShortToast("修改成功");
                finish();
            }else{
                ToastUtil.showShortToast(sReason);
            }
        }
    };


    @OnClick({R.id.btSubmit, R.id.floating_action_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btSubmit:
                if(!lstMemberList.contains(GlobalPara.strPhoneId))
                    lstMemberList.add(GlobalPara.strPhoneId);
                if (lstMemberList.size() < 2){
                    ToastUtil.showShortToastSafe("成员至少大于2人");
                    return;
                }

                if (TextUtils.isEmpty(etYxj.getText().toString())){
                    ToastUtil.showShortToast("优先级不能为空");
                    return;
                }

                 LteHandle.getInstance().ModifyTmpGroup(tvZhhm.getText().toString(),Integer.valueOf(etYxj.getText().toString()),lstMemberList);
                break;
            case R.id.floating_action_button:
                launchActivity(SeeGroupNumberActivity.class);
                break;
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

            SwipeMenuItem deleteItem = new SwipeMenuItem(SeeGroupActivity.this)
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


                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
