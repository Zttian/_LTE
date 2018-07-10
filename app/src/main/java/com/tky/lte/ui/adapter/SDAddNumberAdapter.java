package com.tky.lte.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tky.lte.R;
import java.util.List;

/**
 * Created by I am on 2018/6/26.
 */

public class SDAddNumberAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public SDAddNumberAdapter(List mData){
        super(R.layout.item_sd_add_number,mData);
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvNumber,item);
    }
}
