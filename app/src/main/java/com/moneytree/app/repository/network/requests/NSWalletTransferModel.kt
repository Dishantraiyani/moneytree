package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of verify otp
 */
data class NSWalletTransferModel(
	@SerializedName("transactionId")
    @Expose
    var transactionId: String?,
	@SerializedName("password")
    @Expose
    var password: String?,
	@SerializedName("remark")
	@Expose
	var remark: String?,
	@SerializedName("amount")
	@Expose
	var amount: String?,
	@SerializedName("is_voucher_available")
	@Expose
	var isVoucherAvailable: Boolean? = false,
	@SerializedName("package_id")
	@Expose
	var packageId: String?,
	@SerializedName("voucherQty")
	@Expose
	var voucherQty: Int? = 0,
	@SerializedName("select_transfer_from")
	@Expose
	var selectTransferFrom: String? = null,
)
