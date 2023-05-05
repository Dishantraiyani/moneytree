package com.moneytree.app.ui.home

import android.app.Activity
import com.moneytree.app.common.SliderAdapter
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.NsFragmentHomeBinding
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class HomeRepository {

	companion object {

		fun setupViewPager(activity: Activity, binding: NsFragmentHomeBinding, homeModel: NSHomeViewModel, viewPager: SliderView) {
			binding.apply {
				homeModel.apply {
					try {
						rlBanner.setVisibility(mFragmentList.isValidList())
						val pagerAdapter = SliderAdapter(activity, mFragmentList)
						viewPager.setSliderAdapter(pagerAdapter)
						pagerAdapter.notifyDataSetChanged()
						viewPager.setIndicatorAnimation(IndicatorAnimationType.NONE)
						viewPager.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
						viewPager.startAutoCycle()
					} catch (e: Exception) {
						e.printStackTrace()
					}
				}
			}
		}
	}

}
