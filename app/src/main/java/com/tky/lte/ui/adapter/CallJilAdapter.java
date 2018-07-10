package com.tky.lte.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tky.lte.R;
import com.tky.lte.ui.entity.CallJilEntity;
import java.util.List;

/**
 * Created by I am on 2018/6/23.
 */

public class CallJilAdapter extends BaseQuickAdapter<CallJilEntity,BaseViewHolder> {

    public CallJilAdapter(List mData){
        super(R.layout.item_call_jil,mData);
    }
    @Override
    protected void convert(BaseViewHolder helper, CallJilEntity item) {
        String funNumber = item.getFunNumber();
        int callType = item.getCallType();
        int call_way = item.getCall_way();
        TextView  tvFunNumber = helper.getView(R.id.tvFunNumber);
        TextView  tvFunNumberName = helper.getView(R.id.tvFunNumberName);
        TextView  tvPeerNumber = helper.getView(R.id.tvPeerNumber);
        ImageView headImgG = helper.getView(R.id.headImgG);
        ImageView headImgZ = helper.getView(R.id.headImgZ);

        //
        if(callType == 1){
            if (!TextUtils.isEmpty(funNumber)){
                tvFunNumber.setVisibility(View.VISIBLE);
                tvFunNumberName.setVisibility(View.VISIBLE);
                tvPeerNumber.setVisibility(View.GONE);
                helper.setText(R.id.tvFunNumber,funNumber);
                helper.setText(R.id.tvFunNumberName,item.getFunNumberName());
            }else{
                tvFunNumber.setVisibility(View.GONE);
                tvFunNumberName.setVisibility(View.GONE);
                tvPeerNumber.setVisibility(View.VISIBLE);
                helper.setText(R.id.tvPeerNumber,item.getPeerNumber());
            }
        }else {
            tvFunNumber.setVisibility(View.VISIBLE);
            tvFunNumberName.setVisibility(View.VISIBLE);
            tvPeerNumber.setVisibility(View.GONE);
            helper.setText(R.id.tvFunNumber,item.getPeerNumber());
            helper.setText(R.id.tvFunNumberName,item.getFunNumberName());
        }


        if (callType == 1){
            headImgG.setVisibility(View.VISIBLE);
            headImgZ.setVisibility(View.GONE);
        }else{
            headImgG.setVisibility(View.GONE);
            headImgZ.setVisibility(View.VISIBLE);
        }
        if (call_way == 1){
            helper.setImageResource(R.id.imgHr,R.drawable.huru);
        }else{
            helper.setImageResource(R.id.imgHr,R.drawable.huchu);
        }
    }
}
