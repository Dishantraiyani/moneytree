package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSErrorPaymentResponse(
    @SerializedName("error")
    @Expose
    var error: NSErrorDataResponse? = null
)

data class NSErrorDataResponse(
    @SerializedName("code")
    @Expose
    var code: String? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("source")
    @Expose
    var source: String? = null
)
