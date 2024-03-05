package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class DistrictResponse(

	@field:SerializedName("data")
	val data: MutableList<DistrictDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class DistrictDataItem(

	@field:SerializedName("district_name")
	val districtName: String = "",

	@field:SerializedName("state_name")
	val stateName: String = ""
)
