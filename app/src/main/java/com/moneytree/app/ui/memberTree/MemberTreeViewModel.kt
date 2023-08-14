package com.moneytree.app.ui.memberTree

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSMemberTreeRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for member tree. It handles the business logic to communicate with the model for the member tree and provides the data to the observing UI component.
 */
class MemberTreeViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var isMemberTree: Boolean? = false
    var memberList: MutableList<NSUpLineData> = arrayListOf()
    var isMemberDataAvailable = MutableLiveData<Boolean>()
    private var memberResponse: NSUpLineListResponse? = null

    /**
     * Get member tree data
     *
     */
    fun getMemberTreeData(isShowProgress: Boolean) {
        memberList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }

        NSMemberTreeRepository.getUpLineMemberList(this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        val memberMainListData = data as NSUpLineListResponse
        memberResponse = memberMainListData
        if (memberMainListData.data != null) {
            if (memberMainListData.data.isValidList()) {
                memberList.addAll(memberMainListData.data)
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
