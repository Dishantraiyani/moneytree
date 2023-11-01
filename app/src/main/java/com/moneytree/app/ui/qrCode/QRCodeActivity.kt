package com.moneytree.app.ui.qrCode

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class QRCodeActivity : NSActivity() {
	private lateinit var binding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment(intent.extras!!)
	}

	/**
	 * To initialize qr code fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle) {
		replaceCurrentFragment(NSQRCodeFragment.newInstance(bundle), false, binding.commonContainer.id)
	}
}
