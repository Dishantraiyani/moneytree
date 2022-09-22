package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of check version for display Upload Dialog
 */
data class NSCheckVersionResponse(
    @SerializedName("data")
    @Expose
    val data: Data?,
    @SerializedName("message")
    @Expose
    val message: String?,
    @SerializedName("status")
    @Expose
    val status: Boolean=false
)

data class Data(
    @SerializedName("skip")
    @Expose
    val skip: String?,
    @SerializedName("version")
    @Expose
    val version: String?,
    @SerializedName("link")
    @Expose
    val link: String?
)