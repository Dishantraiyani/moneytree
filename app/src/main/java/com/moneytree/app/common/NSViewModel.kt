package com.moneytree.app.common

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSConstants.Companion.REFRESH_TOKEN_ENABLE
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.ui.login.NSLoginActivity

/**
 * The base class for all view models which holds methods and members common to all view models
 */
open class NSViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    var isProgressShowing = MutableLiveData<Boolean>()
    var isBottomProgressShowing = MutableLiveData<Boolean>()
    val validationErrorId by lazy { NSSingleLiveEvent<Int>() }
    val failureErrorMessage: NSSingleLiveEvent<String?> = NSSingleLiveEvent()
    val apiErrors: NSSingleLiveEvent<List<Any>> = NSSingleLiveEvent()
    val noNetworkAlert: NSSingleLiveEvent<Boolean> = NSSingleLiveEvent()
    var isRefreshComplete = MutableLiveData<Boolean>()

    /**
     * To handle the API failure error and communicate back to UI
     *
     * @param errorMessage The error message to show
     */
    protected fun handleFailure(errorMessage: String?) {
        isProgressShowing.value = false
        isBottomProgressShowing.value = false
        failureErrorMessage.value = errorMessage
    }

    /**
     * To handle api error message
     *
     * @param apiErrorList The errorList contains string resource id and string
     */
    protected fun handleError(apiErrorList: List<Any>) {
        isProgressShowing.value = false
        isBottomProgressShowing.value = false

        if (apiErrorList.contains("Session TimeOut!!\n")) {
            NSApplication.getInstance().getPrefs().clearPrefData()
            NSApplication.getInstance().startActivity(Intent(NSApplication.getInstance(), NSLoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        } else {
            apiErrors.value = apiErrorList
        }
    }

    /**
     * To handle no network
     */
    protected open fun handleNoNetwork() {
        isProgressShowing.value = false
        isBottomProgressShowing.value = false
        noNetworkAlert.value = true
    }
}