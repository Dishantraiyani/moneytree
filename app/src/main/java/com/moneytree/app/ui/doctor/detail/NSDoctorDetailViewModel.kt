package com.moneytree.app.ui.doctor.detail

import android.app.Application
import com.google.gson.Gson
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSDoctorRepository
import com.moneytree.app.repository.network.requests.NSDoctorSendRequest
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.DoctorResponse
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSDoctorDetailViewModel(application: Application) : NSViewModel(application) {

	fun getDoctorDetail(detail: String?, callback: ((DoctorDataItem) -> Unit)) {
		if (detail?.isNotEmpty() == true) {
			callback.invoke(Gson().fromJson(detail, DoctorDataItem::class.java))
		}
	}

	fun sendDoctorRequest(isShowProgress: Boolean, imageFile: List<String>, map: HashMap<String, String>, callback: ((Boolean) -> Unit)) {
		val list: MutableList<String> = arrayListOf()
		list.addAll(imageFile)
		list.remove("ADD_IMAGE")

		if (isShowProgress) showProgress()

		val imageParts: List<MultipartBody.Part> = list.mapIndexed { index, uri ->
			val file = File(uri)
			val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
			MultipartBody.Part.createFormData("image", file.name, requestBody)
		}

		val request = NSDoctorSendRequest(map[NSConstants.DOCTOR_ID]?:"",
			map[NSConstants.PATIENT_NAME]?:"",
			map[NSConstants.PATIENT_NUMBER]?:"",
			map[NSConstants.PATIENT_DOB]?:"",
			map[NSConstants.PATIENT_GENDER]?:"",
			map[NSConstants.PATIENT_AGE]?:"",
			map[NSConstants.PATIENT_REMARK]?:"")

		callCommonApi({ obj ->
			NSDoctorRepository.sendDoctorRequest(request, imageParts, obj)
		}, { data, isSuccess ->
			hideProgress()
			if (isSuccess) {
				callback.invoke(true)
			}
		})
	}
}
