package com.moneytree.app.common


/**
 * Class that contains constants that used across modules
 */
class NSConstants {
    companion object {
        const val KEY_LOGIN_DATA = "key_login_data"
        const val UNKNOWN_HOST_EXCEPTION = "Unable to reach server"
        const val KEY_ALERT_BUTTON_POSITIVE = "alertButtonPositive"
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

        //Add Image
        const val POSITIVE_CLICK = "positive_button_click"
        const val REDEEM_SAVE_CLICK = "redeem_save_button_click"
        const val LOGOUT_CLICK = "logout_button_click"

        //Pagination
        const val PAGINATION = 25
        var IS_LOGIN_SUCCESS: Boolean = false
    }
}
