package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.SerializedName

data class DownloadListResponse(

	@field:SerializedName("data")
	val data: List<DownloadDataItem> = arrayListOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean = false
)

data class DownloadDataItem(

	@field:SerializedName("img")
	val img: String? = null,

	@field:SerializedName("pdf")
	val pdf: String? = null,

	@field:SerializedName("entrydate")
	val entrydate: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)
