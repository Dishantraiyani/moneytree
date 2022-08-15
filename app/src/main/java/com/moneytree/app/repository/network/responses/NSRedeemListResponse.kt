package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSRedeemListResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("nextPage")
    @Expose
    var nextPage: Boolean = false,
    @SerializedName("wallet_amount")
    @Expose
    var walletAmount: List<WalletRedeemAmount> = arrayListOf(),
    @SerializedName("data")
    @Expose
    var data: MutableList<NSWalletRedeemData> = arrayListOf()
)

data class WalletRedeemAmount(
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
)

data class NSWalletRedeemData(
    @SerializedName("wallet_redemption_id")
    @Expose
    var walletRedemptionId: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("admin_charges")
    @Expose
    var adminCharges: String? = null,
    @SerializedName("tds_charges")
    @Expose
    var tdsCharges: String? = null,
	@SerializedName("total")
	@Expose
	var total: String? = null
)
