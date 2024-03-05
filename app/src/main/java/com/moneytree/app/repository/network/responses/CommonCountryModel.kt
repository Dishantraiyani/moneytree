package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class CommonCountryModel(

	@field:SerializedName("title")
	val title: String = "",

	@field:SerializedName("code")
	val code: String = "",

	@field:SerializedName("selected_code")
	val selectedCode: String = ""
)