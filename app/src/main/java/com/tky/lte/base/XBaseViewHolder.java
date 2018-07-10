package com.tky.lte.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author ttz
 *
 * @ClassName: XBaseViewHolder
 *
 * @date Created by ttz on 2017/8/29.
 *
 * @version V1.0
 *
 * @Description:  公用ViewHolder 简化控件重复创建的操作
 */

public class XBaseViewHolder {
    private int position;
    private View convertView;
    private SparseArray<View> mViews;

    public XBaseViewHolder(Context context, int position, ViewGroup parent,
                           int layoutId) {
        this.position = position;
        this.mViews = new SparseArray<View>();
        this.convertView = LayoutInflater.from(context).inflate(layoutId,
                parent, false);
        this.convertView.setTag(this);
    }

    public static XBaseViewHolder getInstance(Context context, int position,
                                              View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new XBaseViewHolder(context, position, parent, layoutId);
        }
        XBaseViewHolder viewHolder = (XBaseViewHolder) convertView.getTag();
        viewHolder.position = position;
        return viewHolder;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = convertView.findViewById(id);
            mViews.put(id, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return convertView;
    }

    public XBaseViewHolder setText(int id, String text) {
        TextView tv = getView(id);
        tv.setText(text);
        return this;
    }

    public XBaseViewHolder setTextButton(int id, String text) {
        Button btn = getView(id);
        btn.setText(text);
        return this;
    }

    public XBaseViewHolder setImageResource(int id, int resId) {
        ImageView iv = getView(id);
        iv.setImageResource(resId);
        return this;
    }

    public XBaseViewHolder setImageBitmap(int id, Bitmap bitmap) {
        ImageView iv = getView(id);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public XBaseViewHolder setImageURI(int id, Uri uri) {
        ImageView iv = getView(id);
        iv.setImageURI(uri);
        return this;
    }

    public XBaseViewHolder setImageURL(int id, String url) {
        ImageView iv = getView(id);
        // ImageLoad.laod(iv,url);
        return this;
    }
}
