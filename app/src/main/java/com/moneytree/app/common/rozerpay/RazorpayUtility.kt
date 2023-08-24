package com.moneytree.app.common.rozerpay

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.moneytree.app.repository.network.responses.RozerModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class RazorpayUtility(private val context: Activity) {

    fun startPayment(rozerModel: RozerModel) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_live_2hfV6yS6Cv9ri8")

        val options = JSONObject()
        rozerModel.apply {
            options.put(bidDetails.name, productName)
            val description: String = bidDetails.description
            options.put(description, productName)
            options.put(bidDetails.sendSmsHash, false)
            options.put(bidDetails.allowRotation, false)
            options.put(bidDetails.image, image)
            options.put(bidDetails.currency, bidDetails.currencyValue)
            options.put(bidDetails.amount, price!!.toInt() * 100)
            val preFill = JSONObject()
            preFill.put(bidDetails.email, email)
            preFill.put(bidDetails.contact, mobile)
            options.put(bidDetails.prefill, preFill)
        }
        try {
            checkout.open(context, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}