package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class PlansResponse(

	@field:SerializedName("mobile_operator")
	val mobileOperator: MobileOperator? = null,

	@field:SerializedName("data")
	val data: PlansData? = null,

	@field:SerializedName("service_provider")
	val serviceProvider: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class PlansData(

	@field:SerializedName("Operator")
	val operator: String? = null,

	@field:SerializedName("err_msg")
	val errMsg: String? = null,

	@field:SerializedName("erCode")
	val erCode: Int? = null,

	@field:SerializedName("pack")
	val pack: MutableList<PackItem> = arrayListOf(),

	@field:SerializedName("status")
	val status: String? = null,

	//Custom Selected
	@field:SerializedName("selected_pack_item")
	var selectedPack: PackItem? = null,
)

data class MobileOperator(

	@field:SerializedName("err_msg")
	val errMsg: String? = null,

	@field:SerializedName("erCode")
	val erCode: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("info")
	val info: Info? = null
)

data class PackItem(

	@field:SerializedName("catagory")
	val catagory: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("talktime")
	val talktime: String? = null,

	@field:SerializedName("validity")
	val validity: String? = null,

	@field:SerializedName("benefit")
	val benefit: String? = null
)

data class Info(

	@field:SerializedName("opr")
	val opr: String? = null,

	@field:SerializedName("Circle")
	val circle: String? = null,

	@field:SerializedName("CircleCode")
	val circleCode: String? = null
)
