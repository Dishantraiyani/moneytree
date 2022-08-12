package com.moneytree.app.common

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.network.manager.NSApiManager
import org.greenrobot.eventbus.EventBus

/**
 * The MoneyTree application class containing Preference, network manager and functionality
 * to be used across modules
 */
class NSApplication : Application() {
    private lateinit var preferences: NSPreferences
    private lateinit var apiManager: NSApiManager

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initInstance()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * To initialize global instances that be used across modules
     */
    private fun initInstance() {
        instance = this
        preferences = NSPreferences(this)
        apiManager = NSApiManager()
        MainDatabase.appDatabase(applicationContext)
        isAlertShown = false
    }

    /**
     * To get instances of shared preferences
     */
    fun getPrefs(): NSPreferences = preferences

    /**
     * To get instance of Api manager
     */
    fun getApiManager(): NSApiManager = apiManager

    companion object {
        private lateinit var instance: NSApplication
        private var isNetworkConnected: Boolean = false

        // App opening alert shown status
        var isAlertShown: Boolean = false

        /**
         * To get the application instance
         *
         * @return The application instance
         */
        fun getInstance(): NSApplication = instance

        /**
         * To check the internet connection status
         *
         * @return Whether internet connected or not
         */
        fun isNetworkConnected(): Boolean = isOnline(getInstance().applicationContext)

		/**
		 * To start the network connection listener
		 */
		private fun isOnline(context: Context): Boolean {
			val connectivityManager =
				context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val capabilities =
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
				} else {
					@Suppress("DEPRECATION") val networkInfo =
						connectivityManager.activeNetworkInfo ?: return false
					@Suppress("DEPRECATION")
					return networkInfo.isConnected
				}
			if (capabilities != null) {
				if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
					return true
				} else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
					return true
				} else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
					return true
				}
			}
			return false
		}
    }
}
