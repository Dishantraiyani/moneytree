package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of register
 */
data class NSRegisterListResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("nextPage")
    @Expose
    var nextPage: Boolean = false,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSRegisterListData>? = null
)

/**
 * The class representing the register list details
 */
data class NSRegisterListData(
    @SerializedName("user_id")
    @Expose
    var userId: String? = null,
    @SerializedName("fullname")
    @Expose
    var fullName: String? = null,
    @SerializedName("username")
    @Expose
    var username: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("set_default")
    @Expose
    var setDefault: String? = null
)
