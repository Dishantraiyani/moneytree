package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSUpLineListResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
	@SerializedName("nextPage")
	val nextPage: Boolean = false,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSUpLineData> = arrayListOf()
)

data class NSUpLineData(
    @SerializedName("earnedid")
    @Expose
    var earnedId: String? = null,
    @SerializedName("levelno")
    @Expose
    var levelNo: String? = null,
	@SerializedName("is_active")
	@Expose
	var isActive: String? = null
)
