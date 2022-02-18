package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of retail info
 */
data class NSRetailInfoResponse(
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
    var data: MutableList<NSRetailInfoData>? = null
)

/**
 * The class representing the retail details
 */
data class NSRetailInfoData(
    @SerializedName("direct_retail_offer_part_id")
    @Expose
    var directRetailOfferPartId: String? = null,
    @SerializedName("direct_retail_offer_main_id")
    @Expose
    var directRetailOfferMainId: String? = null,
    @SerializedName("level")
    @Expose
    var level: String? = null,
    @SerializedName("percentage")
    @Expose
    var percentage: String? = null,
    @SerializedName("repurchase_memberid")
    @Expose
    var repurchaseMemberid: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("repurchase_id")
    @Expose
    var repurchaseId: String? = null
)