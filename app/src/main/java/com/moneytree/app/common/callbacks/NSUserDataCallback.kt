package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSDataUser

interface NSUserDataCallback {
    fun onResponse(userDetail: NSDataUser)
}