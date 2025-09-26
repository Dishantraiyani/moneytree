package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of voucher
 */
data class PendingVoucherListResponse(
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
    var data: MutableList<PendingVoucherListData>? = null
)

/**
 * The class representing the trip voucher list details
 */
data class PendingVoucherListData(
    @SerializedName("topup_28_voucher_id")
    @Expose
    var topup28VoucherId: String? = null,
    @SerializedName("voucher_name")
    @Expose
    var voucherName: String? = null,
    @SerializedName("voucher_month")
    @Expose
    var voucherMonth: String? = null,
    @SerializedName("remark")
    @Expose
    var remark: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null
)
