package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class VoucherList30000Response(

	@field:SerializedName("data")
	val data: List<VoucherList30000DataItem>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class VoucherList30000DataItem(

	@field:SerializedName("claim_status")
	val claimStatus: String? = null,

	@field:SerializedName("expiry_date")
	val expiryDate: String? = null,

	@field:SerializedName("target_repurchase")
	val targetRepurchase: String? = null,

	@field:SerializedName("voucher_amount")
	val voucherAmount: String? = null,

	@field:SerializedName("repurchase_done")
	val repurchaseDone: String? = null,

	@field:SerializedName("claim_remark")
	val claimRemark: String? = null,

	@field:SerializedName("entry_date")
	val entryDate: String? = null
)
