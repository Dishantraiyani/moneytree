package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of diseases list
 */
data class NSDiseasesResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSDiseasesData> = arrayListOf()
)

data class NSDiseasesData(
    @SerializedName("diseases_id")
    @Expose
    var diseasesId: String? = null,
    @SerializedName("diseases_name")
    @Expose
    var diseasesName: String? = null
)
