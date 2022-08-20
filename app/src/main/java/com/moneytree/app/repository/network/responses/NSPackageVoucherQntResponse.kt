package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of package list
 */
data class NSPackageVoucherQntResponse(
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
    var data: MutableList<NSPackageVoucherData> = arrayListOf()
)

data class NSPackageVoucherData(
    @SerializedName("voucher_id")
    @Expose
    var voucherId: String? = null,
    @SerializedName("voucher_code")
    @Expose
    var voucherCode: String? = null
)
