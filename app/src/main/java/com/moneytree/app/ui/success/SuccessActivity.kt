package com.moneytree.app.ui.success

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding


class SuccessActivity : NSActivity() {
	private lateinit var binding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize home fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(SuccessFragment.newInstance(bundle), false, binding.commonContainer.id)
	}
}
