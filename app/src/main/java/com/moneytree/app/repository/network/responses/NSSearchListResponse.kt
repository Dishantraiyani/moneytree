package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSSearchListResponse(

	@field:SerializedName("status")
	val status: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("data")
	val data: MutableList<SearchData> = arrayListOf()
)

data class SearchData(

	@field:SerializedName("search_name")
	val searchName: String? = null,
)
