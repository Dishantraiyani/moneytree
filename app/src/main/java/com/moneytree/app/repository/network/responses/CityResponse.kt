package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class CityResponse(

	@field:SerializedName("NSCityResponse")
	val nSCityResponse: List<CityResponseItem?>? = null
)

data class CityResponseItem(

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state_id")
	val stateId: Int? = null,

	@field:SerializedName("state_code")
	val stateCode: String? = null,

	@field:SerializedName("country_id")
	val countryId: Int? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)
