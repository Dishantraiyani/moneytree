package com.moneytree.app.ui.profile.password

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel

/**
 * The view model class for change password. It handles the business logic to communicate with the model for the change password item and provides the data to the observing UI component.
 */
class NSChangePasswordViewModel(application: Application) : NSViewModel(application) {
    var isChangePassword: Boolean = false
}