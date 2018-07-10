package com.tky.lte.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import com.tky.lte.R;
import com.tky.lte.base.BaseActivity;
import com.tky.lte.db.ABookDao;
import com.tky.lte.ui.entity.AddressBook;
import com.tky.lte.util.ToastUtil;
import com.tky.lte.widget.CTitleBar;
import com.tky.lte.widget.ClearEditText;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ttz on 2018/6/14.
 */

public class NewContactActivity extends BaseActivity implements CTitleBar.CTitleBarContainer {

    @BindView(R.id.et_Csz)
    ClearEditText etCsz;
    @BindView(R.id.et_Czm)
    ClearEditText etCzm;
    @BindView(R.id.btAdd)
    Button btAdd;

    @Override
    public void initializeTitleBar(CTitleBar titleBar) {
        titleBar.setTitle("新建联系人");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_new_contact;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @OnClick(R.id.btAdd)
    public void onViewClicked() {
        if (TextUtils.isEmpty(etCsz.getText().toString())){
            ToastUtil.showShortToastSafe("请输入用户名");
            return;
        }

        if (TextUtils.isEmpty(etCzm.getText().toString())){
            ToastUtil.showShortToastSafe("请输入号码");
            return;
        }

        AddressBook book = new AddressBook();
        book.setName(etCsz.getText().toString());
        book.setNumber(etCzm.getText().toString());
        ABookDao.insertAddressBook(book);
        finish();
    }
}
