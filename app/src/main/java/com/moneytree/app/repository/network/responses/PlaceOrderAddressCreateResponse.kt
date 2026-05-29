package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class PlaceOrderAddressCreateResponse(
    @SerializedName("full_name")
    @Expose
    var fullName: String = "",
    @SerializedName("mobile")
    @Expose
    var mobile: String = "",
    @SerializedName("email")
    @Expose
    var email: String = "",
    @SerializedName("address")
    @Expose
    var address: String = "",
    @SerializedName("pin_code")
    @Expose
    val pinCode: String = "",
    @SerializedName("city")
    @Expose
    val city: String = "",
    @SerializedName("district")
    @Expose
    val district: String = "",
    @SerializedName("state")
    @Expose
    var state: String = "",
    @SerializedName("set_as_defualt")
    @Expose
    var setAsDefault: String = "N"
)