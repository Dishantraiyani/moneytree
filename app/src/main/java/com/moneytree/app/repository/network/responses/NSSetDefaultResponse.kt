package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of logout
 */
data class NSSetDefaultResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("error")
    @Expose
    var error: DefaultError? = null
)

/**
 * The class representing the error details
 */
data class DefaultError(
    @SerializedName("error_code")
    @Expose
    var code: String? = null
)
