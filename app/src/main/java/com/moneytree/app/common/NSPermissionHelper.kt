package com.moneytree.app.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.moneytree.app.R
import com.moneytree.app.common.NSRequestCodes.REQUEST_LOCATION_CODE
import com.moneytree.app.common.NSRequestCodes.REQUEST_SMS_CODE


/**
 * Created by Admin on 25-01-2022.
 */
class NSPermissionHelper(context: Context) {
    val tag: String = NSPermissionHelper::class.java.simpleName
    private val nsContext = context
    private var gpsEnabled = false
    private var networkEnabled = false

    /**
     * Is location permission enable
     *
     * @param activity The activity's context
     * @param locationCode location request code
     * @return location permission check
     */
    fun isLocationPermissionEnable(activity: Activity, locationCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                checkLocationPermission(activity, locationCode)
                false
            }
        } else {
            true
        }
    }

    fun isLocationBackgroundPermissionEnable(activity: Activity, locationCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    locationCode
                )
                false
            }
        } else {
            true
        }
    }

    /**
     * Is location permission enable
     *
     * @param activity The activity's context
     * @param locationCode location request code
     * @return location permission check
     */
    fun isReadPhonePermissionEnable(activity: Activity, locationCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                checkPhonePermission(activity, locationCode)
                false
            }
        } else {
            true
        }
    }

    /**
     * Check location permission
     *
     * @param activity The activity's context
     * @param locationCode location request code
     */
    private fun checkPhonePermission(activity: Activity, locationCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.location_permission)
                    .setMessage(R.string.location_permission_description)
                    .setPositiveButton(R.string.ok) { dialog, which ->
                        NSLog.d(tag, "checkPhonePermission: $dialog $which")
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_CODE
                        )
                    }
                    .create()
                    .show()

            } else ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                locationCode
            )
        }
    }

    /**
     * Check location permission
     *
     * @param activity The activity's context
     * @param locationCode location request code
     */
    private fun checkLocationPermission(activity: Activity, locationCode: Int) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.location_permission)
                    .setMessage(R.string.location_permission_description)
                    .setPositiveButton(R.string.ok) { dialog, which ->
                        NSLog.d(tag, "checkLocationPermission: $dialog $which")
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_CODE
                        )
                    }
                    .create()
                    .show()

            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationCode
                )
            }
        }
    }

    /**
     * Is sms permission enable
     *
     * @param activity The activity's context
     * @return location permission check
     */
    fun isSmsPermissionEnable(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECEIVE_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                checkSmsPermission(activity)
                false
            }
        } else {
            true
        }
    }

    /**
     * Check location permission
     *
     * @param activity The activity's context
     */
    private fun checkSmsPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.RECEIVE_SMS
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.sms_permission)
                    .setMessage(R.string.sms_permission_description)
                    .setPositiveButton(R.string.ok) { dialog, which ->
                        NSLog.d(tag, "checkSmsPermission: $dialog $which")
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.RECEIVE_SMS),
                            REQUEST_SMS_CODE
                        )
                    }
                    .create()
                    .show()

            } else ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                REQUEST_SMS_CODE
            )
        }
    }

    fun isGpsEnable(activity: Activity) {
        val lm = nsContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gpsEnabled && !networkEnabled) {
            // notify user
            AlertDialog.Builder(activity)
                .setTitle(R.string.gps_network_not_enabled)
                .setMessage(R.string.open_location_settings)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    NSLog.d(tag, "checkGpsEnable: $dialog $which")
                    nsContext.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
        }
    }

    private fun isLocationEnabledOrNot(context: Context): Boolean {
        val locationManager: LocationManager? = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showAlertLocation(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, which ->
                NSLog.d(tag, "showAlertLocation: $dialog $which")
                context.startActivity(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }

    fun checkLocation(activity: Activity) {
        if (!isLocationEnabledOrNot(activity)) {
            showAlertLocation(
                activity,
                activity.resources.getString(R.string.gps_enable),
                activity.resources.getString(R.string.please_turn_on_gps)
            )
        }
    }
}