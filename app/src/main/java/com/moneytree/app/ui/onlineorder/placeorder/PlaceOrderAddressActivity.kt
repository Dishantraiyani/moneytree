package com.moneytree.app.ui.onlineorder.placeorder

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import com.google.gson.Gson
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.callbacks.NSPaymentDetailCallback
import com.moneytree.app.common.callbacks.NSPaymentFragmentCallback
import com.moneytree.app.databinding.ActivityCommonBinding
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class PlaceOrderAddressActivity : NSActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityCommonBinding
    private var paymentCallback: NSPaymentDetailCallback? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        val fragment = PlaceOrderAddressFragment.newInstance(bundle, object : NSPaymentFragmentCallback {
            override fun onResponse(callback: NSPaymentDetailCallback) {
                paymentCallback = callback
            }
        })
        
        replaceCurrentFragment(fragment, false, binding.commonContainer.id)
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
