package com.moneytree.app.ui.invite

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSInviteModel(application: Application) : NSViewModel(application) {
	var nsUserData: NSDataUser? = null
}
