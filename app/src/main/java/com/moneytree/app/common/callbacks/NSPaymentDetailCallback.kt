package com.moneytree.app.common.callbacks

/**
 * The interface to listen the joining voucher list
 */
interface NSPaymentDetailCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onResponse(paymentId: String, paymentData: String, isSuccess: Boolean)
}
