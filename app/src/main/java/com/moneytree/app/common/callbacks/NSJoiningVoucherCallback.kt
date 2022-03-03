package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSVoucherListData

/**
 * The interface to listen the joining voucher list
 */
interface NSJoiningVoucherCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onResponse(isAvailable: Boolean)
}