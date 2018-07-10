package com.tky.lte.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import com.tky.lte.R;
import com.tky.lte.util.ViewUtil;

/**
 * Created by I am on 2018/6/6.
 */

public class ResizingTextEditText extends AppCompatEditText {
    private final int mOriginalTextSize;
    private final int mMinTextSize;

    public ResizingTextEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOriginalTextSize = (int) getTextSize();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResizingText);
        mMinTextSize = (int) a.getDimension(R.styleable.ResizingText_resizing_text_min_size,
                mOriginalTextSize);
        a.recycle();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        ViewUtil.resizeText(this, mOriginalTextSize, mMinTextSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ViewUtil.resizeText(this, mOriginalTextSize, mMinTextSize);
    }
}
