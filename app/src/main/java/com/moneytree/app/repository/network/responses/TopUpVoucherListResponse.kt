package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of voucher
 */
data class TopUpVoucherListResponse(
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
    var data: MutableList<TopUpSVoucherListData>? = null
)

/**
 * The class representing the trip voucher list details
 */
data class TopUpSVoucherListData(
    @SerializedName("voucher_transfer_id")
    @Expose
    var voucherTransferId: String? = null,
    @SerializedName("voucher_id")
    @Expose
    var voucherId: String? = null,
    @SerializedName("voucher_code")
    @Expose
    var voucherCode: String? = null,
    @SerializedName("memberid")
    @Expose
    var memberId: String? = null,
    @SerializedName("member_type")
    @Expose
    var memberType: String? = null,
    @SerializedName("transferid")
    @Expose
    var transferId: String? = null,
    @SerializedName("transfer_type")
    @Expose
    var transferType: String? = null,
    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null,
    @SerializedName("created_id")
    @Expose
    var createdId: String? = null,
    @SerializedName("updated_id")
    @Expose
    var updatedId: String? = null
)
