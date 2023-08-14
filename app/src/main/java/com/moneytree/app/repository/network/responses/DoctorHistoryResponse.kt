package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class DoctorHistoryResponse(

	@field:SerializedName("data")
	val data: MutableList<DoctorHistoryDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class DoctorHistoryDataItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("doctor_memberid")
	val doctorMemberid: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("appointment_id")
	val appointmentId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("remark")
	val remark: String? = null,

	@field:SerializedName("doctor_id")
	val doctorId: String? = null,

	@field:SerializedName("charges")
	val charges: String? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("doctor_name")
	val doctorName: String? = null,

	@field:SerializedName("age")
	val age: String? = null,

	@field:SerializedName("memberid")
	val memberid: String? = null
)
