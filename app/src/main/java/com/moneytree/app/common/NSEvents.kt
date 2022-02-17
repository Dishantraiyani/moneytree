package com.moneytree.app.common

import android.content.Intent
import androidx.fragment.app.Fragment
import com.moneytree.app.repository.network.responses.NSDataUser

/**
 * Class that contains events that used across modules
 */

/**
 * Event that triggered when logout needs to do
 */
class NSLogoutEvent

/**
 * The event triggered when the network state changed
 *
 * @param isNetworkConnected The status of the network state
 */
class NSNetworkStateChangeEvent(private var isNetworkConnected: Boolean)

/**
 * The event that is triggered when a button is clicked in the alert dialog
 */
class NSAlertButtonClickEvent(val buttonType: String, val alertKey: String)

/**
 * Event that triggered when login or register select
 */
class NSLoginRegisterEvent(val data: NSDataUser?)

/**
 * Event that triggered when the permission check
 */
class NSPermissionEvent(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray
)

/**
 * Event that triggered when the permission check
 */
class NSActivityEvent(
    val resultCode: Int,
    val data: Intent?
)

/**
 * Event that triggered when click on Fragment change
 */
class NSFragmentChange(var fragment: Fragment)

/**
 * Event that triggered when click on Fragment change
 */
class NSTabChange(var tab: Int)

class BackPressEvent

class SearchCloseEvent

class SearchStringEvent(val search: String)
