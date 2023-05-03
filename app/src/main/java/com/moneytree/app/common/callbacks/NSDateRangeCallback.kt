package com.moneytree.app.common.callbacks

/**
 * The interface to listen the joining voucher list
 */
interface NSDateRangeCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onDateRangeSelect(startDate: String, endDate: String, type: String)
}
