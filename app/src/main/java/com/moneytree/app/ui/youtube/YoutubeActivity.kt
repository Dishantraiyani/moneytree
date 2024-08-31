package com.moneytree.app.ui.youtube

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class YoutubeActivity : NSActivity() {
	private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment()
    }

	/**
	 * To initialize home fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSYoutubeFragment.newInstance(), false, binding.commonContainer.id)
	}
}
