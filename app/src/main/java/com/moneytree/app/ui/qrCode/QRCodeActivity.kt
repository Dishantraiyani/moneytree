package com.moneytree.app.ui.qrCode

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityQrCodeBinding

class QRCodeActivity : NSActivity() {
	private lateinit var qrBinding: ActivityQrCodeBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		qrBinding = ActivityQrCodeBinding.inflate(layoutInflater)
		setContentView(qrBinding.root)
		loadInitialFragment(intent.extras!!)
	}

	/**
	 * To initialize qr code fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle) {
		replaceCurrentFragment(NSQRCodeFragment.newInstance(bundle), false, qrBinding.qrContainer.id)
	}
}
