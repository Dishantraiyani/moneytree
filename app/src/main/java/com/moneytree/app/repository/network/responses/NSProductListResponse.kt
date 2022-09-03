package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSProductListResponse(

	@field:SerializedName("status")
	val status: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("data")
	val data: MutableList<ProductDataDTO> = arrayListOf()
)

data class ProductDataDTO(

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("category_id")
	val categoryId: String? = null,

	@field:SerializedName("rate")
	val rate: String? = null,

	@field:SerializedName("sd_price")
	val sdPrice: String? = null,

	@field:SerializedName("pv")
	val pv: String? = null,

	@field:SerializedName("is_delete")
	val isDelete: String? = null,

	@field:SerializedName("is_website")
	val isWebsite: String? = null,

	@field:SerializedName("created_id")
	val createdId: Any? = null,

	@field:SerializedName("updated_id")
	val updatedId: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("remark")
	val remark: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("product_image")
	val productImage: String? = null,

	@field:SerializedName("product_slug")
	val productSlug: String? = null
)
