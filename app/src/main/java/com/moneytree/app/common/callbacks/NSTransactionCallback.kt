package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSWalletData

interface NSTransactionCallback {
    fun onTransactionClick(transaction: NSWalletData)
}
