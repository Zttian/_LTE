package com.tky.lte.util;

import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ListView;
import android.widget.TextView;
import com.tky.lte.R;

/**
 * Created by I am on 2018/6/6.
 */

public class ViewUtil {
    private ViewUtil() {}

    /**
     * Returns the width as specified in the LayoutParams
     * @throws IllegalStateException Thrown if the view's width is unknown before a layout pass
     * s
     */
    public static int getConstantPreLayoutWidth(View view) {
        // We haven't been layed out yet, so get the size from the LayoutParams
        final ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p.width < 0) {
            throw new IllegalStateException("Expecting view's width to be a constant rather " +
                    "than a result of the layout pass");
        }
        return p.width;
    }

    /**
     * Returns a boolean indicating whether or not the view's layout direction is RTL
     *
     * @param view - A valid view
     * @return True if the view's layout direction is RTL
     */
    public static boolean isViewLayoutRtl(View view) {
        return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static final ViewOutlineProvider OVAL_OUTLINE_PROVIDER = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    };

    /**
     * Configures the floating action button, clipping it to a circle and setting its translation z.
     * @param view The float action button's view.
     * @param res The resources file.
     */
    public static void setupFloatingActionButton(View view, Resources res) {
        view.setOutlineProvider(OVAL_OUTLINE_PROVIDER);
        view.setTranslationZ(
                res.getDimensionPixelSize(R.dimen.common_dimen_8dp));
    }

    /**
     * Adds padding to the bottom of the given {@link ListView} so that the floating action button
     * does not obscure any content.
     *
     * @param listView to add the padding to
     * @param res valid resources object
     */
    public static void addBottomPaddingToListViewForFab(ListView listView, Resources res) {
        final int fabPadding = res.getDimensionPixelSize(R.dimen.common_dimen_88dp);
        listView.setPaddingRelative(listView.getPaddingStart(), listView.getPaddingTop(),
                listView.getPaddingEnd(), listView.getPaddingBottom() + fabPadding);
        listView.setClipToPadding(false);
    }

    public static void resizeText(TextView textView, int originalTextSize, int minTextSize) {
        final Paint paint = textView.getPaint();
        final int width = textView.getWidth();
        if (width == 0) return;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSize);
        float ratio = width / paint.measureText(textView.getText().toString());
        if (ratio <= 1.0f) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    Math.max(minTextSize, originalTextSize * ratio));
        }
    }

}
