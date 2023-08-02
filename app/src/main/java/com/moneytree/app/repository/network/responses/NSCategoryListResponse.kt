package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSCategoryListResponse(
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

data class NSCategoryData(
    @SerializedName("category_id")
    @Expose
    var categoryId: String? = null,
    @SerializedName("category_name")
    @Expose
    var categoryName: String? = null,
    @SerializedName("category_img")
    @Expose
    var categoryImg: String? = null
)
