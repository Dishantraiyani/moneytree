package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of change password
 */
data class NSChangePasswordRequest(
    @SerializedName("current_password")
    @Expose
    var currentPassword: String?,
    @SerializedName("new_password")
    @Expose
    var newPassword: String?
)