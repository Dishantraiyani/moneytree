package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class CreditPayoutSummaryResponse(
	
	@field:SerializedName("summary")
	val summary: List<SummaryItem>? = null,
	
	@field:SerializedName("data")
	val data: List<SummaryDataItem>? = null,
	
	@field:SerializedName("nextPage")
	val nextPage: Boolean? = null,
	
	@field:SerializedName("release_dates")
	val releaseDates: List<ReleaseDatesItem>? = null,
	
	@field:SerializedName("message")
	val message: String? = null,
	
	@field:SerializedName("status")
	val status: Boolean? = null
)

data class SummaryDataItem(

	@field:SerializedName("admin_charge")
	val adminCharge: String? = null,

	@field:SerializedName("coin_amount")
	val coinAmount: String? = null,

	@field:SerializedName("payout_name")
	val payoutName: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("payout_release_date")
	val payoutReleaseDate: String? = null,

	@field:SerializedName("ov_credit_payout_id")
	val ovCreditPayoutId: String? = null,

	@field:SerializedName("entry_date")
	val entryDate: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class SummaryItem(

	@field:SerializedName("coin_amount")
	val coinAmount: String? = null,

	@field:SerializedName("released_cnt")
	val releasedCnt: String? = null,

	@field:SerializedName("pending_cnt")
	val pendingCnt: String? = null,

	@field:SerializedName("total_cnt")
	val totalCnt: String? = null,

	@field:SerializedName("released_amount")
	val releasedAmount: String? = null,

	@field:SerializedName("next_release_date")
	val nextReleaseDate: String? = null,

	@field:SerializedName("gross_amount")
	val grossAmount: String? = null,

	@field:SerializedName("net_amount")
	val netAmount: String? = null,

	@field:SerializedName("pending_coin")
	val pendingCoin: String? = null,

	@field:SerializedName("admin_amount")
	val adminAmount: String? = null,

	@field:SerializedName("released_coin")
	val releasedCoin: String? = null,

	@field:SerializedName("pending_amount")
	val pendingAmount: String? = null
)

data class ReleaseDatesItem(

	@field:SerializedName("coin_amount")
	val coinAmount: String? = null,

	@field:SerializedName("cnt")
	val cnt: String? = null,

	@field:SerializedName("payout_release_date")
	val payoutReleaseDate: String? = null,

	@field:SerializedName("net_amount")
	val netAmount: String? = null
)
