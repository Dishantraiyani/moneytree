package com.moneytree.app.ui.success

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.FragmentSuccessBinding
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.ui.recharge.history.NSRechargeHistoryActivity

class SuccessFragment : NSFragment() {
	private var _binding: FragmentSuccessBinding? = null

	private val successBinding get() = _binding!!
	private var successFailData: String? = null
	private var successResponse: NSSuccessResponse? = null

	companion object {
		fun newInstance(bundle: Bundle?) = SuccessFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			successFailData = it.getString(NSConstants.KEY_SUCCESS_FAIL)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentSuccessBinding.inflate(inflater, container, false)
		setSuccessFailMessage()
		setListener()
		return successBinding.root
	}

	private fun setSuccessFailMessage() {
		with(successBinding) {
			if (!successFailData.isNullOrEmpty()) {
				successResponse = Gson().fromJson(successFailData, NSSuccessResponse::class.java)

				if (successResponse?.status == true) {
					ivSuccessFail.setImageResource(R.drawable.ic_success)
					tvSuccessFailTitle.text = resources.getString(R.string.success)
					tvMessage.text = successResponse?.message
				} else {
					ivSuccessFail.setImageResource(R.drawable.ic_failed)
					tvSuccessFailTitle.text = resources.getString(R.string.failed)
					tvMessage.text = successResponse?.message
				}
			} else {
				ivSuccessFail.setImageResource(R.drawable.ic_failed)
				tvSuccessFailTitle.text = resources.getString(R.string.failed)
				tvMessage.text = resources.getString(R.string.something_went_wrong)
			}
		}
	}

	private fun setListener() {
		with(successBinding) {
			tvContinueButton.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					if (successResponse?.isPaymentMode == true) {

					} else {
						switchActivity(
							NSRechargeHistoryActivity::class.java,
							flags = intArrayOf(Intent.FLAG_ACTIVITY_CLEAR_TOP)
						)
					}
					finish()
				}
			})
		}
	}
}
