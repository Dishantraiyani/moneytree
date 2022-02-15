package com.moneytree.app.repository.network.error

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the common error response
 */
data class NSErrorResponse(
    @SerializedName("code")
    @Expose
    var code: String? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null
)