package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of logout
 */
data class NSLogoutResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("error")
    @Expose
    var error: Error? = null
)

/**
 * The class representing the error details
 */
data class Error(
    @SerializedName("error_code")
    @Expose
    var code: String? = null
)