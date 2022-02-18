package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of royalty info
 */
data class NSRoyaltyInfoResponse(
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
    var data: MutableList<NSRoyaltyInfoData>? = null
)

/**
 * The class representing the royalty info details
 */
data class NSRoyaltyInfoData(
    @SerializedName("royalty_offer_part_id")
    @Expose
    var royaltyOfferPartId: String? = null,
    @SerializedName("royalty_offer_main_id")
    @Expose
    var royaltyOfferMainId: String? = null,
    @SerializedName("repurchase_memberid")
    @Expose
    var repurchaseMemberId: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("repurchase_no")
    @Expose
    var repurchaseNo: String? = null
)