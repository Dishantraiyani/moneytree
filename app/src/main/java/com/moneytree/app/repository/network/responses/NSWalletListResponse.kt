package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class NSWalletListResponse(
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
    var data: MutableList<NSWalletData> = arrayListOf()
)

data class WalletAmount(
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
)

data class NSWalletData(
    @SerializedName("transferid")
    @Expose
    var transferid: String? = null,
    @SerializedName("entryType")
    @Expose
    var entryType: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
    @SerializedName("entryDate")
    @Expose
    var entryDate: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null
)