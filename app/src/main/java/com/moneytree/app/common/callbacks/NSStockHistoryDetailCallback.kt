package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.RepurchaseDataItem

/**
 * The interface to listen the joining voucher list
 */
interface NSStockHistoryDetailCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onResponse(item: RepurchaseDataItem)
}
