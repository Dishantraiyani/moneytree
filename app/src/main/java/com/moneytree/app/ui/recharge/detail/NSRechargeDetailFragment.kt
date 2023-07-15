package com.moneytree.app.ui.recharge.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentRechargeDetailBinding
import com.moneytree.app.ui.recharge.NSRechargeViewModel


class NSRechargeDetailFragment : NSFragment() {
	private val viewModel: NSRechargeViewModel by lazy {
		ViewModelProvider(this)[NSRechargeViewModel::class.java]
	}
    private var _binding: NsFragmentRechargeDetailBinding? = null
    private val rgBinding get() = _binding!!

	companion object {
		fun newInstance(bundle: Bundle?) = NSRechargeDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(viewModel) {
				rechargeDetail = it.getString(NSConstants.KEY_RECHARGE_VERIFY)
				setRechargeData()
			}
		}
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRechargeDetailBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
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
				headerTitle = resources.getString(R.string.detail_verify)
			)
			setValue()
			observeViewModel()
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(rgBinding) {
            btnSubmit.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					viewModel.saveRecharge(activity)
				}
			})
		}
    }

	private fun setValue() {
		with(viewModel) {
			with(rgBinding) {
				tvRechargeValue.text = rechargeRequest!!.rechargeType
				tvServiceProvider.text = rechargeRequest!!.serviceProviderTitle
				tvAccountDisplay.text = rechargeRequest!!.accountDisplayTitle
				tvAccountDisplayValue.text = rechargeRequest!!.accountDisplay
				tvAmount.text = rechargeRequest!!.amount
				tvNote.text = rechargeRequest!!.note
				tvAd1.text = rechargeRequest!!.ad1
				tvAd2.text = rechargeRequest!!.ad2
				tvAd3.text = rechargeRequest!!.ad3
				with(rechargeRequest!!) {
					if (ad1?.isNotEmpty() == true) {
						llAd1.visible()
					}
					if (ad2?.isNotEmpty() == true) {
						llAd2.visible()
					}
					if (ad3?.isNotEmpty() == true) {
						llAd3.visible()
					}
					if (note?.isNotEmpty() == true) {
						llNotes.visible()
					}
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
