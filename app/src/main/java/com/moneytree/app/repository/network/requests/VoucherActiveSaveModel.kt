package com.moneytree.app.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of verify otp
 */
data class VoucherActiveSaveModel(
	@SerializedName("dsp_id")
    @Expose
    var dspId: String?,
	@SerializedName("sponsorid")
    @Expose
    var sponsorId : String?,
	@SerializedName("package_no")
	@Expose
	var packageNo: String?,
	@SerializedName("voucher_id")
	@Expose
	var voucherId: String?
)
