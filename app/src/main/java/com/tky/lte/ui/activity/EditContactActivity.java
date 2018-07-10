package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.db.ABookDao;
import com.tky.lte.ui.entity.AddressBook;
import com.tky.lte.ui.event.EditBookBean;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by I am on 2018/6/14.
 */

public class EditContactActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.et_Csz)
    ClearEditText etCsz;
    @BindView(R.id.et_Czm)
    ClearEditText etCzm;
    @BindView(R.id.btAdd)
    Button btAdd;
    private String name;
    private String number;
    private int position;
    private AddressBook book;


    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("编辑联系人");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_new_contact;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        btAdd.setText("修改");
        book = (AddressBook)getIntent().getSerializableExtra("bean");
//
//        name = getIntent().getStringExtra("name");
//        number = getIntent().getStringExtra("number");
//        position = getIntent().getIntExtra("position",-1);
        if (book != null){
            etCsz.setText(book.getName());
            etCzm.setText(book.getNumber());
        }
    }


    @OnClick(R.id.btAdd)
    public void onViewClicked() {
        if (TextUtils.isEmpty(etCsz.getText().toString())){
            ToastUtil.showShortToastSafe("请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(etCzm.getText().toString())){
            ToastUtil.showShortToastSafe("请输入号码");
            return;
        }
        book.setNumber(etCzm.getText().toString());
        book.setName(etCsz.getText().toString());
        ABookDao.updateAddressBook(book);

//        EditBookBean editBookBean = new EditBookBean();
//        editBookBean.setName(etCsz.getText().toString());
//        editBookBean.setNumber(etCzm.getText().toString());
//        editBookBean.setPosition(position + "");
//        EventBus.getDefault().post(editBookBean);
        finish();
    }
}
