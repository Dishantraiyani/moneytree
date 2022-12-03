package com.moneytree.app.ui.youtube.detail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityYoutubeViewBinding

class YoutubeViewActivity : NSActivity() {
	private lateinit var binding: ActivityYoutubeViewBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityYoutubeViewBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize home fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(NSYoutubeViewFragment.newInstance(bundle), false, binding.youtubeContainer.id)
	}
}
