package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of level member tree
 */
data class NSLevelMemberTreeResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSLevelMemberTreeData>? = null
)

/**
 * The class representing the level member tree details
 */
data class NSLevelMemberTreeData(
    @SerializedName("levelno")
    @Expose
    var levelNo: String? = null,
    @SerializedName("cnt")
    @Expose
    var cnt: String? = null,
    @SerializedName("rep_total")
    @Expose
    var repTotal: Int = 0,
    @SerializedName("repurchase")
    @Expose
    var repurchase: Int = 0,
	@SerializedName("direct_fifty")
	@Expose
	var directFifty: Int = 0

)
