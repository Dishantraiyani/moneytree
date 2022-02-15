package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*{"status":true,"message":"","data":[{"memberid":"4548396215","status":"Not
Eligible","colour":"#ff1d15"},{"memberid":"9473931001","status":"Not
Eligible","colour":"#ff1d15"},{"memberid":"2898543375","status":"Not
Eligible","colour":"#ff1d15"},{"memberid":"9125548840","status":"Not
Eligible","colour":"#ff1d15"},{"memberid":"6353336889","status":"Not Eligible","colour":"#ff1d15"}]}*/

/**
 * The class representing the response body of user
 */
data class NSDownlineMemberDirectReOfferResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<NSDownlineMemberDirectReOfferData>? = null
)

/**
 * The class representing the order details
 */
data class NSDownlineMemberDirectReOfferData(
    @SerializedName("memberid")
    @Expose
    var memberid: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("colour")
    @Expose
    var colour: String? = null
)