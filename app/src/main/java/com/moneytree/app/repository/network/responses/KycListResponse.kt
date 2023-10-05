package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class KycListResponse(

	@field:SerializedName("key")
	val key: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)
