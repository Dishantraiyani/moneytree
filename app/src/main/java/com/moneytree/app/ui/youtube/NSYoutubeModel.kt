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
    private val youtubeList: MutableList<YoutubeItems> = ArrayList()
    var isYoutubeVideosAvailable = MutableLiveData<YoutubeResponse>()
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
		val youtubeRequestMap: HashMap<String, String> = hashMapOf()
		youtubeRequestMap["key"] = "AIzaSyCw5Wbju4gtWuIudqYcRy7h2T424GQcnMo"
		youtubeRequestMap["channelId"] = "UCVdmNa50qiK4jlT46LFFjWg"
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
				isYoutubeVideosAvailable.value = data as YoutubeResponse?
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

	fun getFilteredList(response: YoutubeResponse?): MutableList<YoutubeItems>? {
		return response?.items?.filterTo(youtubeList) { it.id?.videoId != null }
	}
}
