package com.moneytree.app.ui.wallets.verifyMember

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.NSWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.NSWalletTransferModel
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSVerifyWalletModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var isWalletTransferDataAvailable = MutableLiveData<Boolean>()
    var isWalletDataAvailable = MutableLiveData<Boolean>()
    var isMemberDetailGet = MutableLiveData<Boolean>()
	var successResponse: NSSuccessResponse? = null
	var memberDetailModel: MemberDetailModel? = null
	var strVerifyData: String? = null
	var verifyTransferModel: NSWalletTransferModel? = null

	fun getDetail() {
		if (strVerifyData?.isNotEmpty() == true) {
			verifyTransferModel = Gson().fromJson(strVerifyData, NSWalletTransferModel::class.java)
			isWalletDataAvailable.value = true
		}
	}

	fun getMemberDetail(memberId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSUserRepository.getMemberDetail(memberId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val modelData = data as NSMemberDetailResponse
				if (modelData.status) {
					memberDetailModel = modelData.data
				}
				isMemberDetailGet.value = true
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

	fun transferVoucherTransfer(transferId: String, qty: String, isShowProgress: Boolean) {
		if (verifyTransferModel != null) {
			if (isShowProgress) {
				isProgressShowing.value = true
			}
			NSVoucherRepository.joiningVoucherTransfer(
				transferId,
				verifyTransferModel!!.packageId!!, qty, this
			)
		}
	}

    /**
     * Get redeem list data
     *
     */
    fun transferWalletAmount(transactionId: String, amount: String, remark: String, password: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSWalletRepository.transferWalletMoney(transactionId, amount, remark, password, this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        successResponse = data as NSSuccessResponse
		isWalletTransferDataAvailable.value = true
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
