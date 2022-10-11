package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of verify otp
 */
data class NSQRCodeRequest(
	@SerializedName("qr_user_id")
	@Expose
	var qrUserId: String?,
    @SerializedName("amount")
    @Expose
    var amount: String?,
    @SerializedName("note")
    @Expose
    var note: String?
)
