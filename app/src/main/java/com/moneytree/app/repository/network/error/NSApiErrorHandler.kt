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
        private val TAG: String = NSApiErrorHandler::class.java.simpleName
        private lateinit var errorMessageList: MutableList<Any>
        const val ERROR_LOGIN = "error_login"
        const val ERROR_USER_DETAIL = "error_user_detail"
        const val ERROR_LOGIN_WITH_SEND_OTP = "error_login_with_send_otp"
        const val ERROR_RESEND_LOGIN_WITH_SEND_OTP = "error_resend_login_with_send_otp"
        const val ERROR_REFRESH_TOKEN = "error_refresh_token"
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
        const val ERROR_WALLET_DATA = "error_wallet_data"
        const val ERROR_WALLET_BALANCE_DATA = "error_wallet_balance_data"
        const val ERROR_UPLOAD_FILE = "error_upload_file"
        const val ERROR_UPDATE_MEDIA = "error_update_media"
        const val ERROR_LOGOUT = "error_logout"

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