package com.tky.lte.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.tky.lte.R;
import com.tky.lte.base.BaseFragment;
import com.tky.lte.db.ABookDao;
import com.tky.lte.lte.LteHandle;
import com.tky.lte.ui.activity.DialingActivity;
import com.tky.lte.ui.activity.EditContactActivity;
import com.tky.lte.ui.activity.NewContactActivity;
import com.tky.lte.ui.adapter.AddBookAdapter;
import com.tky.lte.ui.entity.AddressBook;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.LoadingLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by ttz on 2018/5/29.
 * 通讯录
 */

public class AddressBookFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.swipeRecycler)
    SwipeMenuRecyclerView swipeRecycler;
    @BindView(R.id.floating_action_button)
    ImageButton floatingActionButton;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;

    private List<AddressBook> bookEntities;
    private AddBookAdapter addBookAdapter;
    //滑动删除
    private int direction;
    private int adapterPosition;
    private int menuPosition;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_address_book;
    }

    @Override
    protected void init() {
        swipeRecycler.setHasFixedSize(true);
        swipeRecycler.setSwipeMenuCreator(swipeMenuCreator);
        swipeRecycler.setItemAnimator(new DefaultItemAnimator());
        swipeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRecycler.setSwipeItemClickListener(this);
        swipeRecycler.setSwipeMenuItemClickListener(menuItemClickListener);
        //先初始化
        addBookAdapter = new AddBookAdapter(null);
        addBookAdapter.openLoadAnimation();
        swipeRecycler.setAdapter(addBookAdapter);
        loadingLayout.showEmpty();
    }

    @Override
    public void onResume() {
        super.onResume();
        bookEntities = ABookDao.queryAll();
        if (bookEntities != null && bookEntities.size() > 0) {
            addBookAdapter.setNewData(bookEntities);
            loadingLayout.showContent();
        }else{
            loadingLayout.showEmpty();
        }
    }

    @Override
    public void onItemClick(View itemView, int position) {
        bookEntities = addBookAdapter.getData();
        LteHandle mLteHandle = LteHandle.getInstance();

        //
        if(!mLteHandle.mblPOCRegister){
            ToastUtil.showShortToastSafe("网络受限,无法进行操作");
            return;
        }

        int ret = mLteHandle.StartCall(0x01, -1, bookEntities.get(position).getNumber());
        if (ret == -1) {
            ToastUtil.showShortToast("呼叫失败");
        } else {
            launchActivity(DialingActivity.class);
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
                    .setBackground(R.drawable.selector_blue)
                    .setText("编辑")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(editItem);// 添加菜单到右侧

            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
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
            bookEntities = addBookAdapter.getData();
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {//判断左滑还是右滑
                if (menuPosition == 0) {
                    Bundle b = new Bundle();
                    b.putSerializable("bean", bookEntities.get(adapterPosition));
                    launchActivity(EditContactActivity.class, b);
                } else if (menuPosition == 1) {
                    ABookDao.deleteAddressBook(bookEntities.get(adapterPosition));
                    addBookAdapter.remove(adapterPosition);
                }
            }
        }
    };

    @OnClick(R.id.floating_action_button)
    public void onViewClicked() {
        launchActivity(NewContactActivity.class);
    }

}
