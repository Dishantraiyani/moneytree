package com.moneytree.app.common

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.moneytree.app.R
import com.bumptech.glide.Glide
import androidx.viewpager.widget.ViewPager

class SliderAdapter(private val context: Context, private val mItems: List<String>) :
    PagerAdapter() {
    override fun getCount(): Int {
        return mItems.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val images: ImageView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.row_slider, container, false)
        images = itemView.findViewById(R.id.image)
        Glide.with(context).load(mItems[position]).into(images)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}
