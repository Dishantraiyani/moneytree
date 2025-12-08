package com.moneytree.app.ui.noNetwork

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding
import com.muddassir.connection_checker.ConnectionState
import com.muddassir.connection_checker.ConnectivityListener

class NoNetworkActivity : NSActivity(), ConnectivityListener{
	private lateinit var noNetworkBinding: ActivityCommonBinding
	private var isConnectedNetwork = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		noNetworkBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(noNetworkBinding.root)
		initView(noNetworkBinding.root)
		isConnected = false
		loadInitialFragment()
	}

	/**
	 * To initialize No Network fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NoNetworkFragment.newInstance(), false, noNetworkBinding.commonContainer.id)
	}

	override fun onDestroy() {
		super.onDestroy()
		isConnected = true
	}

	override fun onConnectionState(state: ConnectionState) {
		val isConnected = when(state) {
			ConnectionState.CONNECTED -> {
				isConnectedNetwork = true
				true
			}
			ConnectionState.DISCONNECTED -> {
				isConnectedNetwork = false
				false
			}
			else -> {
				isConnectedNetwork = false
				false
			}
		}

		if (isConnected && isConnectedNetwork) {
			onBackPressed()
		}
	}
}
