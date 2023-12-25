package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSPendingCoinWalletListResponse(
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
    var walletAmount: List<WalletAmount> = arrayListOf(),
    @SerializedName("data")
    @Expose
    var data: MutableList<NSPendingWalletData> = arrayListOf()
)

data class NSPendingWalletData(
    @SerializedName("mt_coin_earning_date")
    @Expose
    var mtCoinEarningDate: String? = null,
    @SerializedName("memberid")
    @Expose
    var memberId: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("order_no")
    @Expose
    var orderNo: String? = null,
    @SerializedName("levelno")
    @Expose
    var levelNo: String? = null
)