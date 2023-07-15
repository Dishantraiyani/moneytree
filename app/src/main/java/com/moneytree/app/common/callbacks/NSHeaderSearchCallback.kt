package com.moneytree.app.common.callbacks

/**
 * The interface to listen the joining voucher list
 */
interface NSHeaderSearchCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onHeader(text: String, tabPosition: Int, isClose: Boolean)
}
