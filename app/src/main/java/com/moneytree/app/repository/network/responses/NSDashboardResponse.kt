package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSDashboardResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: NSDashboardData? = null
)

/**
 * The class representing the response of single order detail
 */
data class NSDashboardData(
    @SerializedName("today_joining")
    val todayJoining: MutableList<NSTodayJoiningData>? = null,
    @SerializedName("today_joining_list")
    val todayJoiningList: MutableList<NSTodayJoiningListData>? = null,
    @SerializedName("today_repurchase")
    val todayRepurchase: MutableList<NSTodayRePurchaseData>? = null,
    @SerializedName("today_repurchase_list")
    val todayRepurchaseList: MutableList<NSTodayRePurchaseListData>? = null,
    @SerializedName("wlt_amt")
    val wltAmt: MutableList<NSWalletAmountData>? = null,
    @SerializedName("dwn_totl")
    val dwnTotal: MutableList<NSDwnTotalData>? = null,
    @SerializedName("voucher_totl")
    val voucherTotal: MutableList<NSDwnTotalData>? = null,
    @SerializedName("available_joining_voucher")
    val availableJoiningVoucher: MutableList<NSDwnTotalData>? = null,
    @SerializedName("slot_list")
    val slotList: MutableList<NSSlotListData>? = null
)

/**
 * The class representing the order details
 */
data class NSDwnTotalData(
    @SerializedName("cnt")
    @Expose
    var cnt: String? = null
)

/**
 * The class representing the order details
 */
data class NSWalletAmountData(
    @SerializedName("amount")
    @Expose
    var amount: String? = null
)

/**
 * The class representing the order details
 */
data class NSTodayJoiningData(
    @SerializedName("today_joining")
    @Expose
    var todayJoining: String? = null
)

/**
 * The class representing the order details
 */
data class NSSlotListData(
    @SerializedName("slot_master_id")
    @Expose
    var slotMasterId: String? = null,
    @SerializedName("mrp")
    @Expose
    var mrp: String? = null,
    @SerializedName("cnt")
    @Expose
    var cnt: String? = null,
    @SerializedName("checkEntry")
    @Expose
    var checkEntry: String? = null
)

/**
 * The class representing the order details
 */
data class NSTodayJoiningListData(
    @SerializedName("fullname")
    @Expose
    var fullName: String? = null,
    @SerializedName("username")
    @Expose
    var username: String? = null,
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
    var acNo: String? = null
)

/**
 * The class representing the order details
 */
data class NSTodayRePurchaseData(
    @SerializedName("today_repurchase")
    @Expose
    var todayRepurchase: String? = null
)

/**
 * The class representing the order details
 */
data class NSTodayRePurchaseListData(
    @SerializedName("repurchase_id")
    @Expose
    var repurchaseId: String? = null,
    @SerializedName("repurchase_no")
    @Expose
    var repurchaseNo: String? = null,
    @SerializedName("stockiestid")
    @Expose
    var stockiestid: String? = null,
    @SerializedName("memberid")
    @Expose
    var memberid: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("remark")
    @Expose
    var remark: String? = null,
    @SerializedName("total")
    @Expose
    var total: String? = null
)