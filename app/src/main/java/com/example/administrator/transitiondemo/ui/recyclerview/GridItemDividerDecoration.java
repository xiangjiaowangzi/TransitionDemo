package com.example.administrator.transitiondemo.ui.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by LiuB on 2017/10/26.
 */

public class GridItemDividerDecoration extends RecyclerView.ItemDecoration {

    private final int dividerSize;
    private final Paint paint;
    private boolean isDrawDivider;

    public GridItemDividerDecoration(int dividerSize,
                                     @ColorInt int dividerColor) {
        this.dividerSize = dividerSize;
        paint = new Paint();
        paint.setColor(dividerColor);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.isAnimating()) return;

        final int childCount = parent.getChildCount();
        final RecyclerView.LayoutManager lm = parent.getLayoutManager();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);
            if (isdrawDivider()) {
                //获取view的范围
                final int right = lm.getDecoratedRight(child);
                final int bottom = lm.getDecoratedBottom(child);
                //画底部条
                canvas.drawRect(lm.getDecoratedLeft(child),
                        bottom - dividerSize,
                        right,
                        bottom,
                        paint);
                //画右边条
                canvas.drawRect(right - lm.getDecoratedLeft(child),
                        lm.getDecoratedTop(child),
                        right,
                        bottom - dividerSize,
                        paint);
            }
        }

    }

    private boolean isdrawDivider() {
        return isDrawDivider;
    }

}
