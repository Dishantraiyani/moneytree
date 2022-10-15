package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.RechargeListDataItem

/**
 * The interface to listen the click on Info select
 */
interface NSRechargeRepeatCallback {

    /**
     * Invoked when the info click
     */
    fun onClick(rechargeData: RechargeListDataItem)
}
