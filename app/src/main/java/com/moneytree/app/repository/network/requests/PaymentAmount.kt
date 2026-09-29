package com.moneytree.app.repository.network.requests

data class PaymentAmount(
    val walletAmount: Int = 0,
    val creditAmount: Int = 0,
    val cashAmount: Int = 0
)