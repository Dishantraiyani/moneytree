package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of voucher
 */
data class TopUpVoucherAvailableListResponse(
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
    var data: MutableList<TopUpSVoucherAvailableListData>? = null
)

/**
 * The class representing the trip voucher list details
 */
data class TopUpSVoucherAvailableListData(
    @SerializedName("voucher_id")
    @Expose
    var voucherId: String? = null,
    @SerializedName("voucher_code")
    @Expose
    var voucherCode: String? = null
)
