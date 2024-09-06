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
        const val ERROR_RECHARGE_LIST_DATA = "error_recharge_list_data"
        const val ERROR_REGISTER_SAVE_DATA = "error_register_save_data"
        const val ERROR_DIRECT_REGISTER_SAVE_DATA = "error_direct_register_save_data"
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
        const val ERROR_MEMBER_DETAIL = "error_member_detail"
        const val ERROR_COMMON = "error_common"
        const val ERROR_CHECK_STOKE_TYPE = "error_check_stock_type"
        const val ERROR_CHANGE_PASSWORD = "error_change_password"
        const val ERROR_CHANGE_TRAN_PASSWORD = "error_change_tran_password"
        const val ERROR_UPDATE_PROFILE = "error_update_profile"
        const val ERROR_WALLET_LIST_DATA = "error_wallet_list_data"
        const val ERROR_SERVICE_PROVIDER_DATA = "error_service_provider_data"
        const val ERROR_RECHARGE_SAVE_DATA = "error_recharge_save_data"
        const val ERROR_RECHARGE_UPDATE_STATUS = "error_recharge_update_status"
        const val ERROR_RECHARGE_FETCH_DATA = "error_recharge_fetch_data"
		const val ERROR_REDEEM_LIST_DATA = "error_redeem_list_data"
		const val ERROR_REDEEM_SAVE_DATA = "error_redeem_save_data"
		const val ERROR_TRANSFER_WALLET_AMOUNT = "error_transfer_wallet_amount"
		const val ERROR_PACKAGE_MASTER_LIST = "error_package_master_list"
		const val ERROR_PACKAGE_VISE_QUANTITY = "error_package_vise_quantity"
		const val ERROR_PACKAGE_VISE_TRANSFER = "error_voucher_vise_transfer"
		const val ERROR_CATEGORY_PRODUCT = "error_category_product"
		const val ERROR_PRODUCT_LIST = "error_product_list"
		const val ERROR_ACTIVATION_LIST = "error_activation_list"
		const val ERROR_ACTIVATION_PACKAGE_LIST = "error_activation_package_list"
		const val ERROR_ACTIVATION_PACKAGE_SAVE = "error_activation_package_save"
		const val ERROR_UP_LINE_MEMBER_TREE = "error_up_line_member_tree"
        const val ERROR_CHECK_VERSION = "error_check_version"
        const val ERROR_QR_SCAN = "error_qr_scan"
        const val ERROR_PAYMENT_MODE = "error_payment_mode"
		const val ERROR_PRODUCT_SEND_DATA = "error_product_send_data"
		const val ERROR_REPURCHASE_STOCK_DATA = "error_repurchase_stock_data"
		const val ERROR_STOCK_TRANSFER_DATA = "error_stock_transfer_data"
		const val ERROR_STOCK_TRANSFER_DETAIL_DATA = "error_stock_transfer_detail_data"
		const val ERROR_YOUTUBE_DATA = "error_youtube_data"
		const val ERROR_NOTIFICATION_LIST = "error_notification_list"
		const val ERROR_DOWNLOAD_LIST = "error_download_list"
		const val ERROR_SET_DEFAULT = "error_set_default"
		const val ERROR_SEND_MESSAGE = "error_send_message"
        const val ERROR_DISEASES_LIST_DATA = "error_diseases_list_data"
        const val ERROR_SEARCH_LIST_DATA = "error_search_list_data"
        const val ERROR_DOCTOR_LIST_DATA = "error_doctor_list_data"
        const val ERROR_KYC_DATA_SEND = "error_kyc_data_send"
        const val ERROR_KYC_STATUS = "error_kyc_status_check"
        const val ERROR_PLAN_LIST = "error_plan_list"
        const val ERROR_MEETING_LIST = "error_meeting_list"
        const val ERROR_SAVE_MEETING = "error_save_meeting"
        const val ERROR_DELETE_MEETING = "error_delete_meeting"

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
            errorMessageList.clear()
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
                            errorMessageList.clear()
                            errorMessageList.add(rawErrorResponse.message())
                            errorMessageList.add(errorString)
                        }
                    }
                    in 500..503 -> {
                        val errorString = context.getString(R.string.error_01, responseErrorCode)
						errorMessageList.clear()
                        errorMessageList.add(rawErrorResponse.message())
                        errorMessageList.add(errorString)
                    }
                    else -> {
                        val errorString = context.getString(R.string.error_01, responseErrorCode)
						errorMessageList.clear()
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
