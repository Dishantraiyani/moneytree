package com.moneytree.app.ui.mycart.kyc

import android.app.Application
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSDoctorRepository
import com.moneytree.app.repository.NSKycRepository
import com.moneytree.app.repository.network.requests.NSDoctorSendRequest
import com.moneytree.app.repository.network.requests.NSKycSendRequest
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.DoctorResponse
import com.moneytree.app.repository.network.responses.KycResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.random.Random


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSKycViewModel(application: Application) : NSViewModel(application) {

	fun kycVerification(isShowProgress: Boolean, imageList: MutableList<String>, callback: (KycResponse, Boolean) -> Unit) {

		if (imageList.isValidList()) {
			val url = imageList[0]
			val file: File = File(url)
			if (file.exists()) {
				if (isShowProgress) showProgress()

				val base64Str = convertImageToBase64(url)
				val kycSendRequest = NSKycSendRequest("image", base64Str, java.util.UUID.randomUUID().toString())

				callCommonApi({ obj ->
					NSKycRepository.kycVerification(kycSendRequest, obj)
				}, { data, isSuccess ->
					hideProgress()
					if (!isSuccess) {
						callback.invoke(KycResponse(), false)
					} else if (data is KycResponse) {
						callback.invoke(data, true)
					}
				})
			}
		}


	}

	private fun convertImageToBase64(imagePath: String): String {
		val bufferSize = 4096 // Adjust the buffer size as needed

		try {
			FileInputStream(imagePath).use { inputStream ->
				ByteArrayOutputStream().use { outputStream ->
					val buffer = ByteArray(bufferSize)
					var bytesRead: Int

					while (inputStream.read(buffer).also { bytesRead = it } != -1) {
						outputStream.write(buffer, 0, bytesRead)
					}

					val bytes = outputStream.toByteArray()
					return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
				}
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}

		return ""
	}

	fun sendKycRequest(kycDetail: String, imageFile: List<String>, callback: ((String, Boolean) -> Unit)) {
		showProgress()

		val file = File(imageFile[0])
		val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
		val part = MultipartBody.Part.createFormData("image", file.name, requestBody)

		callCommonApi({ obj ->
			NSKycRepository.sendKycRequest(kycDetail, part, obj)
		}, { data, isSuccess ->
			hideProgress()
			if (isSuccess) {
				if (data is NSSuccessResponse) {
					callback.invoke(data.message?:"", true)
				}
			} else {
				callback.invoke("", false)
			}
		})
	}
}
