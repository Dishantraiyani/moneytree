package com.moneytree.app.repository.network.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSUserResponse(
    @SerializedName("token_id")
    @Expose
    var tokenId: String? = null,
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: NSDataUser? = null
)

@Entity
data class NSDataUser(
    @PrimaryKey(autoGenerate = true)
    var autoId: Int = 0,
    @SerializedName("fullname")
    @Expose
    var fullName: String? = null,
    @SerializedName("username")
    @Expose
    var userName: String? = null,
    @SerializedName("sponsor_id")
    @Expose
    var sponsorId: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("address")
    @Expose
    var address: String? = null,
    @SerializedName("panno")
    @Expose
    var panno: String? = null,
    @SerializedName("bank_name")
    @Expose
    var bankName: String? = null,
    @SerializedName("ifsc_code")
    @Expose
    var ifscCode: String? = null,
    @SerializedName("bank_holder_name")
    @Expose
    var bankHolderName: String? = null,
    @SerializedName("ac_no")
    @Expose
    var acNo: String? = null,
	@SerializedName("is_active")
	@Expose
	var isActive: String? = null
)
