package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class DoctorResponse(

	@field:SerializedName("data")
	val data: MutableList<DoctorDataItem> = arrayListOf(),

	@field:SerializedName("nextPage")
	val nextPage: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class DoctorDataItem(

	@field:SerializedName("doctor_id")
    var doctorId: String? = null,

	@field:SerializedName("charges")
	val charges: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("education")
	val education: String? = null,

	@field:SerializedName("sponsorid")
	val sponsorid: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("doctor_name")
	val doctorName: String? = null,

	@field:SerializedName("experience")
	val experience: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("remarks")
	val remarks: String? = null,

	@field:SerializedName("memberid")
	val memberid: String? = null,

	@field:SerializedName("is_delete")
	val isDelete: String? = null,

	@field:SerializedName("image")
	val image: String? = null
)
