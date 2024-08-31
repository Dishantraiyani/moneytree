package com.moneytree.app.ui.doctor.historyDetail

import android.app.Activity
import android.app.Application
import com.google.gson.Gson
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.SliderDoctorAdapter
import com.moneytree.app.repository.network.responses.DoctorHistoryDataItem
import com.moneytree.app.slider.IndicatorView.animation.type.IndicatorAnimationType
import com.moneytree.app.slider.SliderView


class NSDoctorHistoryDetailViewModel(application: Application) : NSViewModel(application) {

    val mFragmentList: MutableList<String> = ArrayList()
    var doctorDetail: DoctorHistoryDataItem? = null
    fun getDoctorDetail(strDetail: String?) {
        if (strDetail?.isNotEmpty() == true) {
            doctorDetail = Gson().fromJson(strDetail, DoctorHistoryDataItem::class.java)
        }
    }

    fun setupViewPager(activity: Activity, viewPager: SliderView) {
        val list = doctorDetail?.image
        val imageList = list?.split(",")

        for (image in imageList?: arrayListOf()) {
            val base = "https://moneytree.biz/upload/appointment/"
            mFragmentList.add(base + image)
        }

        val pagerAdapter = SliderDoctorAdapter(activity, mFragmentList)
        viewPager.setSliderAdapter(pagerAdapter)
        pagerAdapter.notifyDataSetChanged()
        viewPager.setIndicatorAnimation(IndicatorAnimationType.NONE)
        viewPager.setSliderTransformAnimation()
       // viewPager.startAutoCycle()
    }

}
