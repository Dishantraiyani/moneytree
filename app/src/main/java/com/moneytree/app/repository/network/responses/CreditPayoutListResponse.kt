package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of rePurchase list
 */
data class CreditPayoutListResponse(
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
    var data: MutableList<CreditPayoutData>? = null,
    @SerializedName("summary")
    @Expose
    var summary: MutableList<CreditPayoutSummary>? = null
)

data class CreditPayoutData(
    @SerializedName("payout_no")
    @Expose
    var payoutNo: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("bank_holder_name")
    @Expose
    var bankHolderName: String? = null,
    @SerializedName("bank_name")
    @Expose
    var bankName: String? = null,
    @SerializedName("ac_no")
    @Expose
    var acNo: String? = null,
    @SerializedName("ifsc_code")
    @Expose
    var ifscCode: String? = null,
    @SerializedName("payment_status")
    @Expose
    var paymentStatus: String? = null,
    @SerializedName("remark")
    @Expose
    var remark: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("tds_amount")
    @Expose
    var tdsAmount: String? = null,
    @SerializedName("net_amount")
    @Expose
    var netAmount: String? = null
)

data class CreditPayoutSummary(
    @SerializedName("total_cnt")
    @Expose
    var totalCnt: String? = null,
    @SerializedName("total_amount")
    @Expose
    var totalAmount: String? = null,
    @SerializedName("tds_amount")
    @Expose
    var tdsAmount: String? = null,
    @SerializedName("pending_amount")
    @Expose
    var pendingAmount: String? = null,
    @SerializedName("accepted_amount")
    @Expose
    var acceptedAmount: String? = null,
    @SerializedName("rejected_amount")
    @Expose
    var rejectedAmount: String? = null,
    @SerializedName("net_amount")
    @Expose
    var netAmount: String? = null
)