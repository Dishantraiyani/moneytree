package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class StateResponse(

	@field:SerializedName("data")
	val data: MutableList<StateDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class StateDataItem(

	@field:SerializedName("state_name")
	val stateName: String = ""
)
