package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of change password
 */
data class NSDoctorSendRequest(
    @SerializedName("doctor_id")
    @Expose
    var doctorId: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("mobile")
    @Expose
    var mobile: String,
    @SerializedName("dob")
    @Expose
    var dob: String,
    @SerializedName("gender")
    @Expose
    var gender: String,
    @SerializedName("age")
    @Expose
    var age: String,
    @SerializedName("remark")
    @Expose
    var remark: String
)