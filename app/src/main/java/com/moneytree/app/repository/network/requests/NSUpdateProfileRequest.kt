package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of update profile
 */
data class NSUpdateProfileRequest(
    @SerializedName("fullname")
    @Expose
    var fullName: String? = null,
    @SerializedName("address")
    @Expose
    var address: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,
    @SerializedName("panno")
    @Expose
    var panno: String? = null,
    @SerializedName("ifsc_code")
    @Expose
    var ifscCode: String? = null,
    @SerializedName("bank_name")
    @Expose
    var bankName: String? = null,
    @SerializedName("ac_no")
    @Expose
    var acNo: String? = null
)