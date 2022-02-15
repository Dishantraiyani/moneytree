package com.moneytree.app.ui.notification

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.network.responses.NSNotificationListData


/**
 * The view model class for notification. It handles the business logic to communicate with the model for the notification and provides the data to the observing UI component.
 */
class NSNotificationViewModel(application: Application) : NSViewModel(application) {
    var notificationList: MutableList<NSNotificationListData> = arrayListOf()
    var isNotificationDataAvailable = MutableLiveData<Boolean>()

    /**
     * Get order list data
     *
     * @param activity The activity's context
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
}