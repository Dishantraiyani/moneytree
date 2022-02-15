package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSTransactionListResponse(
    @SerializedName("data")
    @Expose
    var orderData: MutableList<NSTransactionListData>? = null
)

/**
 * The class representing the order details
 */
data class NSTransactionListData(
    @SerializedName("order_id")
    @Expose
    var orderId: String? = null,
    @SerializedName("is_credit")
    @Expose
    var isCredit: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("total")
    @Expose
    var total: Double? = null
)