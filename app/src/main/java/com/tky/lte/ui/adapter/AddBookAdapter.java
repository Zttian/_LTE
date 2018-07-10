package com.tky.lte.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tky.lte.R;
import com.tky.lte.ui.entity.AddressBook;
import java.util.List;

/**
 * Created by ttz on 2018/5/29.
 * 通讯录
 */

public class AddBookAdapter extends BaseQuickAdapter<AddressBook,BaseViewHolder>{

    public AddBookAdapter(List mData){
        super(R.layout.item_address_book,mData);
    }
    @Override
    protected void convert(BaseViewHolder helper, AddressBook item) {
        helper.setText(R.id.tvStationName,item.getName());
        helper.setText(R.id.tvStationNumber,item.getNumber());
    }
}
