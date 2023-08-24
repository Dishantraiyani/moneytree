package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

class RozerModel {
    @SerializedName("image")
    var image: String? = null

    @SerializedName("start_time")
    var startTime: String? = null

    @SerializedName("price")
    var price: String? = null

    @SerializedName("end_time")
    var endTime: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("mobile")
    var mobile: String? = null

    @SerializedName("created_date")
    var createdDate: String? = null

    @SerializedName("product_name")
    var productName: String? = null

    @SerializedName("is_featured")
    var isFeatured: String? = null
    var isNativeAds = false
    val bidDetails = RozerDetails()

    inner class RozerDetails(
        val name: String = "name",
        val description: String = "description",
        val sendSmsHash: String = "send_sms_hash",
        val allowRotation: String = "allow_rotation",
        val image: String = "image",
        val currency: String = "currency",
        val amount: String = "amount",
        val email: String = "email",
        val contact: String = "contact",
        val prefill: String = "prefill",
        val currencyValue: String = "INR"
    )
}