package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSRetailListResponse(
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
    var data: MutableList<NSRetailListData>? = null
)

/**
 * The class representing the order details
 */
data class NSRetailListData(
    @SerializedName("direct_retail_offer_main_id")
    @Expose
    var directRetailOfferMainId: String? = null,
    @SerializedName("payout_no")
    @Expose
    var payoutNo: String? = null,
    @SerializedName("entry_from")
    @Expose
    var entryFrom: String? = null,
    @SerializedName("entry_to")
    @Expose
    var entryTo: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
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