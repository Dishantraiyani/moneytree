package com.moneytree.app.ui.downloads

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSDashboardRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.DownloadDataItem
import com.moneytree.app.repository.network.responses.DownloadListResponse


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSDownloadPlanModel(application: Application) : NSViewModel(application) {
	var downloadList: MutableList<DownloadDataItem> = arrayListOf()
	var isDownloadListAvailable = MutableLiveData<Boolean>()

	/*fun setDownloadList() {
		downloadList.clear()
		downloadList.add(NSDownloadData(title = "Business Plan English", link = NSUtilities.decrypt("YUhSMGNITTZMeTl0YjI1bGVYUnlaV1V1WW1sNkwyRnpjMlYwY3k5bWNtOXVkR1Z1WkM5a2IyTnpMMjF2Ym1WNUxYUnlaV1V0WW5WegphVzVsYzNNdGNHeGhiaTFsYm1jdWNHUm0K")))
		downloadList.add(NSDownloadData(title = "Business Plan Hindi", link = NSUtilities.decrypt("YUhSMGNITTZMeTl0YjI1bGVYUnlaV1V1WW1sNkwyRnpjMlYwY3k5bWNtOXVkR1Z1WkM5a2IyTnpMMjF2Ym1WNUxYUnlaV1V0WW5WegphVzVsYzNNdGNHeGhiaTFvYVc1a2FTNXdaR1k9Cg==")))
		downloadList.add(NSDownloadData(title = "Product List", link = NSUtilities.decrypt("YUhSMGNITTZMeTl0YjI1bGVYUnlaV1V1WW1sNkwyRnpjMlYwY3k5bWNtOXVkR1Z1WkM5a2IyTnpMM0J5YjJSMVkzUXRiR2x6ZEM1dwpaR1k9Cg==")))
	}*/

	fun getDownloadList() {
		isProgressShowing.value = true
		NSDashboardRepository.getDownloadList(object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				if (data is DownloadListResponse) {
					downloadList.clear()
					downloadList.addAll(data.data)
				}
				isDownloadListAvailable.value = downloadList.isValidList()
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
		})
	}
}
