package com.moneytree.app.common

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.network.manager.NSApiManager
import org.greenrobot.eventbus.EventBus

/**
 * The DoT application class containing Preference, network manager and functionality
 * to be used across modules
 */
class NSApplication : Application() {
    private lateinit var preferences: NSPreferences
    private lateinit var apiManager: NSApiManager
        private lateinit var permissionHelper: NSPermissionHelper

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
        permissionHelper = NSPermissionHelper(this)
        MainDatabase.appDatabase(applicationContext)

        startNetworkListener()
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

    /**
     * To get the instance of Permission helper
     *
     * @return The permission helper instance
     */
    fun getPermissionHelper(): NSPermissionHelper = permissionHelper


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
        fun isNetworkConnected(): Boolean = isNetworkConnected

        /**
         * To start the network connection listener
         */
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        private fun startNetworkListener() {
            val connectivityManager =
                getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(),
                object :
                    ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        isNetworkConnected = true
                        EventBus.getDefault()
                            .post(
                                NSNetworkStateChangeEvent(
                                    isNetworkConnected
                                )
                            )
                    }

                    override fun onLost(network: Network) {
                        isNetworkConnected = false
                        EventBus.getDefault()
                            .post(
                                NSNetworkStateChangeEvent(
                                    isNetworkConnected
                                )
                            )
                    }
                })
        }
    }
}