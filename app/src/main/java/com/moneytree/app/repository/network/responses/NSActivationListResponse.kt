package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSActivationListResponse(
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
    var data: MutableList<NSActivationData> = arrayListOf()
)

data class NSActivationData(
	@SerializedName("package_id")
	@Expose
	var packageID: String? = null,
	@SerializedName("package_name")
	@Expose
	var packageName: String? = null,
	@SerializedName("voucher_id")
	@Expose
	var voucherID: String? = null,
	@SerializedName("voucher_code")
	@Expose
	var voucherCode: String? = null,
	@SerializedName("voucher_name")
	@Expose
	var voucherName: String? = null,
	@SerializedName("memberid")
	@Expose
	var memberId: String? = null,
	@SerializedName("voucher_status")
	@Expose
	var voucherStatus: String? = null,
	@SerializedName("member_type")
	@Expose
	var memberType: String? = null,
	@SerializedName("created_at")
	@Expose
	var createdAt: String? = null,
	@SerializedName("updated_at")
	@Expose
	var updatedAt: String? = null,
	@SerializedName("created_id")
	@Expose
	var createdId: String? = null,
	@SerializedName("updated_id")
	@Expose
	var updatedId: String? = null
)
