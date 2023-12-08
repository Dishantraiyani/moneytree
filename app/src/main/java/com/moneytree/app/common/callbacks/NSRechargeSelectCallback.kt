package com.moneytree.app.common.callbacks

/**
 * The interface to listen the click on recharge select
 */
interface NSRechargeSelectCallback {

    /**
     * Invoked when the recharge click
     */
    fun onClick(position: Int, selectedType: String)
}