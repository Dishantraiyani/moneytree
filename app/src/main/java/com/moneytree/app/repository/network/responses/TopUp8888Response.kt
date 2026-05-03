package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class TopUp8888Response(

	@field:SerializedName("data")
	val data: TopUpData? = null,

	@field:SerializedName("is_payment_mode")
	val isPaymentMode: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DownlineListItem(

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("username")
	val username: String? = null,
	
	@field:SerializedName("activation_id")
	val activationId: String? = null,
	
	@field:SerializedName("activation_sponsor_id")
	val activationSponsorId: String? = null,
	
	@field:SerializedName("voucher_code")
	val voucherCode: String? = null,
	
	@field:SerializedName("activated_at")
	val activatedAt: String? = null,
	
	@field:SerializedName("activation_status")
	val activationStatus: String? = null
)

data class TopUpData(
	
	@field:SerializedName("self_activation")
	val selfActivation: Any? = null,

	@field:SerializedName("downline_list")
	val downlineList: List<DownlineListItem>? = null,

	@field:SerializedName("available_pin")
	val availablePin: Int? = null
)

