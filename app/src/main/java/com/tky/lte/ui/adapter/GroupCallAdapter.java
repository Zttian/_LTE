package com.tky.lte.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tky.lte.R;
import com.tky.lte.lte.GroupTmpCall;

import java.util.List;

/**
 * Created by I am on 2018/6/25.
 * 临时组呼列表
 */

public class GroupCallAdapter extends BaseQuickAdapter<GroupTmpCall,BaseViewHolder> {

    public GroupCallAdapter(List mData){
        super(R.layout.item_group_call,mData);
    }
    @Override
    protected void convert(BaseViewHolder helper, GroupTmpCall item) {
        helper.setText(R.id.tvGroupName,item.strGroupName);
        helper.setText(R.id.tvNumber,item.strGroupId);
        helper.setText(R.id.tvPriority,"(" + item.iPriority + "级)");
    }
}
