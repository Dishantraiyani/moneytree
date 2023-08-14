package com.moneytree.app.ui.doctor.history

import android.app.Application
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSDoctorRepository
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.DoctorHistoryDataItem
import com.moneytree.app.repository.network.responses.DoctorHistoryResponse
import com.moneytree.app.repository.network.responses.DoctorResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSDoctorHistoryViewModel(application: Application) : NSViewModel(application) {

	var tempDoctorList: MutableList<DoctorHistoryDataItem> = arrayListOf()

	fun getDoctorListApi(isShowProgress: Boolean, page: Int, search: String, callback: ((Int, MutableList<DoctorHistoryDataItem>) -> Unit)) {
		if (isShowProgress) showProgress()

		callCommonApi({ obj ->
			NSDoctorRepository.doctorHistoryList(page.toString(), search, obj)
		}, { data, isSuccess ->
			hideProgress()
			if (!isSuccess) {
				callback.invoke(page, arrayListOf())
			} else if (data is DoctorHistoryResponse) {
				callback.invoke(page, data.data)
			}
		})
	}
}
