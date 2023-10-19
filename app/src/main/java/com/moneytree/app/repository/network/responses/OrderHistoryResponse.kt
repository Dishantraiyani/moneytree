package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class OrderHistoryResponse(

	@field:SerializedName("data")
	val data: List<OrderHistoryDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class OrderHistoryDataItem(

	@field:SerializedName("order_no")
	val orderNo: String? = null,

	@field:SerializedName("pincode")
	val pinCode: String? = null,

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("address2")
	val address2: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("address1")
	val address1: String? = null,

	@field:SerializedName("mobile_no")
	val mobileNo: String? = null,

	@field:SerializedName("direct_order_id")
	val directOrderId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("address_data")
	val addressData: String? = null,

	@field:SerializedName("order_status")
	val orderStatus: String? = null,

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("full_name")
	val fullName: String? = null,

	@field:SerializedName("wallet_type")
	val walletType: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("landmark")
	val landmark: String? = null,

	@field:SerializedName("memberid")
	val memberId: String? = null,

	@field:SerializedName("address_final")
	var addressFinal: String? = null
)
