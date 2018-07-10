package com.tky.lte.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.db.CallJiDao;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.activity.DialingActivity;
import com.tky.lte.ui.adapter.CallJilAdapter;
import com.tky.lte.ui.entity.CallJilEntity;
import com.tky.lte.ui.event.CallJilBean;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ttz on 2018/5/29.
 * 通话记录
 */

public class CallJilFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.swipeRecycler)
    SwipeMenuRecyclerView swipeRecycler;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;

    private CallJilAdapter callJilAdapter;
    private List<CallJilEntity> callJilEntities;

    //滑动删除
    private int direction;
    private int adapterPosition;
    private int menuPosition;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_call_ji_l;
    }

    @Override
    protected void init() {
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layout.setReverseLayout(true);//列表翻转
        swipeRecycler.setLayoutManager(layout);

        swipeRecycler.setHasFixedSize(true);
        swipeRecycler.setSwipeMenuCreator(swipeMenuCreator);
        swipeRecycler.setItemAnimator(new DefaultItemAnimator());

        swipeRecycler.setSwipeItemClickListener(this);
        swipeRecycler.setSwipeMenuItemClickListener(menuItemClickListener);
        callJilAdapter = new CallJilAdapter(null);
        callJilAdapter.openLoadAnimation();
        swipeRecycler.setAdapter(callJilAdapter);
        loadingLayout.showEmpty();
    }


    @Override
    public void onItemClick(View itemView, int position) {
        LteHandle mLteHandle = LteHandle.getInstance();

        //
        String funNumber = newCallJilEntities.get(position).getFunNumber();
        String peerNumber = newCallJilEntities.get(position).getPeerNumber();
        int iCallType = newCallJilEntities.get(position).getCallType();
        if(iCallType == 1){
            if (!TextUtils.isEmpty(funNumber)) {

                //
                if(!mLteHandle.mblPOCRegister){
                    ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                    return;
                }

                int ret = mLteHandle.StartCall(0x01, -1, funNumber);
                if (ret == -1) {
                    ToastUtil.showShortToast("呼叫失败");
                } else {
                    launchActivity(DialingActivity.class);
                }
            } else {
                //
                if(!mLteHandle.mblPOCRegister){
                    ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                    return;
                }

                int ret = mLteHandle.StartCall(0x01, -1, peerNumber);
                if (ret == -1) {
                    ToastUtil.showShortToast("呼叫失败");
                } else {
                    launchActivity(DialingActivity.class);
                }
            }
        }else{

            //
            if(!mLteHandle.mblPOCRegister){
                ToastUtil.showShortToastSafe("网络受限,无法进行操作");
                return;
            }

            int ret = mLteHandle.StartCall(0x02, -1, peerNumber);
            if (ret == -1) {
                ToastUtil.showShortToast("呼叫失败");
            } else {
                launchActivity(DialingActivity.class);
            }
        }


    }

    ArrayList<CallJilEntity> newCallJilEntities = new ArrayList<CallJilEntity>();//

    @Override
    public void onResume() {
        super.onResume();
        callJilEntities = CallJiDao.queryAll();

        //
        newCallJilEntities.clear();
        if (callJilEntities != null && callJilEntities.size() > 0){
            for(CallJilEntity cj : callJilEntities){
                boolean bFlag = false;
                for(CallJilEntity ncj : newCallJilEntities){
                    if(ncj.getCallType() == cj.getCallType() && (ncj.getPeerNumber().equals(cj.getPeerNumber()) || ncj.getFunNumber().equals(cj.getFunNumber())) ) {
                        bFlag = true;
                        break;
                    }
                }
                if(!bFlag){
                    newCallJilEntities.add(cj);
                }
            }
        }
        if (newCallJilEntities != null && newCallJilEntities.size() > 0) {
            callJilAdapter.setNewData(newCallJilEntities);
            loadingLayout.showContent();
        }else{
            loadingLayout.showEmpty();
        }


    }

    //菜单创建器，在Item要创建菜单的时候调用
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.common_dimen_90dp);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                    .setBackground(R.drawable.selector_red)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);
        }
    };

    // RecyclerView的Item的Menu点击监听
    private SwipeMenuItemClickListener menuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {//判断左滑还是右滑
                if (menuPosition == 0) {
                    newCallJilEntities =(ArrayList<CallJilEntity>) callJilAdapter.getData();
                    CallJilEntity callJilEntity = newCallJilEntities.get(adapterPosition);
                    List<CallJilEntity> list = new ArrayList<>();
                    if (callJilEntities != null && callJilEntities.size() > 0){
                        for (int i=0;i<callJilEntities.size();i++){
                            CallJilEntity callJil = callJilEntities.get(i);

                            if(callJilEntity.getCallType() == 1) {
                                if (callJilEntity.getCallType() == callJil.getCallType()) {
                                    if (!callJilEntity.getFunNumber().equals("")) {
                                        if (callJilEntity.getFunNumber().equals(callJil.getFunNumber())) {
                                            list.add(callJil);
                                        }
                                    } else {
                                        if (callJilEntity.getPeerNumber().equals(callJil.getPeerNumber())) {
                                            list.add(callJil);
                                        }
                                    }
                                }
                            }else {
                                if (callJilEntity.getCallType() == callJil.getCallType()) {
                                    if (callJilEntity.getPeerNumber().equals(callJil.getPeerNumber())) {
                                        list.add(callJil);
                                    }
                                }
                            }

                        }
                        CallJiDao.deleteCallJilList(list);
                        callJilAdapter.remove(adapterPosition);
                    }
                }
            }
        }
    };
}
