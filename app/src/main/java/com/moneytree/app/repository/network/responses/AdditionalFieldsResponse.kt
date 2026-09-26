package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class AdditionalFieldsResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<AdditionalDataFields>? = null
)

data class AdditionalDataFields(
    @SerializedName("label")
    @Expose
    var label: String? = null,
    @SerializedName("type")
    @Expose
    var type: String? = null,
    @SerializedName("regex")
    @Expose
    var regex: String? = null,
    @SerializedName("required")
    @Expose
    var required: Boolean? = false,
    @SerializedName("key")
    @Expose
    var key: String? = null,
    @SerializedName("options")
    @Expose
    var options: MutableList<AdditionalOptions>? = null
)

data class AdditionalOptions(
    @SerializedName("label")
    @Expose
    var label: String? = null,
    @SerializedName("value")
    @Expose
    var value: String? = null
)
