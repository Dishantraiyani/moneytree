package com.moneytree.app.ui.meeting.edit

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.MeetingsRepository
import com.moneytree.app.repository.NSAddressRepository
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class MeetingAddEditViewModel(application: Application) : NSViewModel(application) {

    fun addOrUpdateMeeting(map: HashMap<String, RequestBody>,imageFile: List<String>, callback: ((NSSuccessResponse) -> Unit)) {

        var part: MultipartBody.Part? = null
        if (imageFile.isValidList()) {
            val file = File(imageFile[0])
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            part = MultipartBody.Part.createFormData("image", file.name, requestBody)
        }

        showProgress()
        callCommonApi({ obj ->
            MeetingsRepository.saveMeetings(map, part, obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess && data is NSSuccessResponse) {
                callback.invoke(data)
            }
        })
    }

    fun deleteMeetings(eventId: String?, callback: (NSSuccessResponse) -> Unit) {
        showProgress()
        callCommonApi({ obj ->
            MeetingsRepository.deleteMeeting(eventId?:"", obj)
        }, { data, _ ->
            hideProgress()
            if (data is NSSuccessResponse) {
                callback.invoke(data)
            }
        })
    }
}
