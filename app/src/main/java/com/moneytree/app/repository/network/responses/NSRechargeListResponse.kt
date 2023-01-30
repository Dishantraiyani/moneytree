package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSRechargeListResponse(

	@field:SerializedName("data")
	val data: List<RechargeListDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class RechargeListDataItem(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("note")
	val note: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("transaction_status")
	val transactionStatus: String? = null,

	@field:SerializedName("recharge_type")
	val rechargeType: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("service_provider")
	val serviceProvider: String? = null,

	@field:SerializedName("ad2")
	val ad2: String? = null,

	@field:SerializedName("rep_data")
	val repData: String? = null,

	@field:SerializedName("ad1")
	val ad1: String? = null,

	@field:SerializedName("recharge_id")
	val rechargeId: String? = null,

	@field:SerializedName("ad3")
	val ad3: String? = null,

	@field:SerializedName("account_display")
	val accountDisplay: String? = null,

	@field:SerializedName("memberid")
	val memberid: String? = null,

	@field:SerializedName("invoice")
	var invoice: String? = null,

	@field:SerializedName("qr_scan_url")
	var qrScanUrl: String? = null
)
