package com.moneytree.app.ui.recharge.rechargePayment

import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.callbacks.NSPaymentDetailCallback
import com.moneytree.app.common.callbacks.NSPaymentFragmentCallback
import com.moneytree.app.databinding.ActivityRozerBinding
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class RozerActivity : NSActivity(), PaymentResultWithDataListener {
	private lateinit var qrBinding: ActivityRozerBinding
	private var paymentCallback: NSPaymentDetailCallback? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		qrBinding = ActivityRozerBinding.inflate(layoutInflater)
		setContentView(qrBinding.root)
		Checkout.preload(this)
		loadInitialFragment(intent.extras!!)
	}

	/**
	 * To initialize qr code fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle) {
		val fragment = RozerFragment.newInstance(bundle, object : NSPaymentFragmentCallback {
			override fun onResponse(callback: NSPaymentDetailCallback) {
				paymentCallback = callback
			}
		})

		replaceCurrentFragment(fragment, false, qrBinding.rozerContainer.id)
	}

	override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
		Log.d("TAG", "onPaymentSuccess: ")
		paymentCallback?.onResponse(p0?:"", Gson().toJson(p1), true)
	}

	override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
		Log.d("TAG", "onPaymentSuccess: ")
		paymentCallback?.onResponse(p1?:"", Gson().toJson(p2), false)
	}
}
