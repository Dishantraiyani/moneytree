package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSDownloadListResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSCategoryData> = arrayListOf()
)

data class NSDownloadData(
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("title")
    @Expose
    var title: String? = null,
	@SerializedName("description")
	@Expose
	var description: String? = null,
	@SerializedName("link")
	@Expose
	var link: String? = null
)
