package com.moneytree.app.common


/**
 * Class that contains constants that used across modules
 */
class NSConstants {
    companion object {
        const val KEY_IS_MEETING_IMAGES = "key_is_meeting_images"
        const val KEY_IS_MEETING_REVIEW = "key_is_meeting_review"
        const val KEY_IS_SELECTED_ADDRESS = "key_is_selected_address"
        const val KEY_IS_SELECTED_MEETING = "key_is_selected_meeting"
        const val KEY_IS_FROM_ORDER = "key_is_from_order"
        const val KEY_IS_DEFAULT_ADDRESS = "key_is_default_address"
        const val KEY_IS_ADD_ADDRESS = "key_is_add_address"
        const val KEY_IS_ADD_MEETING = "key_is_add_meeting"
        const val KEY_LOGIN_DATA = "key_login_data"
        const val UNKNOWN_HOST_EXCEPTION = "Unable to reach server"
        const val KEY_ALERT_BUTTON_POSITIVE = "alertButtonPositive"
        const val KEY_ALERT_DOCTOR_SEND = "alertButtonDoctorSend"
        const val KEY_ALERT_BUTTON_NEGATIVE = "alertButtonNegative"
        const val KEY_REPURCHASE_INFO = "key_repurchase_info"
        const val KEY_ROYALTY_INFO = "key_royalty_info"
        const val KEY_RETAIL_INFO = "key_retail_info"
        const val KEY_HOME_DETAIL = "key_home_detail"
        const val REFRESH_TOKEN_ENABLE = "refresh_token_enable"
        const val MEMBER_TREE_ENABLE = "member_tree_enable"
        const val KEY_SLOTS_INFO = "key_slots_info"
        const val KEY_CHANGE_PASSWORD = "key_change_password"
		const val KEY_AVAILABLE_BALANCE = "key_available_balance"
		const val KEY_WALLET_VERIFY = "key_wallet_verify"
		const val KEY_PACKAGE_DETAIL = "key_package_detail"
		const val KEY_IS_VOUCHER_FROM_TRANSFER = "key_voucher_from_transfer"
		const val KEY_IS_PACKAGE_ID = "key_voucher_package_id"
		const val KEY_IS_VOUCHER_QUANTITY = "key_voucher_quantity"
		const val KEY_PRODUCT_CATEGORY = "key_product_category"
		const val KEY_PRODUCT_CATEGORY_NAME = "key_product_category_name"
		const val KEY_PRODUCT_DETAIL = "key_product_detail"
		const val KEY_PRODUCT_FULL_LIST = "key_product_full_list"
		const val KEY_NOTIFICATION_DETAIL = "key_notification_detail"
		const val KEY_MEMBER_ACTIVATION_FORM = "key_member_activation_form"
		const val KEY_MEMBER_FORM_ACTIVATION_FORM = "key_member_form_activation_form"
		const val KEY_MEMBER_FORM_ACTIVATION_FORM_DETAIL = "key_member_form_activation_form_detail"
		const val KEY_MEMBER_LEVEL_NUMBER = "key_member_level_number"
		const val KEY_OFFER_DETAIL_TYPE = "key_offer_detail_type"
		const val KEY_DEFAULT_TYPE = "default_type"

		const val KEY_BANNER_URL = "key_banner_url"
		const val KEY_BANNER_POSITION = "key_banner_position"

        //Add Image
        const val POSITIVE_CLICK = "positive_button_click"
        const val REDEEM_SAVE_CLICK = "redeem_save_button_click"
        const val RECHARGE_SAVE_CLICK = "recharge_save_button_click"
        const val MEMBER_ACTIVATE_CLICK = "member_activate_button_click"
        const val MEMBER_REGISTER_SUCCESS = "member_register_success"
        const val MEMBER_TH_ACTIVATE_CLICK = "member_th_activate_button_click"
        const val LOGOUT_CLICK = "logout_button_click"
        const val COMMON_CLICK = "common_button_click"
        const val SIGNUP_CLICK = "sign_up_common_button_click"
		const val PRODUCT_SEND_CLICK = "product_send_button_click"

		const val KEY_RECHARGE_VERIFY = "key_recharge_verify"
		const val KEY_RECHARGE_TYPE = "key_recharge_type_selected"
		const val KEY_RECHARGE_DETAIL = "key_recharge_detail_selected"
		const val KEY_QR_CODE_ID = "key_qr_code_id"
		const val KEY_WALLET_AMOUNT = "key_wallet_amount"
		const val KEY_KYC_TYPE = "key_kyc_type"
		const val KEY_SUCCESS_FAIL = "key_success_fail"
		var SOCKET_TYPE: String? = null
		const val SUPER_SOCKET_TYPE: String = "Super Stockiest"
		const val  NORMAL_SOCKET_TYPE: String = "Stockiest"
		const val  SOCKET_HISTORY: String = "Stock"
		const val  REPURCHASE_HISTORY: String = "Repurchase"
		const val  RETAIL_LIST: String = "Retail"
		const val  ROYALTY_LIST: String = "Royalty"
		const val STOCK_DETAIL_ID = "stock_detail_id"
		const val ORDER_DETAIL_ID = "order_detail_id"
		const val ORDER_DETAIL_ID_DETAIL = "order_detail_id_detail"
		const val KEY_STOCK_TYPE = "key_stock_type"

        //Pagination
        const val PAGINATION = 25
        var IS_LOGIN_SUCCESS: Boolean = false
		var isGridMode: Boolean = false
		var tabName: Class<*>? = null
		var STOCK_UPDATE: Int = -1
		const val STOCK_HISTORY_LIST: String = "stock_history_list"
		const val YOUTUBE_DETAIL = "key_youtube_detail"
		const val YOUTUBE_FULL_RESPONSE = "key_youtube_full_response"
		const val KEY_LOCK_SCREEN = "key_lock_screen"
		var WALLET_BALANCE = "0"
		const val CUSTOMER_CARE = "+918735985994"
		const val CUSTOMER_CARE_EMAIL = "support@moneytree.biz"
		const val CUSTOMER_CARE_WEB = "www.moneytree.biz"
		const val DOCTOR_DETAIL = "doctor_detail"
		const val DOCTOR_ID = "doctor_id"
		const val PATIENT_NAME = "name"
		const val PATIENT_GENDER = "gender"
		const val PATIENT_AGE = "age"
		const val PATIENT_NUMBER = "mobile"
		const val PATIENT_DOB = "dob"
		const val PATIENT_REMARK = "remark"

		const val PAYMENT_WALLET = "wallet"
		const val PAYMENT_GATEWAY = "payment"
		const val PAYMENT_MT_COIN = "mt_coin"
		const val DIRECTORY_PATH = "/moneyTree/"
		const val DIRECTORY_PATH_ID = "/moneyTree/idCard/"
		const val STOCK_NOT_AVAILABLE = "No Stock Available"
		const val MAX_TITLE = "Max "
		const val MAX_TITLE_SECOND = " Qty Available"
		var RECHARGE_USING_PAYMENT_GATEWAY: Boolean = false
	}
}
