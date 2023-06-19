package com.moneytree.app.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSWelcomePreferences
import com.moneytree.app.common.ViewPagerMDAdapter
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.ActivityWelcomeLayoutBinding
import com.moneytree.app.ui.login.NSLoginActivity

class WelcomeActivity : NSActivity() {
	private lateinit var binding: ActivityWelcomeLayoutBinding
	val mFragmentList: MutableList<Fragment> = ArrayList()
	var isDone: Boolean = false
	var circles: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		binding = ActivityWelcomeLayoutBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment()
		setListener()
    }

	/**
	 * To initialize home fragment
	 *
	 */
	private fun loadInitialFragment() {
		//buildCircles()
		setFragmentData()
	}

	private fun setListener() {
		binding.btnNext.setOnClickListener {
			if (isDone) {
				NSWelcomePreferences(this).isWelcome = true
				switchActivity(
					NSLoginActivity::class.java,
					flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
				)
				finish()
			} else {
				binding.pager.currentItem = binding.pager.currentItem + 1
			}
		}
	}

	private fun setFragmentData() {
		mFragmentList.clear()
		mFragmentList.add(NSWelcomeFragment.newInstance(R.layout.fragment_screen1))
		mFragmentList.add(NSWelcomeFragment.newInstance(R.layout.fragment_screen2))
		mFragmentList.add(NSWelcomeFragment.newInstance(R.layout.fragment_screen3))
		mFragmentList.add(NSWelcomeFragment.newInstance(R.layout.fragment_screen4))
		setupViewPager(binding.pager)
	}

	private fun setupViewPager(viewPager: ViewPager2) {
		with(binding) {
			val adapter = ViewPagerMDAdapter(this@WelcomeActivity)
			adapter.setFragment(mFragmentList)
			viewPager.adapter = adapter
			viewPager.offscreenPageLimit = 3
			viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
				override fun onPageSelected(position: Int) {
					super.onPageSelected(position)
					//setIndicator(position)
					if (position >= mFragmentList.size - 1) {
						isDone = true
						binding.btnNext.text = "Done"
					} else {
						isDone = false
						binding.btnNext.text = "Next"
					}
				}
			})

			TabLayoutMediator(tabLayout, pager) { tab, position ->
				//Some implementation
			}.attach()
		}
	}
}
