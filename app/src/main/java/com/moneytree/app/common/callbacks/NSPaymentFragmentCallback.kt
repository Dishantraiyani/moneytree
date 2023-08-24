package com.moneytree.app.common.callbacks

/**
 * The interface to listen the joining voucher list
 */
interface NSPaymentFragmentCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onResponse(callback: NSPaymentDetailCallback)
}
