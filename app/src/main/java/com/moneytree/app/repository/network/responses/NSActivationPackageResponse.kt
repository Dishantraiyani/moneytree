package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSActivationPackageResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSActivationPackageData> = arrayListOf()
)

data class NSActivationPackageData(
	@SerializedName("package_id")
    @Expose
    var packageId: String? = null,
	@SerializedName("package_name")
    @Expose
    var packageName: String? = null
)
