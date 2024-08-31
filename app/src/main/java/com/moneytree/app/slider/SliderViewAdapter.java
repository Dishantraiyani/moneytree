package com.moneytree.app.slider;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.LinkedList;
import java.util.Queue;


public abstract class SliderViewAdapter<VH extends SliderViewAdapter.ViewHolder> extends PagerAdapter {

    private DataSetListener dataSetListener;

    public static abstract class ViewHolder {
        public final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

    private Queue<VH> destroyedItems = new LinkedList<>();

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        VH viewHolder = destroyedItems.poll();
        if (viewHolder == null) {
            viewHolder = onCreateViewHolder(container);
        }
        container.addView(viewHolder.itemView);
        onBindViewHolder(viewHolder, position);
        return viewHolder;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(((VH) object).itemView);
        destroyedItems.add((VH) object);
    }

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((VH) object).itemView == view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (this.dataSetListener != null) {
            dataSetListener.dataSetChanged();
        }
    }

    public abstract VH onCreateViewHolder(ViewGroup parent);

    public abstract void onBindViewHolder(VH viewHolder, int position);

    void dataSetChangedListener(DataSetListener dataSetListener) {
        this.dataSetListener = dataSetListener;
    }

    interface DataSetListener {
        void dataSetChanged();
    }
}
