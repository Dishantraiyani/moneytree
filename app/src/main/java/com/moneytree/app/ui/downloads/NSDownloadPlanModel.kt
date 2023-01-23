package com.moneytree.app.ui.downloads

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.network.responses.NSActivationPackageData
import com.moneytree.app.repository.network.responses.NSDownloadData


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSDownloadPlanModel(application: Application) : NSViewModel(application) {
	var downloadList: MutableList<NSDownloadData> = arrayListOf()

	fun setDownloadList() {
		downloadList.clear()
		downloadList.add(NSDownloadData(title = "Business Plan English", link = "https://moneytree.biz/assets/frontend/docs/money-tree-business-plan-eng.pdf"))
		downloadList.add(NSDownloadData(title = "Business Plan Hindi", link = "https://moneytree.biz/assets/frontend/docs/money-tree-business-plan-hindi.pdf"))
		downloadList.add(NSDownloadData(title = "Product List", link = "https://moneytree.biz/assets/frontend/docs/product-list.pdf"))
	}

}
