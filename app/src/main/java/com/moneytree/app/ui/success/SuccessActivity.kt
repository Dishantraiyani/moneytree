package com.moneytree.app.ui.success

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.databinding.ActivityRechargeBinding
import com.moneytree.app.databinding.ActivitySuccessBinding
import com.moneytree.app.repository.network.responses.NSSuccessResponse

class SuccessActivity : AppCompatActivity() {
	private lateinit var successBinding: ActivitySuccessBinding

	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		successBinding = ActivitySuccessBinding.inflate(layoutInflater)
		setContentView(successBinding.root)
		setSuccessFailMessage()
		setListener()
    }

	private fun setSuccessFailMessage() {
		val data = intent.getStringExtra(NSConstants.KEY_SUCCESS_FAIL)
		with(successBinding) {
			if (!data.isNullOrEmpty()) {
				val successFail = Gson().fromJson(data, NSSuccessResponse::class.java)

				if (successFail.status) {
					ivSuccessFail.setImageResource(R.drawable.ic_success)
					tvSuccessFailTitle.text = resources.getString(R.string.success)
					tvMessage.text = resources.getString(R.string.success_msg)
				} else {
					ivSuccessFail.setImageResource(R.drawable.ic_failed)
					tvSuccessFailTitle.text = resources.getString(R.string.failed)
					tvMessage.text = resources.getString(R.string.failed_msg)
				}
			} else {
				ivSuccessFail.setImageResource(R.drawable.ic_failed)
				tvSuccessFailTitle.text = resources.getString(R.string.failed)
				tvMessage.text = resources.getString(R.string.failed_msg)
			}
		}
	}

	private fun setListener() {
		with(successBinding) {
			tvContinueButton.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					finish()
				}
			})
		}
	}
}
