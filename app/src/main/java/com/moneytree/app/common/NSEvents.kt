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
 * Event that triggered when click on Fragment change
 */
class NSFragmentChange(var fragment: Fragment)

/**
 * Event that triggered when click on Fragment change
 */
class NSTabChange(var tab: Int)

/**
 * Back press event
 *
 * @constructor Create empty Back press event
 */
class BackPressEvent

/**
 * Search close event
 *
 * @constructor Create empty Search close event
 */
class SearchCloseEvent(val position: Int)

class MainSearchCloseEvent()

/**
 * Search string event
 *
 * @property search
 * @constructor Create empty Search string event
 */
class SearchStringEvent(val search: String, val position: Int)

class MainSearchStringEvent(val search: String)

class NSRepurchaseEventTab()

class NSRetailInfoEventTab()

class NSRoyaltyEventTab()

class NSDownlineEventTab()

class NSPendingEventTab(val isAdded: Boolean)

class NSReceiveEventTab(val isAdded: Boolean)

class NSTransferEventTab(val isAdded: Boolean)

class NSRedemptionEventTab()

class NSTransactionsEventTab()

class NSWalletAmount(val amount: String)

class NSJoiningVoucherEventTab()

class NSProductVoucherEventTab()

class NSSearchClearEvent()

class NSRedeemWalletUpdateEvent()

/**
 * Event that triggered when the permission check
 */
class NSActivityEvent(
	val resultCode: Int,
	val data: Intent?
)
