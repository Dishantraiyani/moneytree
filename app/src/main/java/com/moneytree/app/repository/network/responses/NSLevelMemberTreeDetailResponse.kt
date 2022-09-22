package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of level member tree
 */
data class NSLevelMemberTreeDetailResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSLevelMemberTreeDetail>? = null
)

/**
 * The class representing the level member tree details
 */
data class NSLevelMemberTreeDetail(
    @SerializedName("levelno")
    @Expose
    var levelNo: String? = null,
    @SerializedName("memberid")
    @Expose
    var memberId: String? = null,
    @SerializedName("fullname")
    @Expose
    var fullName: String? = "",
    @SerializedName("repurchase")
    @Expose
    var repurchase: Int = 0,
	@SerializedName("direct_sponsor")
	@Expose
	var directSponsor: Int = 0,
	@SerializedName("royalty_name")
	@Expose
	var royaltyName: String? = null,
	@SerializedName("sponsor_id")
	@Expose
	var sponsorId: String? = null

)
