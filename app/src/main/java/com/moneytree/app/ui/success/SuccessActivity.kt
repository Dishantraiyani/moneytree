package com.moneytree.app.ui.success

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivitySuccessBinding


class SuccessActivity : NSActivity() {
	private lateinit var successBinding: ActivitySuccessBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		successBinding = ActivitySuccessBinding.inflate(layoutInflater)
		setContentView(successBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize home fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(SuccessFragment.newInstance(bundle), false, successBinding.successContainer.id)
	}
}
