package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class Repurchase30000Response(

	@field:SerializedName("data")
	val data: Repurchase3000Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class ActivationListItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("activated_by")
	val activatedBy: String? = null,

	@field:SerializedName("expiry_date")
	val expiryDate: String? = null,

	@field:SerializedName("voucher_id")
	val voucherId: String? = null,

	@field:SerializedName("voucher_code")
	val voucherCode: String? = null,

	@field:SerializedName("activated_by_name")
	val activatedByName: String? = null,

	@field:SerializedName("sponsor_id")
	val sponsorId: String? = null,

	@field:SerializedName("entry_date")
	val entryDate: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DownlineSummary(

	@field:SerializedName("total_downline")
	val totalDownline: Int? = null,

	@field:SerializedName("not_activated")
	val notActivated: Int? = null,

	@field:SerializedName("activated")
	val activated: Int? = null
)

data class Repurchase3000Data(

	@field:SerializedName("activation_list")
	val activationList: List<ActivationListItem>? = null,

	@field:SerializedName("downline_list")
	val downLineList: List<Repurchase3000DownLineListItem>? = null,

	@field:SerializedName("my_status")
	val myStatus: MyStatus? = null,

	@field:SerializedName("downline_summary")
	val downLineSummary: DownlineSummary? = null
)

data class Repurchase3000DownLineListItem(

	@field:SerializedName("member_id")
	val memberId: String? = null,

	@field:SerializedName("expiry_date")
	val expiryDate: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("member_name")
	val memberName: String? = null,

	@field:SerializedName("entry_date")
	val entryDate: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class MyStatus(

	@field:SerializedName("expiry_date")
	val expiryDate: String? = null,

	@field:SerializedName("voucher_code")
	val voucherCode: String? = null,

	@field:SerializedName("sponsor_id")
	val sponsorId: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("entry_date")
	val entryDate: String? = null
)
