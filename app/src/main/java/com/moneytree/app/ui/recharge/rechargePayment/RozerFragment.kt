package com.moneytree.app.ui.recharge.rechargePayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPaymentDetailCallback
import com.moneytree.app.common.callbacks.NSPaymentFragmentCallback
import com.moneytree.app.common.rozerpay.RazorpayUtility
import com.moneytree.app.databinding.FragmentRozerBinding
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSErrorPaymentResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.RozerModel


class RozerFragment : NSFragment() {
	private val viewModel: RozerViewModel by lazy {
		ViewModelProvider(this)[RozerViewModel::class.java]
	}
    private var _binding: FragmentRozerBinding? = null
    private val rgBinding get() = _binding!!

	companion object {

		private var callback: NSPaymentFragmentCallback? = null
		fun newInstance(bundle: Bundle?, paymentCallback: NSPaymentFragmentCallback?) = RozerFragment().apply {
			arguments = bundle
			callback = paymentCallback
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			viewModel.walletAmount = it.getString(NSConstants.KEY_WALLET_AMOUNT)
		}
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRozerBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
		setPaymentCallback()
        return rgBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(rgBinding) {
			HeaderUtils(
				layoutHeader,
				requireActivity(),
				clBackView = true,
				headerTitle = resources.getString(R.string.recharge)
			)
			tvWalletAmount.text = if (viewModel.walletAmount.isNullOrEmpty()) "0" else viewModel.walletAmount
		}
		observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(rgBinding) {
			with(viewModel) {

				var userDetail: NSDataUser? = null
				getUserDetail {
					userDetail = it
				}


				btnSubmit.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {

						note = etNote.text.toString()
						amount = etAmount.text.toString()

						if (amount.isNullOrEmpty()) {
							etAmount.error = "Please Enter Amount"
						} else if (amount?.toDouble()!! <= 0) {
							etAmount.error = "Please Enter Valid Amount"
						} else {
							val razorpayUtility = RazorpayUtility(requireActivity())
							val model = RozerModel()
							model.productName = note
							model.price = amount
							model.email = userDetail?.email
							model.mobile = userDetail?.mobile

							razorpayUtility.startPayment(model)
						}
					}
				})
			}
        }
    }

	private fun setPaymentCallback() {
		rgBinding.apply {
			viewModel.apply {
				callback?.onResponse(object : NSPaymentDetailCallback {
					override fun onResponse(
						paymentId: String,
						paymentData: String,
						isSuccess: Boolean
					) {
						if (isSuccess) {
							rechargeApi(activity, paymentId, paymentData, amount?:"0", note?:"")
						} else {
							etAmount.setText("")
							etNote.setText("")

							successResponse = if (paymentId.contains("description")) {
								val gson = Gson().fromJson(paymentId, NSErrorPaymentResponse::class.java)
								NSSuccessResponse(false, gson.error?.description)
							} else {
								NSSuccessResponse(false, paymentId)
							}


							//Error message
							openSuccessFail(activity)
						}
					}
				})
			}
		}
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(viewModel) {
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
