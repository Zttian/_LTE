package com.tky.lte.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tky.lte.R;
import com.tky.lte.ui.entity.AddressBook;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by I am on 2018/6/26.
 */

public class MineRadioAdapter extends RecyclerView.Adapter<MineRadioAdapter.ViewHolder> {

    private static final int MYLIVE_MODE_CHECK = 0;
    int mEditMode = MYLIVE_MODE_CHECK;

    private Context context;
    private List<AddressBook> addBookList;
    private OnItemClickListener mOnItemClickListener;

    public MineRadioAdapter(Context context) {
        this.context = context;
    }


    public void notifyAdapter(List<AddressBook> addBookList, boolean isAdd) {
        if (!isAdd) {
            this.addBookList = addBookList;
        } else {
            this.addBookList.addAll(addBookList);
        }
        notifyDataSetChanged();
    }

    public List<AddressBook> getAdapterBookList() {
        if (addBookList == null) {
            addBookList = new ArrayList<>();
        }
        return addBookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_live, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return addBookList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AddressBook book = addBookList.get(holder.getAdapterPosition());
        holder.tvStationName.setText(book.getName());
        holder.tvStationNumber.setText(book.getNumber());

        if (mEditMode == MYLIVE_MODE_CHECK) {
            holder.mCheckBox.setVisibility(View.GONE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);

            if (book.getIsSelect()) {
                holder.mCheckBox.setImageResource(R.mipmap.ic_checked);
            } else {
                holder.mCheckBox.setImageResource(R.mipmap.ic_uncheck);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), addBookList);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClickListener(int pos,List<AddressBook> myLiveList);
    }
    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.check_box)
        ImageView mCheckBox;
        @BindView(R.id.root_view)
        RelativeLayout mRootView;
        @BindView(R.id.tvStationName)
        TextView tvStationName;
        @BindView(R.id.tvStationNumber)
        TextView tvStationNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
