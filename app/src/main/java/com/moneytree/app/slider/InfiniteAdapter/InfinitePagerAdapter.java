package com.moneytree.app.slider.InfiniteAdapter;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.moneytree.app.slider.SliderViewAdapter;


public class InfinitePagerAdapter extends PagerAdapter {

    public static final int INFINITE_SCROLL_LIMIT = 32400;
    private SliderViewAdapter adapter;

    public InfinitePagerAdapter(SliderViewAdapter adapter) {
        this.adapter = adapter;
    }

    public PagerAdapter getRealAdapter() {
        return this.adapter;
    }

    @Override
    public int getCount() {
        if (getRealCount() < 1) {
            return 0;
        }
        return getRealCount() * INFINITE_SCROLL_LIMIT;
    }

    public int getRealCount() {
        try {
            return getRealAdapter().getCount();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMiddlePosition(int item) {
       int midpoint = Math.max(0, getRealCount()) * (InfinitePagerAdapter.INFINITE_SCROLL_LIMIT / 2);
        return item + midpoint;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int virtualPosition) {
        // prevent division by zer
        if (getRealCount() < 1) {
            return adapter.instantiateItem(container, 0);
        }
        return adapter.instantiateItem(container, getRealPosition(virtualPosition));
    }

    @Override
    public void destroyItem(ViewGroup container, int virtualPosition, Object object) {
        if (getRealCount() < 1) {
            adapter.destroyItem(container, 0, object);
            return;
        }
        adapter.destroyItem(container, getRealPosition(virtualPosition), object);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        adapter.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        adapter.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return adapter.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        adapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return adapter.saveState();
    }

    @Override
    public CharSequence getPageTitle(int virtualPosition) {
        return adapter.getPageTitle(getRealPosition(virtualPosition));
    }

    @Override
    public float getPageWidth(int position) {
        return adapter.getPageWidth(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        adapter.setPrimaryItem(container, position, object);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public int getItemPosition(Object object) {
        return adapter.getItemPosition(object);
    }

    public int getRealPosition(int virtualPosition) {
        if (getRealCount() > 0) {
            return virtualPosition % getRealCount();
        }
        return 0;
    }
}