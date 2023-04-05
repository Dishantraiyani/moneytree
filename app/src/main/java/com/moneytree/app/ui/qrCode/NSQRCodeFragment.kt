package com.moneytree.app.ui.qrCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentQrAmountBinding
import com.moneytree.app.databinding.NsFragmentRechargeBinding
import com.moneytree.app.ui.activationForm.NSActivationFormActivity
import com.moneytree.app.ui.recharge.NSRechargeViewModel
import com.moneytree.app.ui.recharge.history.NSRechargeHistoryActivity
import com.moneytree.app.ui.recharge.mobile.NSMobileRechargeFragment


class NSQRCodeFragment : NSFragment() {
	private val viewModel: NSQRCodeViewModel by lazy {
		ViewModelProvider(this)[NSQRCodeViewModel::class.java]
	}
    private var _binding: FragmentQrAmountBinding? = null
    private val rgBinding get() = _binding!!

	companion object {
		fun newInstance(bundle: Bundle?) = NSQRCodeFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			viewModel.qrCodeId = it.getString(NSConstants.KEY_QR_CODE_ID)
			viewModel.walletAmount = it.getString(NSConstants.KEY_WALLET_AMOUNT)
		}
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrAmountBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return rgBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(rgBinding) {
			with(viewModel) {
				with(layoutHeader) {
					clBack.visibility = View.VISIBLE
					tvHeaderBack.text = activity.resources.getString(R.string.qr_code)
					ivBack.visibility = View.VISIBLE
				}

			}
        }
		observeViewModel()
		generateUpi()
    }

	private fun generateUpi() {
		with(viewModel) {
			with(rgBinding) {
				val data: List<String>? = viewModel.qrCodeId?.split("?")
				if (data != null) {
					if (data.size >= 2) {
						var upiData = ""
						if (data.size > 2) {
							for (d in data) {
								upiData += d
							}
						} else {
							upiData = data[1]
						}

						val mainUpiData: List<String> = upiData.split("&")
						for (da in mainUpiData) {
							if (da.startsWith("pn=")) {
								if (da.length > 2) {
									name = da.substring(3).replace("%20", " ")
								}
							} else if (da.contains("pa=")) {
								if (da.length > 2) {
									upiId = da.substring(3)
								}
							} else if (da.contains("am=")) {
								if (da.length > 2) {
									if (da.substring(3).isNotEmpty() && da.substring(3) != "0") {
										etAmount.isEnabled = false
										etAmount.setText(da.substring(3))
									}
								}
							}
						}

						tvName.text = name
						tvSubUpi.text = upiId
					}
				}
			}
		}
	}

    /**
     * Set listener
     */
    private fun setListener() {
        with(rgBinding) {
			with(viewModel) {
				with(layoutHeader) {
					clBack.setOnClickListener {
						onBackPress()
					}

					btnSubmit.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							note = etNote.text.toString()
							amount = etAmount.text.toString()

							if (qrCodeId.isNullOrEmpty()) {
								Toast.makeText(activity, "Please Enter Valid Detail", Toast.LENGTH_SHORT).show()
							} else if (amount.isNullOrEmpty()) {
								etAmount.error = "Please Enter Amount"
							} else if (amount?.toDouble()!! <= 0) {
								etAmount.error = "Please Enter Valid Amount"
							}  else if (amount?.toDouble()!! > walletAmount!!.toDouble()) {
								etAmount.error = "Not Enough Balance Available"
							} else {
								qrCodeRecharge(activity)
							}
						}
					})
				}
			}
        }
    }

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(viewModel) {
			with(rgBinding) {
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

}
