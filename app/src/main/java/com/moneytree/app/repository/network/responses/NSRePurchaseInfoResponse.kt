package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSRePurchaseInfoResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("nextPage")
    @Expose
    var nextPage: Boolean = false,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSRePurchaseInfoData>? = null
)

/**
 * The class representing the order details
 */
data class NSRePurchaseInfoData(
    @SerializedName("repurchase_item_id")
    @Expose
    var repurchase_item_id: String? = null,
    @SerializedName("repurchase_id")
    @Expose
    var repurchaseId: String? = null,
    @SerializedName("product_name")
    @Expose
    var productName: String? = null,
    @SerializedName("product_id")
    @Expose
    var productId: String? = null,
    @SerializedName("qty")
    @Expose
    var qty: String? = null,
    @SerializedName("rate")
    @Expose
    var rate: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
)