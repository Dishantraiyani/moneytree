package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class OrderInfoResponse(

	@field:SerializedName("data")
	val data: List<OrderInfoDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class OrderInfoDataItem(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("direct_order_product_id")
	val directOrderProductId: String? = null,

	@field:SerializedName("rate")
	val rate: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("qty")
	val qty: String? = null,

	@field:SerializedName("direct_order_id")
	val directOrderId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("mt_coin")
	val mtCoin: String? = null,

	@field:SerializedName("mt_coin_total")
	var mtCoinTotal: String? = null
)
