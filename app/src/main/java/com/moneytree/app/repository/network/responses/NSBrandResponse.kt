package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of diseases list
 */
data class NSBrandResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSBrandData> = arrayListOf()
)

data class NSBrandData(
    @SerializedName("brand_id")
    @Expose
    var brandId: String? = null,
    @SerializedName("brand_name")
    @Expose
    var brandName: String? = null
)
