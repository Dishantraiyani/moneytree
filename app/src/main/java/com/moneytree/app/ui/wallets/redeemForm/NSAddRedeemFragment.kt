package com.moneytree.app.ui.wallets.redeemForm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.NSAlertButtonClickEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.databinding.NsFragmentAddRedeemBinding
import com.moneytree.app.ui.levelMember.LevelMemberTreeFragment
import com.moneytree.app.ui.wallets.redeem.NSRedeemViewModel
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
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.redeem)
                ivBack.visibility = View.VISIBLE
				tvWalletAmount.text = if (redeemModel.availableBalance.isNullOrEmpty()) "0" else redeemModel.availableBalance
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(adBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    onBackPress()
                }

				btnSubmit.setOnClickListener {
					if (etAmount.text.toString().isEmpty()) {
						etAmount.error = activity.resources.getString(R.string.please_enter_amount)
						return@setOnClickListener
					} else if (etTransactionPassword.text.toString().isEmpty()) {
						etTransactionPassword.error = activity.resources.getString(R.string.please_enter_password)
						return@setOnClickListener
					} else {
						val amount = etAmount.text.toString()
						if (amount.toDouble() > 0) {
							redeemModel.redeemAmountSave(amount, etTransactionPassword.text.toString(), true)
						} else {
							Toast.makeText(activity, activity.resources.getString(R.string.please_enter_valid_amount), Toast.LENGTH_SHORT).show()
						}
					}
				}

            }
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

				isRedeemWalletDataAvailable.observe(
					viewLifecycleOwner
				) { isRedeem ->
					if (isRedeem) {
						if (successResponse?.status == true) {
							showSuccessDialog(activity.resources.getString(R.string.app_name), successResponse?.message, NSConstants.REDEEM_SAVE_CLICK)
						}
					}
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
