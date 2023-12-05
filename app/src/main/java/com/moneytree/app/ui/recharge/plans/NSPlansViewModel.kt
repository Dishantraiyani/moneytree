package com.moneytree.app.ui.recharge.plans

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSRechargeRepository
import com.moneytree.app.repository.network.responses.PackItem
import com.moneytree.app.repository.network.responses.PlansResponse


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSPlansViewModel(application: Application) : NSViewModel(application) {

	//9898811277
	var mobileNumber: String = ""
	fun getPlans(isShowProgress: Boolean, accountDisplay: String = "", callback: (PlansResponse, HashMap<String, MutableList<PackItem>>) -> Unit) {
		if (isShowProgress) showProgress()
		callCommonApi({ obj ->
			NSRechargeRepository.getPrepaidPlan(accountDisplay, obj)
		}, { data, isSuccess ->
			hideProgress()
			if (isSuccess) {
				hideProgress()
				if (data is PlansResponse) {
					if (data.data != null) {
						plansList(data.data.pack) {
							callback.invoke(data, it)
						}
					} else {
						callback.invoke(data, hashMapOf())
					}
				}
			}
		})
	}

	private fun plansList(list: MutableList<PackItem>, callback: (HashMap<String, MutableList<PackItem>>) -> Unit) {
		val map: HashMap<String, MutableList<PackItem>> = hashMapOf()

		for (data in list) {
			if (map.containsKey(data.catagory?.lowercase())) {
				val mapList = map[data.catagory?.lowercase()]
				mapList?.add(data)
				map[data.catagory?.lowercase()!!] = mapList?: arrayListOf()
			} else {
				val mapList: MutableList<PackItem> = arrayListOf()
				mapList.add(data)
				map[data.catagory!!.lowercase()] = mapList
			}
		}

		callback.invoke(map)
	}
}
