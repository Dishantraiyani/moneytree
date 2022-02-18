package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSDataUser

/**
 * user data callback
 *
 * @constructor Create empty user data callback
 */
interface NSUserDataCallback {
    fun onResponse(userDetail: NSDataUser)
}