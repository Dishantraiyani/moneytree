package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of notification
 */
data class NSNotificationListResponse(
    @SerializedName("data")
    @Expose
    var orderData: MutableList<NSNotificationListData>? = null
)

/**
 * The class representing the notification list details
 */
data class NSNotificationListData(
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("sub_title")
    @Expose
    var subTitle: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
)