package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of change password
 */
data class NSKycSendRequest(
    @SerializedName("doc_type")
    @Expose
    var docType: String,
    @SerializedName("doc_base64")
    @Expose
    var docBase64: String,
    @SerializedName("req_id")
    @Expose
    var reqId: String
)