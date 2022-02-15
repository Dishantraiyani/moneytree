package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of verify otp
 */
data class NSLoginRequest(
    @SerializedName("username")
    @Expose
    var userName: String?,
    @SerializedName("password")
    @Expose
    var password: String?
)