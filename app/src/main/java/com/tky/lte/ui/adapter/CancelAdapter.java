package com.tky.lte.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tky.lte.R;
import java.util.List;

/**
 * Created by I am on 2018/6/24.
 */

public class CancelAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public CancelAdapter(List mData){
        super(R.layout.item_cancel,mData);
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvFunctionNumber,item);
    }
}
