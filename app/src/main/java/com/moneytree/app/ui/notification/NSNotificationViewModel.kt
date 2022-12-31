package com.moneytree.app.ui.notification

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSNotificationRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSNotificationListData
import com.moneytree.app.repository.network.responses.NSNotificationListResponse


/**
 * The view model class for notification. It handles the business logic to communicate with the model for the notification and provides the data to the observing UI component.
 */
class NSNotificationViewModel(application: Application) : NSViewModel(application),
	NSGenericViewModelCallback {
    var notificationList: MutableList<NSNotificationListData> = arrayListOf()
    var isNotificationDataAvailable = MutableLiveData<Boolean>()
	var pageIndex: String = "1"
	private var isBottomProgressShow: Boolean = false
	var notificationResponse: NSNotificationListResponse? = null

    /**
     * Get order list data
     *
     * @param isShowProgress The progress dialog check show
     */
    fun getNotificationData(isShowProgress: Boolean) {
        notificationList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        val notification1 = NSNotificationListData("Notification 1", "This is new Order Notification.This is new Order Notification.This is new Order Notification.This is new Order Notification.", "10:10 AM")
        val notification2 = NSNotificationListData("Notification 2", "This is new Order Notification.This is new Order Notification.This is new Order Notification.This is new Order Notification.", "10:10 AM")

        for (data in 0..5) {
            notificationList.add(notification1)
            notificationList.add(notification2)
        }

        isProgressShowing.value = false
        isNotificationDataAvailable.value = notificationList.isValidList()
    }

	/**
	 * Get voucher list data
	 *
	 */
	fun getNotificationData(pageIndex: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
		if (pageIndex == "1") {
			notificationList.clear()
		}
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		if (isBottomProgress) {
			isBottomProgressShowing.value = true
		}
		isBottomProgressShow = isBottomProgress
		NSNotificationRepository.getNotifications(pageIndex, this)
	}

	override fun <T> onSuccess(data: T) {
		isProgressShowing.value = false
		if (isBottomProgressShow) {
			isBottomProgressShowing.value = false
		}
		val notificationListData = data as NSNotificationListResponse
		notificationResponse = notificationListData
		if (notificationListData.data != null) {
			if (notificationListData.data.isValidList()) {
				notificationList.addAll(notificationListData.data!!)
				isNotificationDataAvailable.value = notificationList.isValidList()
			} else if (pageIndex == "1"){
				isNotificationDataAvailable.value = false
			}
		} else if (pageIndex == "1"){
			isNotificationDataAvailable.value = false
		}
	}

	override fun onError(errors: List<Any>) {
		handleError(errors)
	}

	override fun onFailure(failureMessage: String?) {
		handleFailure(failureMessage)
	}

	override fun <T> onNoNetwork(localData: T) {
		handleNoNetwork()
	}
}
