package com.moneytree.app.ui.meeting

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.MeetingsRepository
import com.moneytree.app.repository.network.responses.MeetingsDataResponse
import com.moneytree.app.repository.network.responses.MeetingsListResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class MeetingViewModel(application: Application) : NSViewModel(application) {
    var isFromOrder: Boolean = false

    /**
     * Get voucher list data
     *
     */
    fun getMeetingList(isShowProgress: Boolean, callback: (MutableList<MeetingsDataResponse>) -> Unit) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            MeetingsRepository.getMeetingList(obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess && data is MeetingsListResponse) {
                callback.invoke(data.data)
            } else {
                callback.invoke(arrayListOf())
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
