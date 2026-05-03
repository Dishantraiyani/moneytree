package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class TopUp8888MemberListResponse(

	@field:SerializedName("data")
	val data: TopUp8888MemberData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class TopUp8888MemberListData(

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("username")
	val username: String? = null,
	
	@field:SerializedName("sponsor_id")
	val sponsorId: String? = null,
)

data class TopUp8888MemberData(

	@field:SerializedName("member_list")
	val memberList: List<TopUp8888MemberListData>? = null,

	@field:SerializedName("available_pin")
	val availablePin: Double? = null
)

