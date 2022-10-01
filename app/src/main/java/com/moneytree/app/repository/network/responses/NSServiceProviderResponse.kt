package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSServiceProviderResponse(

	@field:SerializedName("data")
	val data: List<ServiceProviderDataItem> = arrayListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class ServiceProviderDataItem(

	@field:SerializedName("optional_par")
	val optionalPar: Any? = null,

	@field:SerializedName("com_type")
	val comType: Any? = null,

	@field:SerializedName("recharge_type")
	val rechargeType: String? = null,

	@field:SerializedName("sp_key")
	val spKey: String? = null,

	@field:SerializedName("service_provider")
	val serviceProvider: String? = null,

	@field:SerializedName("min_max_amt")
	val minMaxAmt: Any? = null,

	@field:SerializedName("amt_type")
	val amtType: Any? = null,

	@field:SerializedName("ad2")
	val ad2: String? = null,

	@field:SerializedName("ad1")
	val ad1: String? = null,

	@field:SerializedName("ad3")
	val ad3: String? = null,

	@field:SerializedName("rate")
	val rate: Any? = null,

	@field:SerializedName("account_display")
	val accountDisplay: String? = null,

	@field:SerializedName("min_amt")
	val minAmt: String? = null,

	@field:SerializedName("recharge_master_id")
	val rechargeMasterId: String? = null,

	@field:SerializedName("max_amt")
	val maxAmt: String? = null,

	@field:SerializedName("par_pay")
	val parPay: Any? = null,

	@field:SerializedName("current_status")
	val currentStatus: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
