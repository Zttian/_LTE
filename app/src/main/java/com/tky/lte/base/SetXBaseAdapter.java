package com.tky.lte.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collection;
import java.util.List;

/**
 * Created by ttz on 2017/8/29.
 */

public abstract class SetXBaseAdapter<T> extends BaseAdapter {

    public Context context;
    public List<T> mDatas;
    private int layoutId;

    public SetXBaseAdapter(Context context, List<T> mDatas, int layoutId) {
        this.context = context;
        this.mDatas = mDatas;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        XBaseViewHolder holder = XBaseViewHolder.getInstance(context, position,
                convertView, parent, layoutId);
        getView(holder, mDatas.get(position), position);
        return holder.getConvertView();

    }

    public abstract void getView(XBaseViewHolder viewHolder, T bean,
                                 int position);

    public void replaceAll(Collection<T> collection) {
        mDatas.clear();
        if (collection != null) {
            mDatas.addAll(collection);
        }
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> collection) {
        if (collection != null) {
            mDatas.addAll(collection);
        }
        notifyDataSetChanged();
    }

    public void addItem(T e) {
        mDatas.add(e);
        notifyDataSetChanged();
    }

    public void addAllItem(List<T> list) {
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    public void removeItem(T e) {
        mDatas.remove(e);
        notifyDataSetChanged();
    }

    public void removeAllItem(List<T> list) {
        mDatas.removeAll(list);
        notifyDataSetChanged();
    }

    public List<T> getAllItem() {
        return mDatas;
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

}
