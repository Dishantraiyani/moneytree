package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSRepurchaseStockModel(

	@field:SerializedName("data")
	val data: List<RepurchaseDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class RepurchaseDataItem(

	@field:SerializedName("repurchase_no")
	val repurchaseNo: String? = null,

	@field:SerializedName("total")
	val total: String? = null,

	@field:SerializedName("stockiest_type")
	val stockiestType: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("remark")
	val remark: String? = null,

	@field:SerializedName("repurchase_id")
	val repurchaseId: String? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("stockiestid")
	val stockiestid: String? = null,

	@field:SerializedName("memberid")
	val memberid: String? = null,

	@field:SerializedName("stock_transfer_id")
	val stockTransferId: String? = null,

	@field:SerializedName("order_no")
	val orderNo: String? = null,

	@field:SerializedName("stockiest_to")
	val stockiestTo: String? = null
)
