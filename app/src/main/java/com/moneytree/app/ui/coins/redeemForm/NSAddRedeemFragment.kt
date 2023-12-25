package com.moneytree.app.ui.coins.redeemForm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addTextChangeListener
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.NsFragmentAddRedeemBinding
import com.moneytree.app.ui.wallets.redeemForm.NSAddRedeemFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSAddRedeemFragment : NSFragment() {
	private val redeemModel: NSRedeemSaveViewModel by lazy {
		ViewModelProvider(this).get(NSRedeemSaveViewModel::class.java)
	}
	private var _binding: NsFragmentAddRedeemBinding? = null
	private val adBinding get() = _binding!!

	companion object {
		fun newInstance(bundle: Bundle?) = NSAddRedeemFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(redeemModel) {
				availableBalance = it.getString(NSConstants.KEY_AVAILABLE_BALANCE)
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentAddRedeemBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		observeViewModel()
		return adBinding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		with(adBinding) {
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.redeem))
			tvWalletAmount.text =
				if (redeemModel.availableBalance.isNullOrEmpty()) "0" else redeemModel.availableBalance
			adBinding.tvReceivableAmount.text = "0"
		}
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(adBinding) {
			with(layoutHeader) {
				etAmount.addTextChangeListener { searchText ->
					calculateCutAmount(searchText)
				}

				btnSubmit.setSafeOnClickListener {
					Log.d("BtnSubmitCall", "setListener: ")
					if (etAmount.text.toString().isEmpty()) {
						etAmount.error = activity.resources.getString(R.string.please_enter_amount)
					} else if (etTransactionPassword.text.toString().isEmpty()) {
						etTransactionPassword.error = activity.resources.getString(R.string.please_enter_transaction_password)
					} else {
						if (NSUtilities.checkKycVerified()) {
							btnSubmit.isEnabled = false
							btnSubmit.isClickable  = false
							redeem()
							return@setSafeOnClickListener
						}
						showCommonDialog("Kyc Verification", activity.resources.getString(R.string.your_kyc_verification_ask), "Yes", "No", callback = object : NSDialogClickCallback {
							override fun onClick(isOk: Boolean) {
								if (isOk) {
									NSUtilities.isKycVerified(activity, false)
								} else {
									btnSubmit.isEnabled = false
									btnSubmit.isClickable  = false
									redeem()
								}
							}
						})
					}
				}
			}
		}
	}

	private fun redeem() {
		adBinding.apply {
			val amount = etAmount.text.toString()
			val availableBalance = (redeemModel.availableBalance?:"0.0").toDouble()
			if (amount.toDouble() > 0 && availableBalance > 0) {
				if (amount.toDouble() <= availableBalance) {
					redeemModel.redeemAmountSave(
						amount,
						etTransactionPassword.text.toString(),
						true
					) { isSuccess, response ->
						if (response.status && isSuccess) {
							showSuccessDialog(
								activity.resources.getString(R.string.app_name),
								response.message,
								NSConstants.REDEEM_SAVE_CLICK
							)
						} else {
							btnSubmit.isEnabled = true
							btnSubmit.isClickable  = true
						}
					}
				} else {
					Toast.makeText(
						activity,
						activity.resources.getString(R.string.not_enough_balance),
						Toast.LENGTH_SHORT
					).show()
				}
			} else {
				Toast.makeText(
					activity,
					activity.resources.getString(R.string.please_enter_valid_amount),
					Toast.LENGTH_SHORT
				).show()
			}
		}
	}

	private fun calculateCutAmount(amountStr: String) {
		if (amountStr.isNotEmpty()) {
			val amount = amountStr.toDouble()
			val cutAmount = amount * 0.95 // 5% cut
			val received = "$cutAmount"
			adBinding.tvReceivableAmount.text = received
		} else {
			adBinding.tvReceivableAmount.text = "0"
		}
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(redeemModel) {
			with(adBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
					showAlertDialog(errorMessage)
				}

				apiErrors.observe(viewLifecycleOwner) { apiErrors ->
					parseAndShowApiError(apiErrors)
				}

				noNetworkAlert.observe(viewLifecycleOwner) {
					showNoNetworkAlertDialog(
						getString(R.string.no_network_available),
						getString(R.string.network_unreachable)
					)
				}

				validationErrorId.observe(viewLifecycleOwner) { errorId ->
					showAlertDialog(getString(errorId))
				}
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.REDEEM_SAVE_CLICK) {
			val intent = Intent()
			activity.setResult(NSRequestCodes.REQUEST_WALLET_UPDATE, intent)
			finish()
		}
	}

}
