package com.moneytree.app.common.callbacks

import com.moneytree.app.repository.network.responses.NSPackageData

/**
 * user data callback
 *
 * @constructor Create empty user data callback
 */
interface NSPackageClickCallback {
    fun onResponse(packageData: NSPackageData)
}
