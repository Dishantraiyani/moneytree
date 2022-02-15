package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*{"status":true,"message":"","data":[{"memberid":"4548396215","slot2":null,"slot3":null,"slot4":null,"slot5":null,"slot6":null,"slot7":null,"slot8":null,"slot9":null,"slot10":null,"slot11":null,"slot12":null},{"memberid":"9473931001","slot2":null,"slot3":null,"slot4":null,"slot5":null,"slot6":null,"slot7":null,"slot8":null,"slot9":null,"slot10":null,"slot11":null,"slot12":null},{"memberid":"2898543375","slot2":null,"slot3":null,"slot4":null,"slot5":null,"slot6":null,"slot7":null,"slot8":null,"slot9":null,"slot10":null,"slot11":null,"slot12":null},{"memberid":"9125548840","slot2":null,"slot3":null,"slot4":null,"slot5":null,"slot6":null,"slot7":null,"slot8":null,"slot9":null,"slot10":null,"slot11":null,"slot12":null},{"memberid":"6353336889","slot2":null,"slot3":null,"slot4":null,"slot5":null,"slot6":null,"slot7":null,"slot8":null,"slot9":null,"slot10":null,"slot11":null,"slot12":null}]}*/

/**
 * The class representing the response body of user
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
 * The class representing the order details
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