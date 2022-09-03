package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSCategoryData

/**
 * The interface to listen the joining voucher list
 */
interface NSProductCategoryCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onResponse(categoryData: NSCategoryData)
}
