package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of logout
 */
data class DspAndSponsorModel(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<DsPSponsorListData>? = null
)

data class DsPSponsorListData (
    @SerializedName("username")
    @Expose
    var username: Boolean = false,
    @SerializedName("fullname")
    @Expose
    var fullname: String? = null
)