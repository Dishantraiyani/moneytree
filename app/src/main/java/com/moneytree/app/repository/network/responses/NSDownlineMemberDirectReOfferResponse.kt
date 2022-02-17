package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSDownlineMemberDirectReOfferResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSDownlineMemberDirectReOfferData>? = null
)

/**
 * The class representing the order details
 */
data class NSDownlineMemberDirectReOfferData(
    @SerializedName("memberid")
    @Expose
    var memberid: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("colour")
    @Expose
    var colour: String? = null
)