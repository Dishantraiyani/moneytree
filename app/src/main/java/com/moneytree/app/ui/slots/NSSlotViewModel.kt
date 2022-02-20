package com.moneytree.app.ui.slots

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRegisterRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRegisterListData
import com.moneytree.app.repository.network.responses.NSRegisterListResponse
import com.moneytree.app.repository.network.responses.NSSlotListData


/**
 * The view model class for slots. It handles the business logic to communicate with the model for the slots and provides the data to the observing UI component.
 */
class NSSlotViewModel(application: Application) : NSViewModel(application) {
    var slotList: MutableList<NSSlotListData> = arrayListOf()
    var isSlotDataAvailable = MutableLiveData<Boolean>()
    var strSlots: String? = null
    var slotResponse: NSRegisterListResponse? = null

    /**
     * Get slots list data
     *
     */
    fun getSlotsListData(isShowProgress: Boolean) {
        slotList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        val listType = object : TypeToken<List<NSSlotListData>?>() {}.type
        slotList = Gson().fromJson(strSlots, listType)
        isProgressShowing.value = false
        isSlotDataAvailable.value = slotList.isValidList()
    }
}