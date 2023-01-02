package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.moneytree.app.common.NSConstants

data class NSFcmResponse(
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("body")
    @Expose
    var body: String? = null,
    @SerializedName("channelId")
    @Expose
    var channelId: String? = null,
    @SerializedName("sound")
    @Expose
    var sound: String? = null,
    @SerializedName("imageUrl")
    @Expose
    var imageUrl: String? = null,
    @SerializedName("order_id")
    @Expose
    var orderId: String? = null,
	@SerializedName("type")
	@Expose
	var type: String? = NSConstants.KEY_DEFAULT_TYPE
)
