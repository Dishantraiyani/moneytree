package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class OrderInfoResponse(

	@field:SerializedName("data")
	val data: List<OrderInfoDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean? = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class OnlineOrderInfoResponse(
	
	@field:SerializedName("data")
	val data: OnlineOrderHistoryData? = null,
	
	@field:SerializedName("items")
	val items: List<OrderInfoDataItem>? = null,
	
	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,
	
	@field:SerializedName("message")
	val message: String? = null,
	
	@field:SerializedName("status")
	val status: Boolean = false
)

//{"data":{"order_no":"ORD20260130174002","memberid":"9010530525","wallet_type":null,"address_data":null,"created_at":"2026-01-30 17:40:02","total":"250","order_status":"Order Accepted","full_name":"Walpar","mobile_no":"9898811277","email":"walparcustomercare@gmail.com","address1":"jalna road, aurangabad","address2":null,"landmark":null,"pincode":"401110","country":null,"district":"Ahmedabad","state":"Gujarat","city":"Ahmedabad","delivery_charges":null,"mt_coin_total":null,"remark":"Done..","mt_coin_earning_date":null,"mt_coin_status":"Pending","payment_type":"Wallet","accepted_memberid":"6797752581"},"items":[{"direct_order_product_id":"9","direct_order_id":"6","product_id":"14","qty":"1","rate":"250","amount":"250","created_at":null,"mt_coin":null,"product_name":"Marsulin 30 Tab In Jar","product_image":"1739612299_002-Marsulin_Tabs.png","product_slug":"Marsulin-30-Tab-In-Jar"}]}

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

data class OnlineOrderHistoryData(
	
	@field:SerializedName("order_no")
	val orderNo: String? = null,
	
	@field:SerializedName("country")
	val country: String? = null,
	
	@field:SerializedName("accepted_memberid")
	val acceptedMemberid: String? = null,
	
	@field:SerializedName("city")
	val city: String? = null,
	
	@field:SerializedName("mobile_no")
	val mobileNo: String? = null,
	
	@field:SerializedName("direct_order_id")
	val directOrderId: String? = null,
	
	@field:SerializedName("created_at")
	val createdAt: String? = null,
	
	@field:SerializedName("remark")
	val remark: String? = null,
	
	@field:SerializedName("order_status")
	val orderStatus: String? = null,
	
	@field:SerializedName("total")
	val total: String? = null,
	
	@field:SerializedName("delivery_charges")
	val deliveryCharges: Any? = null,
	
	@field:SerializedName("mt_coin_total")
	val mtCoinTotal: String? = null,
	
	@field:SerializedName("state")
	val state: String? = null,
	
	@field:SerializedName("mt_coin_status")
	val mtCoinStatus: String? = null,
	
	@field:SerializedName("landmark")
	val landmark: Any? = null,
	
	@field:SerializedName("email")
	val email: String? = null,
	
	@field:SerializedName("pincode")
	val pincode: String? = null,
	
	@field:SerializedName("address2")
	val address2: Any? = null,
	
	@field:SerializedName("address1")
	val address1: String? = null,
	
	@field:SerializedName("address_data")
	val addressData: Any? = null,
	
	@field:SerializedName("mt_coin_earning_date")
	val mtCoinEarningDate: Any? = null,
	
	@field:SerializedName("payment_type")
	val paymentType: String? = null,
	
	@field:SerializedName("full_name")
	val fullName: String? = null,
	
	@field:SerializedName("district")
	val district: String? = null,
	
	@field:SerializedName("wallet_type")
	val walletType: Any? = null,
	
	@field:SerializedName("memberid")
	val memberid: String? = null
)
