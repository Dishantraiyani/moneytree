package com.moneytree.app.ui.recharge.detail

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.moneytree.app.common.utils.buildAlertDialog
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutAdditionalFieldsViewBinding
import com.moneytree.app.databinding.LayoutWaitBinding
import com.moneytree.app.databinding.NsFragmentRechargeDetailBinding
import com.moneytree.app.repository.network.responses.AdditionalOptions
import com.moneytree.app.ui.recharge.NSRechargeViewModel
import kotlin.collections.forEach


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
					buildAlertDialog(requireContext(), LayoutWaitBinding::inflate) { dialog, binding ->
						viewModel.saveRecharge(requireContext()) {
							Handler(Looper.getMainLooper()).postDelayed({
								dialog.dismiss()
								viewModel.openSuccessFail(activity)
							}, 20000)
						}
					}
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
				addAdditionalFields(rechargeRequest?.additionalFields?: hashMapOf())
				
				with(rechargeRequest!!) {
					if (note?.isNotEmpty() == true) {
						llNotes.visible()
					}
				}
			}
		}
	}
	
	private fun addAdditionalFields(fields: HashMap<String, String>) {
		rgBinding.llAdditionalFields.removeAllViews()
		viewModel.additionalFields.clear()
		fields.forEach { field ->
			val fieldBinding = LayoutAdditionalFieldsViewBinding.inflate(layoutInflater, rgBinding.llAdditionalFields, false)
			val isLabelAvailable = field.key.isNotEmpty()
			fieldBinding.llAd1.setVisibility(isLabelAvailable)
			
			if (field.value.startsWith("{")) {
				val gson = Gson().fromJson(field.value, AdditionalOptions::class.java)
				fieldBinding.tvAd1.text = gson.label
				viewModel.additionalFields[field.key] = gson.value?:""
			} else {
				fieldBinding.tvAd1.text = field.value
				viewModel.additionalFields[field.key] = field.value
			}
			
			rgBinding.llAdditionalFields.addView(fieldBinding.root)
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
