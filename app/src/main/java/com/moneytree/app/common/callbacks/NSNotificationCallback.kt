package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSNotificationListData

/**
 * The interface to notification click
 */
interface NSNotificationCallback {
    /**
     * Invoked when the click on notification
     *
     */
    fun onClick(data: NSNotificationListData)
}