package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of recharge fetch list
 */
data class NSRechargeFetchListResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: HashMap<String, String> = HashMap()
)

data class NSRechargeFetchListData(
	@SerializedName("title")
	@Expose
	var title: String? = null,
	@SerializedName("value")
	@Expose
	var value: String? = null
)
