package com.moneytree.app.ui.repurchaseAct.downlines

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.network.responses.PackItem


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class RepurchaseDownLineViewModel(application: Application) : NSViewModel(application) {

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
