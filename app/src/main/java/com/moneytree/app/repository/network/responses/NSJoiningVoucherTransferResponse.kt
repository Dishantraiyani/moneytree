package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*{"status":true,"message":"","voucher_count":4,"data":[{"username":"6353336889","mobile":"8735985994","email":""}]}*/

/**
 * The class representing the response body of user
 */
data class NSJoiningVoucherTransferResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("voucher_count")
    @Expose
    var voucherCount: Int = 0,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSJoiningVoucherTransferData>? = null
)

/**
 * The class representing the order details
 */
data class NSJoiningVoucherTransferData(
    @SerializedName("username")
    @Expose
    var username: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null
)