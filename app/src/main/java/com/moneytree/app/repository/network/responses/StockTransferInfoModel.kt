package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class StockTransferInfoModel(

	@field:SerializedName("data")
	val data: List<StockDataItem> = arrayListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class StockDataItem(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("stock_transfer_id")
	val stockTransferId: String? = null,

	@field:SerializedName("rate")
	val rate: String? = null,

	@field:SerializedName("stock_transfer_item_id")
	val stockTransferItemId: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("qty")
	val qty: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null
)
