package com.moneytree.app.ui.success

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.ActivityRechargeBinding
import com.moneytree.app.databinding.ActivitySuccessBinding
import com.moneytree.app.databinding.FragmentSuccessBinding
import com.moneytree.app.databinding.NsFragmentMainVouchersBinding
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.ui.recharge.history.NSRechargeHistoryActivity
import com.moneytree.app.ui.wallets.redeemForm.NSAddRedeemFragment

class SuccessFragment : NSFragment() {
	private var _binding: FragmentSuccessBinding? = null

	private val successBinding get() = _binding!!
	private var successFailData: String? = null

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
				val successFail = Gson().fromJson(successFailData, NSSuccessResponse::class.java)

				if (successFail.status) {
					ivSuccessFail.setImageResource(R.drawable.ic_success)
					tvSuccessFailTitle.text = resources.getString(R.string.success)
					tvMessage.text = successFail.message
				} else {
					ivSuccessFail.setImageResource(R.drawable.ic_failed)
					tvSuccessFailTitle.text = resources.getString(R.string.failed)
					tvMessage.text = successFail.message
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
					switchActivity(NSRechargeHistoryActivity::class.java, flags = intArrayOf(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					finish()
				}
			})
		}
	}
}
