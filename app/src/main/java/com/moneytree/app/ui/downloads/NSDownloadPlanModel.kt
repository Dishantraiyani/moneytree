package com.moneytree.app.ui.downloads

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.repository.network.responses.NSActivationPackageData
import com.moneytree.app.repository.network.responses.NSDownloadData


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSDownloadPlanModel(application: Application) : NSViewModel(application) {
	var downloadList: MutableList<NSDownloadData> = arrayListOf()

	fun setDownloadList() {
		downloadList.clear()
		downloadList.add(NSDownloadData(title = "Business Plan English", link = NSUtilities.decrypt("YUhSMGNITTZMeTl0YjI1bGVYUnlaV1V1WW1sNkwyRnpjMlYwY3k5bWNtOXVkR1Z1WkM5a2IyTnpMMjF2Ym1WNUxYUnlaV1V0WW5WegphVzVsYzNNdGNHeGhiaTFsYm1jdWNHUm0K")))
		downloadList.add(NSDownloadData(title = "Business Plan Hindi", link = NSUtilities.decrypt("YUhSMGNITTZMeTl0YjI1bGVYUnlaV1V1WW1sNkwyRnpjMlYwY3k5bWNtOXVkR1Z1WkM5a2IyTnpMMjF2Ym1WNUxYUnlaV1V0WW5WegphVzVsYzNNdGNHeGhiaTFvYVc1a2FTNXdaR1k9Cg==")))
		downloadList.add(NSDownloadData(title = "Product List", link = NSUtilities.decrypt("YUhSMGNITTZMeTl0YjI1bGVYUnlaV1V1WW1sNkwyRnpjMlYwY3k5bWNtOXVkR1Z1WkM5a2IyTnpMM0J5YjJSMVkzUXRiR2x6ZEM1dwpaR1k9Cg==")))
	}

}
