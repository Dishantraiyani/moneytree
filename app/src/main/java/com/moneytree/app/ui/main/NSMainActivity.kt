package com.moneytree.app.ui.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.moneytree.app.R
import com.moneytree.app.common.BackPressFragmentHomeEvent
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSTabChange
import com.moneytree.app.databinding.DialogCloseBinding
import com.moneytree.app.databinding.DialogUpdateBinding
import com.moneytree.app.databinding.NsActivityMainBinding
import com.moneytree.app.ui.home.NSHomeFragment
import org.greenrobot.eventbus.EventBus

class NSMainActivity : NSActivity() {
    private lateinit var mainBinding: NsActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = NsActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(MainFragment.newInstance(), false, mainBinding.mainContainer.id)
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
}
