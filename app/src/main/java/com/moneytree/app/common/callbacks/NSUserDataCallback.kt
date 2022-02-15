package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSUserResponse

interface NSUserDataCallback {
    fun onResponse(userDetail: NSDataUser)
}