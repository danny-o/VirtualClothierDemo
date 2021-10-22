package com.digitalskies.virtualclothierdemo.utils;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class CustomStaggeredGridLayoutManager extends StaggeredGridLayoutManager {


    private boolean isScrollEnabled;

    public CustomStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }


    @Override
    public boolean canScrollHorizontally() {
        return isScrollEnabled;
    }

    public boolean isScrollEnabled() {
        return isScrollEnabled;
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        isScrollEnabled = scrollEnabled;
    }
}
