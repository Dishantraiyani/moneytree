package com.moneytree.app.repository.network.error

import android.content.Context
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants.Companion.REFRESH_TOKEN_ENABLE
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import retrofit2.Response

/**
 * The class to process errors thrown by the web service and give the appropriate error message.
 */
class NSApiErrorHandler {
    companion object {
        private lateinit var errorMessageList: MutableList<Any>
        const val ERROR_LOGIN = "error_login"
        const val ERROR_DASHBOARD = "error_dashboard"
        const val ERROR_REGISTER_LIST_DATA = "error_register_list_data"
        const val ERROR_VOUCHER_PENDING_DATA = "error_voucher_pending_data"
        const val ERROR_VOUCHER_RECEIVE_DATA = "error_voucher_receive_data"
        const val ERROR_VOUCHER_TRANSFER_DATA = "error_voucher_transfer_data"
        const val ERROR_REPURCHASE_LIST_DATA = "error_repurchase_list_data"
        const val ERROR_REPURCHASE_INFO_DATA = "error_repurchase_info_data"
        const val ERROR_RETAIL_INFO_DATA = "error_retail_info_data"
        const val ERROR_ROYAL_INFO_DATA = "error_royal_info_data"
        const val ERROR_ROYALTY_LIST_DATA = "error_royalty_list_data"
        const val ERROR_DOWNLINE_MEMBER_LIST_DATA = "error_downline_member_list_data"
        const val ERROR_MEMBER_TREE = "error_member_tree"
        const val ERROR_LEVEL_WISE_MEMBER_TREE = "error_level_wise_member_tree"
        const val ERROR_LOGOUT = "error_logout"
        const val ERROR_CHANGE_PASSWORD = "error_change_password"
        const val ERROR_CHANGE_TRAN_PASSWORD = "error_change_tran_password"
        const val ERROR_UPDATE_PROFILE = "error_update_profile"
        const val ERROR_WALLET_LIST_DATA = "error_wallet_list_data"
		const val ERROR_REDEEM_LIST_DATA = "error_redeem_list_data"
		const val ERROR_REDEEM_SAVE_DATA = "error_redeem_save_data"

        /**
         * To get the error messages from API endpoints
         *
         * @param rawErrorResponse The error response to parse and get the actual message
         * @return The error message
         */
        fun getApiErrorMessage(
            rawErrorResponse: Response<*>,
            viewModelCallback: NSGenericViewModelCallback
        ) {
            val context: Context = NSApplication.getInstance().applicationContext
            errorMessageList = mutableListOf()

            if (rawErrorResponse.body() == null && rawErrorResponse.errorBody() == null) {
                val errorString = "Session TimeOut!!\n"
                errorMessageList.add(errorString)
                errorMessageList.add("Please Logout and Login again!!!")
            } else {
                when (val responseErrorCode = rawErrorResponse.code()) {
                    in 400..429 -> {
                        if (responseErrorCode == 401) {
                            viewModelCallback.onFailure(REFRESH_TOKEN_ENABLE)
                        } else {
                            val errorString = context.getString(R.string.error_01, responseErrorCode)
                            errorMessageList.add(rawErrorResponse.message())
                            errorMessageList.add(errorString)
                        }
                    }
                    in 500..503 -> {
                        val errorString = context.getString(R.string.error_01, responseErrorCode)
                        errorMessageList.add(rawErrorResponse.message())
                        errorMessageList.add(errorString)
                    }
                    else -> {
                        val errorString = context.getString(R.string.error_01, responseErrorCode)
                        errorMessageList.add(rawErrorResponse.message())
                        errorMessageList.add(errorString)
                    }
                }
            }

            if (errorMessageList.size > 0) {
                viewModelCallback.onError(errorMessageList)
            }
        }
    }
}
