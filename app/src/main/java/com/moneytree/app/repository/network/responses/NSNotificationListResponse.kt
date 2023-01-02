package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.moneytree.app.common.NSConstants

/**
 * The class representing the response body of notification
 */
data class NSNotificationListResponse(
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
    var data: MutableList<NSNotificationListData>? = null
)

/**
 * The class representing the notification list details
 */
data class NSNotificationListData(
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("entrydate")
    @Expose
    var entrydate: String? = null,
	@SerializedName("memberid")
	@Expose
	var memberid: String? = null,
	@SerializedName("img")
	@Expose
	var img: String? = null,
	@SerializedName("body")
	@Expose
	var body: String? = null,
	@SerializedName("type")
	@Expose
	var type: String? = NSConstants.KEY_DEFAULT_TYPE
)
