package com.rodrigo.lock.app.mvp.viewVault;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by Rodrigo on 28/12/2016.
 */

public class StaggeredListDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public StaggeredListDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        //if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        //} else {
        //    outRect.top = 0;
        //}
    }
}