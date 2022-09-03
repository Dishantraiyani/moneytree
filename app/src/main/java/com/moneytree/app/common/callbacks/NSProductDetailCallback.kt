package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.ProductDataDTO

/**
 * The interface to listen the joining voucher list
 */
interface NSProductDetailCallback {

    /**
     * Invoked when the get joining vouchers
     */
    fun onResponse(productDetail: ProductDataDTO)
}
