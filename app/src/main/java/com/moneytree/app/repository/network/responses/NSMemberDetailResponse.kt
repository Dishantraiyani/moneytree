package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of logout
 */
data class NSMemberDetailResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
	@SerializedName("data")
	@Expose
	var data: MemberDetailModel? = null,
    @SerializedName("error")
    @Expose
    var error: ErrorData? = null
)

/**
 * The class representing the error details
 */
data class ErrorData(
    @SerializedName("error_code")
    @Expose
    var code: String? = null
)

/**
 * The class representing the error details
 */
data class MemberDetailModel(
	@SerializedName("username")
	@Expose
	var username: String? = null,
	@SerializedName("mobile")
	@Expose
	var mobile: String? = null,
	@SerializedName("email")
	@Expose
	var email: String? = null,
	@SerializedName("fullname")
	@Expose
	var fullname: String? = null
)
