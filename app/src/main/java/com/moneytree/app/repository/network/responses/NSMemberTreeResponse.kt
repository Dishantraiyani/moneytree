package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of member tree
 */
data class NSMemberTreeResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSMemberTreeData>? = null
)

/**
 * The class representing the member tree details
 */
data class NSMemberTreeData(
    @SerializedName("memberid")
    @Expose
    var memberid: String? = null,
    @SerializedName("slot2")
    @Expose
    var slot2: Boolean? = false,
    @SerializedName("slot3")
    @Expose
    var slot3: Boolean? = false,
    @SerializedName("slot4")
    @Expose
    var slot4: Boolean? = false,
    @SerializedName("slot5")
    @Expose
    var slot5: Boolean? = false,
    @SerializedName("slot6")
    @Expose
    var slot6: Boolean? = false,
    @SerializedName("slot7")
    @Expose
    var slot7: Boolean? = false,
    @SerializedName("slot8")
    @Expose
    var slot8: Boolean? = false,
    @SerializedName("slot9")
    @Expose
    var slot9: Boolean? = false,
    @SerializedName("slot10")
    @Expose
    var slot10: Boolean? = false,
    @SerializedName("slot11")
    @Expose
    var slot11: Boolean? = false,
    @SerializedName("slot12")
    @Expose
    var slot12: Boolean? = false,
)