package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSRegisterListData

/**
 * The interface to listen the click on Info select
 */
interface NSMemberActiveSelectCallback {

    /**
     * Invoked when the info click
     */
    fun onClick(data: NSRegisterListData)
}
