package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of package list
 */
data class NSPackageResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSPackageData> = arrayListOf()
)

data class NSPackageData(
    @SerializedName("package_id")
    @Expose
    var packageId: String? = null,
    @SerializedName("package_name")
    @Expose
    var packageName: String? = null,
    @SerializedName("mrp")
    @Expose
    var mrp: String? = null,
    @SerializedName("is_active")
    @Expose
    var isActive: String? = null
)
