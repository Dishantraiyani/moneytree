package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of royalty
 */
data class NSRoyaltyListResponse(
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
    var data: MutableList<NSRoyaltyListData>? = null
)

/**
 * The class representing the royalty list details
 */
data class NSRoyaltyListData(
    @SerializedName("royalty_offer_main_id")
    @Expose
    var royaltyOfferMainId: String? = null,
    @SerializedName("payout_no")
    @Expose
    var payoutNo: String? = null,
    @SerializedName("entry_date")
    @Expose
    var entryDate: String? = null,
    @SerializedName("memberid")
    @Expose
    var memberId: String? = null,
    @SerializedName("entry_from")
    @Expose
    var entryFrom: String? = null,
    @SerializedName("entry_to")
    @Expose
    var entryTo: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("percentage")
    @Expose
    var percentage: String? = null,
    @SerializedName("percentage_total")
    @Expose
    var percentageTotal: String? = null,
    @SerializedName("sponsor_cnt")
    @Expose
    var sponsorCnt: String? = null,
    @SerializedName("tds")
    @Expose
    var tds: String? = null,
    @SerializedName("admin_charges")
    @Expose
    var adminCharges: String? = null,
    @SerializedName("total")
    @Expose
    var total: String? = null
)