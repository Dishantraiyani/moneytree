package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of verify otp
 */
data class VoucherTransferModel(
	@SerializedName("transferid")
    @Expose
    var transferId: String?,
	@SerializedName("transfer_qty")
    @Expose
    var transferQty: String?,
	@SerializedName("payment_type")
	@Expose
	var paymentType: String?
)
