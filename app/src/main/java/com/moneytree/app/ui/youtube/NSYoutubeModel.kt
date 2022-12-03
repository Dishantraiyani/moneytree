package com.moneytree.app.ui.youtube

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSYoutubeRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.YoutubeItems
import com.moneytree.app.repository.network.responses.YoutubeResponse


/**
 * The view model class for voucher. It handles the business logic to communicate with the model for the voucher and provides the data to the observing UI component.
 */
class NSYoutubeModel(application: Application) : NSViewModel(application) {
    val youtubeList: MutableList<YoutubeItems> = ArrayList()
    val youtubeRequestMap: HashMap<String, String> = hashMapOf()
    var youtubeResponse: YoutubeResponse? = null
    var isYoutubeVideosAvailable = MutableLiveData<Boolean>()
    var isJoiningAdded: Boolean = false
	var pageIndex: String = ""

	/**
	 * Get royalty list data
	 *
	 */
	fun getYoutubeVideos(pageIndex: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
		if (pageIndex.isEmpty()) {
			youtubeList.clear()
		}
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		if (isBottomProgress) {
			isBottomProgressShowing.value = true
		}
		youtubeRequestMap.clear()
		youtubeRequestMap["key"] = "AIzaSyCw5Wbju4gtWuIudqYcRy7h2T424GQcnMo"
		youtubeRequestMap["channelId"] = "UCwZuiF2VbJazXZ7BU_r9vEA"
		youtubeRequestMap["part"] = "snippet,id"
		youtubeRequestMap["order"] = "date"
		youtubeRequestMap["maxResults"] = "${NSConstants.PAGINATION}"
		if (pageIndex.isNotEmpty()) {
			youtubeRequestMap["pageToken"] = pageIndex
		}
		NSYoutubeRepository.getYoutubeVideos(youtubeRequestMap, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				if (isBottomProgress) {
					isBottomProgressShowing.value = false
				}
				val youtubeResponseModel = data as YoutubeResponse
				youtubeResponse = youtubeResponseModel
				if (youtubeResponseModel.items.isValidList()) {
					for (item in youtubeResponse?.items!!) {
						if (item.id != null) {
							if (item.id.videoId != null) {
								youtubeList.add(item)
							}
						}
					}
					//youtubeList.addAll(youtubeResponseModel.items)
					isYoutubeVideosAvailable.value = youtubeList.isValidList()
				} else if (pageIndex == "1"){
					isYoutubeVideosAvailable.value = false
				}
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
