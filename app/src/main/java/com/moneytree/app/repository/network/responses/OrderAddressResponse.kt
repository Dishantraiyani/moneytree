package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class OrderAddressResponse(

	@field:SerializedName("area")
	val area: String? = null,

	@field:SerializedName("full_name")
	val fullName: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("pin_code")
	val pinCode: String? = null,

	@field:SerializedName("county")
	val county: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("flat_house")
	val flatHouse: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("landmark")
	val landmark: String? = null
)
