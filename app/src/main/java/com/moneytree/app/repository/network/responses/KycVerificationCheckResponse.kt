package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class KycVerificationCheckResponse(

	@field:SerializedName("data")
	val data: List<KycDataItem> = arrayListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class KycDataItem(

	@field:SerializedName("img")
	val img: String? = null,

	@field:SerializedName("response_data")
	val responseData: String? = null,

	@field:SerializedName("entrydate")
	val entrydate: String? = null,

	@field:SerializedName("kyc_type")
	val kycType: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("memberid")
	val memberId: String? = null
)
