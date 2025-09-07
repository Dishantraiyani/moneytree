package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class TopUpDashboardResponse(

	@field:SerializedName("data")
	val data: TopUpDashboardData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class TopUpDashboardData(

	@field:SerializedName("total_spomsor")
	val totalSpomsor: Int? = null,

	@field:SerializedName("Sponsor Income")
	val sponsorIncome: Int? = null,

	@field:SerializedName("activation_date")
	val activationDate: String? = null,

	@field:SerializedName("Pending Voucher")
	val pendingVoucher: String? = null,

	@field:SerializedName("Topup Income")
	val topupIncome: Int? = null,

	@field:SerializedName("sponsor_id")
	val sponsorId: String? = null,

	@field:SerializedName("Total Voucher")
	val totalVoucher: String? = null,

	@field:SerializedName("pending_recevalble")
	val pendingRecevalble: Int? = null,

	@field:SerializedName("topup_status")
	val topupStatus: String? = null,

	@field:SerializedName("Lapse Voucher")
	val lapseVoucher: Int? = null,

	@field:SerializedName("dsp_id")
	val dspId: String? = null,

	@field:SerializedName("Total Share")
	val totalShare: String? = null,

	@field:SerializedName("universal Id Income")
	val universalIdIncome: Int? = null,

	@field:SerializedName("Used Voucher")
	val usedVoucher: Int? = null,

	@field:SerializedName("total_recevalble")
	val totalRecevalble: Int? = null,

	@field:SerializedName("total_payout")
	val totalPayout: Int? = null,

	@field:SerializedName("Share Value")
	val shareValue: Int? = null,

	@field:SerializedName("option")
	val option: String? = null
)
