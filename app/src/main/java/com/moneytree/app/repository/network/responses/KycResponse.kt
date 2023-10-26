package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class KycResponse(

	@field:SerializedName("req_id")
	val reqId: String? = null,

	@field:SerializedName("error_message")
	val errorMessage: String? = null,

	@field:SerializedName("extracted_data")
	val extractedData: HashMap<String, Any> = hashMapOf(),

	@field:SerializedName("success")
	val success: Boolean = false,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("doc_type")
	val docType: String? = null,

	@field:SerializedName("image_quality")
	val imageQuality: String? = null
)

data class AddressDetails(

	@field:SerializedName("PIN")
	val pIN: String? = null,

	@field:SerializedName("State")
	val state: String? = null,

	@field:SerializedName("City")
	val city: String? = null
)

data class ExtractedData(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("father_spouse_name")
	val fatherSpouseName: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("father_name")
	val fatherName: String? = null,

	@field:SerializedName("aadhar_id")
	val aadharId: String? = null,

	@field:SerializedName("dob")
	val dob: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("address_details")
	val addressDetails: AddressDetails? = null,

	@field:SerializedName("aadhar_masked_no")
	val aadharMaskedNo: String? = null
)
