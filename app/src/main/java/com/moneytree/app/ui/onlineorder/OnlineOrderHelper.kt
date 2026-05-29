package com.moneytree.app.ui.onlineorder

import com.moneytree.app.repository.network.responses.ProductDataDTO

object OnlineOrderHelper {
	private var orderList: HashMap<String, ProductDataDTO> = hashMapOf()
	
	fun clearOrderList() {
		orderList.clear()
	}
	
	fun setOrderList(model: ProductDataDTO) {
		val key = model.productId + "_" + model.categoryId
		orderList[key] = model
	}
	
	fun getOrderList(): MutableList<ProductDataDTO> {
		val list: MutableList<ProductDataDTO> = arrayListOf()
		for ((key, value) in orderList.entries) {
			list.add(value)
		}
		return list
	}
	
	fun getOrder(model: ProductDataDTO): ProductDataDTO? {
		val key = model.productId + "_" + model.categoryId
		return orderList[key]
	}
	
	fun removeOrder(model: ProductDataDTO): MutableList<ProductDataDTO> {
		val key = model.productId + "_" + model.categoryId
		orderList.remove(key)
		return getOrderList()
	}
	
	fun isOrderAdded(model: ProductDataDTO) : Boolean {
		val key = model.productId + "_" + model.categoryId
		return orderList.contains(key)
	}
}