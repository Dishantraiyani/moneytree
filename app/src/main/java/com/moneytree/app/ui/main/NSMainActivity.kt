package com.moneytree.app.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.moneytree.app.R
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSTabChange
import com.moneytree.app.common.utils.isLocationPermissionGranted
import com.moneytree.app.common.utils.requestLocationPermissions
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.databinding.DialogCloseBinding
import com.moneytree.app.ui.home.NSHomeFragment
import org.greenrobot.eventbus.EventBus

class NSMainActivity : NSActivity() {
    private lateinit var mainBinding: ActivityCommonBinding
	private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        loadInitialFragment()
		locationPermission()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(MainFragment.newInstance(), false, mainBinding.commonContainer.id)
    }

	override fun onBackPressed() {
		if (NSConstants.tabName != NSHomeFragment::class.java) {
			EventBus.getDefault().post(NSTabChange(R.id.tb_home))
		} else if (NSConstants.tabName == NSHomeFragment::class.java) {
			showBackDialog(activity = this)
		} else {
			super.onBackPressed()
		}
	}

	private fun showBackDialog(activity: Activity) {
		val builder = AlertDialog.Builder(activity)
		val view: View = activity.layoutInflater.inflate(R.layout.dialog_close, null)
		builder.setView(view)
		val bind: DialogCloseBinding = DialogCloseBinding.bind(view)
		val dialog = builder.create()
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		bind.tvCancel.setOnClickListener {
			dialog.dismiss()
		}
		bind.tvOk.setOnClickListener {
			dialog.dismiss()
			super.onBackPressed()
		}
		dialog.show()
	}

	private fun locationPermission() {
		if (isLocationPermissionGranted()) {
			// Permissions are granted, proceed with location-related tasks
		} else {
			// Request permissions using the extension function
			requestLocationPermissions(LOCATION_PERMISSION_REQUEST_CODE)
		}
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission granted, proceed with location-related tasks
			} else {
				// Permission denied, handle accordingly
			}
		}
	}
}
