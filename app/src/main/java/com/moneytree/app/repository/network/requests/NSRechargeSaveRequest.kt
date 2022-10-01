package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of verify otp
 */
data class NSRechargeSaveRequest(
    @SerializedName("recharge_type")
    @Expose
    var rechargeType: String?,
    @SerializedName("service_provider")
    @Expose
    var serviceProvider: String?,
	@SerializedName("service_provider_title")
	@Expose
	var serviceProviderTitle: String?,
	@SerializedName("account_display")
	@Expose
	var accountDisplay: String?,
	@SerializedName("account_display_title")
	@Expose
	var accountDisplayTitle: String?,
	@SerializedName("amount")
	@Expose
	var amount: String?,
	@SerializedName("note")
	@Expose
	var note: String?,
	@SerializedName("ad1")
	@Expose
	var ad1: String?,
	@SerializedName("ad2")
	@Expose
	var ad2: String?,
	@SerializedName("ad3")
	@Expose
	var ad3: String?
)
