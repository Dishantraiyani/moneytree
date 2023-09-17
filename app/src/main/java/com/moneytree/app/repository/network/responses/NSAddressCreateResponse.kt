package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSAddressCreateResponse(
    @SerializedName("full_name")
    @Expose
    var fullName: String = "",
    @SerializedName("mobile")
    @Expose
    var mobile: String = "",
	@SerializedName("flat_house")
	val flatHouse: String = "",
    @SerializedName("area")
    @Expose
    var area: String = "",
    @SerializedName("landmark")
    @Expose
    var landMark: String = "",
    @SerializedName("pin_code")
    @Expose
    val pinCode: String = "",
    @SerializedName("city")
    @Expose
    val city: String = "",
    @SerializedName("state")
    @Expose
    var state: String = "",
    @SerializedName("county")
    @Expose
    var country: String = ""
)