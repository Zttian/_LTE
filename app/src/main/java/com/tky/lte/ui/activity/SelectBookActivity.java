package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.db.ABookDao;
import com.tky.lte.lte.GlobalPara;
import com.tky.lte.ui.adapter.MineRadioAdapter;
import com.tky.lte.ui.entity.AddressBook;
import com.tky.lte.ui.event.AddNumberBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by I am on 2018/6/26.
 */

public class SelectBookActivity extends BaseActivity implements CTitleBar.CTitleBarContainer,MineRadioAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_select_num)
    TextView mTvSelectNum;
    @BindView(R.id.select_all)
    TextView mSelectAll;
    @BindView(R.id.tvFinish)
    TextView tvFinish;

    private MineRadioAdapter mRadioAdapter = null;
    private List<AddressBook> bookList;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;
    private ArrayList<String> numbers = new ArrayList<>();


    @Override
    public void initializeTitleBar(final CTitleBar titleBar) {
        titleBar.setTitle("通讯录");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.select_book_list;
    }


    @Override
    protected void init(Bundle savedInstanceState) {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRadioAdapter = new MineRadioAdapter(this);
        mRecyclerView.setAdapter(mRadioAdapter);
        bookList = ABookDao.queryAll();
        mRadioAdapter.notifyAdapter(bookList, false);

        mRadioAdapter.setOnItemClickListener(this);
        editorStatus = true;
        mRadioAdapter.setEditMode(1);
    }


    @OnClick({R.id.select_all, R.id.tvFinish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_all:
                selectAllMain();
                break;
            case R.id.tvFinish:
                if (bookList != null && bookList.size() > 0) {
                    for (int i = 0; i < bookList.size(); i++) {
                        if (bookList.get(i).getIsSelect()) {
                            numbers.add(bookList.get(i).getNumber());
                        }
                    }

                    //
                    ArrayList<String> newList = new ArrayList<String>();
                    Iterator it = numbers.iterator();
                    while(it.hasNext()){
                        String obj = (String)it.next();
                        if(!newList.contains(obj)){
                            newList.add(obj);
                        }
                    }
                    if(!newList.contains(GlobalPara.strPhoneId))
                        newList.add(GlobalPara.strPhoneId);
                    if (newList !=null && newList.size() <= 1){
                        ToastUtil.showShortToastSafe("请至少添加一名其他成员");
                        return;
                    }
                    AddNumberBean bean = new AddNumberBean();
                    bean.setNumbers(newList);
                    EventBus.getDefault().post(bean);
                    finish();
                }
                break;
        }
    }


    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (mRadioAdapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = mRadioAdapter.getAdapterBookList().size(); i < j; i++) {
                mRadioAdapter.getAdapterBookList().get(i).setIsSelect(true);
            }
            index = mRadioAdapter.getAdapterBookList().size();
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = mRadioAdapter.getAdapterBookList().size(); i < j; i++) {
                mRadioAdapter.getAdapterBookList().get(i).setIsSelect(false);
            }
            index = 0;
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        mRadioAdapter.notifyDataSetChanged();
        mTvSelectNum.setText(String.valueOf(index));
    }


    @Override
    public void onItemClickListener(int pos, List<AddressBook> list) {
        if (editorStatus) {
            AddressBook book = list.get(pos);
            boolean isSelect = book.getIsSelect();
            if (!isSelect) {
                index++;
                book.setIsSelect(true);
                if (index == list.size()) {
                    isSelectAll = true;
                    mSelectAll.setText("取消全选");
                }
            } else {
                book.setIsSelect(false);
                index--;
                isSelectAll = false;
                mSelectAll.setText("全选");
            }
            mTvSelectNum.setText(String.valueOf(index));
            mRadioAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookList != null && bookList.size() > 0) {
            for (int i = 0; i < bookList.size(); i++) {
                AddressBook book = bookList.get(i);
                book.setIsSelect(false);
            }
        }
    }
}
