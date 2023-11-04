package com.moneytree.app.ui.idCard

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.network.responses.NSDataUser


/**
 * The view model class for transaction. It handles the business logic to communicate with the model for the transaction and provides the data to the observing UI component.
 */
class NSIDCardViewModel(application: Application) : NSViewModel(application) {

    fun getUserDetail(callback: (NSDataUser) -> Unit) {
        MainDatabase.getUserData(object : NSUserDataCallback {
            override fun onResponse(userDetail: NSDataUser) {
                callback.invoke(userDetail)
            }
        })
    }
}
