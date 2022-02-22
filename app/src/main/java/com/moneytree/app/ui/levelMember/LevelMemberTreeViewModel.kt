package com.moneytree.app.ui.levelMember

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSMemberTreeRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSLevelMemberTreeData
import com.moneytree.app.repository.network.responses.NSLevelMemberTreeResponse
import com.moneytree.app.repository.network.responses.NSMemberTreeData
import com.moneytree.app.repository.network.responses.NSMemberTreeResponse


/**
 * The view model class for member tree. It handles the business logic to communicate with the model for the member tree and provides the data to the observing UI component.
 */
class LevelMemberTreeViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var isMemberTree: Boolean? = false
    var memberList: MutableList<NSLevelMemberTreeData> = arrayListOf()
    var isMemberDataAvailable = MutableLiveData<Boolean>()
    private var memberResponse: NSLevelMemberTreeResponse? = null

    /**
     * Get member tree data
     *
     */
    fun getMemberTreeData(isShowProgress: Boolean) {
        memberList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSMemberTreeRepository.getLevelWiseTree(this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        val memberMainListData = data as NSLevelMemberTreeResponse
        memberResponse = memberMainListData
        if (memberMainListData.data != null) {
            if (memberMainListData.data.isValidList()) {
                memberList.addAll(memberMainListData.data!!)
            }
            isMemberDataAvailable.value = memberList.isValidList()
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
}