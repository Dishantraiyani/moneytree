package com.moneytree.app.ui.mycart.address.selectAddress

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.NsFragmentSelectAddressBinding
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.ui.mycart.address.NSAddressActivity
import com.moneytree.app.ui.mycart.placeOrder.NSPlaceOrderActivity

class NSSelectAddressFragment : BaseViewModelFragment<NSSelectAddressViewModel, NsFragmentSelectAddressBinding>() {

    override val viewModel: NSSelectAddressViewModel by lazy {
        ViewModelProvider(this)[NSSelectAddressViewModel::class.java]
    }

    private var adapter: NSAddressListRecycleAdapter? = null

    private val cartAddressResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                getAddress(true)
            }
        }

	companion object {
		fun newInstance(bundle: Bundle?) = NSSelectAddressFragment().apply {
            arguments = bundle
        }
	}

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentSelectAddressBinding {
        return NsFragmentSelectAddressBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        viewModel.isFromOrder = arguments?.getBoolean(NSConstants.KEY_IS_FROM_ORDER)?:false
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(binding) {
            NSApplication.getInstance().addSelectedAddress(NSAddressCreateResponse())
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.address), isAddNew = true)
            getAddress(true)
        }
        observeViewModel()
    }

    private fun setListener() {
        binding.apply {
            viewModel.apply {
                layoutHeader.ivAddNew.setSafeOnClickListener {
                    switchResultActivity(cartAddressResult,
                        NSAddressActivity::class.java,
                        bundleOf(NSConstants.KEY_IS_FROM_ORDER to isFromOrder, NSConstants.KEY_IS_ADD_ADDRESS to true)
                    )
                }

                btnSubmit.setSafeOnClickListener {
                    if (NSApplication.getInstance().getSelectedAddress().addressId.isNotEmpty()) {
                        switchResultActivity(
                            dataResult,
                            NSPlaceOrderActivity::class.java,
                            bundleOf(NSConstants.KEY_IS_FROM_ORDER to viewModel.isFromOrder)
                        )
                    } else {
                        Toast.makeText(requireContext(), "Please Select Address", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
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

    private fun getAddress(isShowProgress: Boolean) {
        binding.apply {
            viewModel.apply {
                getAddressList(isShowProgress) {
                    setAdapter(it)
                }
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setAdapter(list: MutableList<NSAddressCreateResponse>) {
        with(binding) {
            with(viewModel) {
                rvCommon.layoutManager = LinearLayoutManager(activity)
                adapter = NSAddressListRecycleAdapter{ model, isEdit, isDelete, position ->
                    if (isDelete) {
                        activity.resources.apply {
                            showCommonDialog(getString(R.string.app_name), getString(R.string.are_you_sure_delete), getString(R.string.yes_title), getString(R.string.no_title), callback = object : NSDialogClickCallback {
                                override fun onClick(isOk: Boolean) {
                                    if (isOk) {
                                        deleteAddress(model.addressId) {
                                            if (it.status) {
                                                if (NSApplication.getInstance().getSelectedAddress().addressId == model.addressId) {
                                                    NSApplication.getInstance().addSelectedAddress(
                                                        NSAddressCreateResponse()
                                                    )
                                                }
                                                checkButton()
                                                adapter?.getData()?.removeAt(position)
                                                adapter?.notifyItemRemoved(position)
                                            } else {
                                                if (it.message?.isNotEmpty() == true) {
                                                    showError(it.message ?: "")
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                        }
                    } else {
                        NSApplication.getInstance().addSelectedAddress(model)
                        btnSubmit.visible()
                        if (isEdit) {
                            switchResultActivity(
                                cartAddressResult,
                                NSAddressActivity::class.java,
                                bundleOf(
                                    NSConstants.KEY_IS_FROM_ORDER to isFromOrder,
                                    NSConstants.KEY_IS_ADD_ADDRESS to false,
                                    NSConstants.KEY_IS_SELECTED_ADDRESS to Gson().toJson(model)
                                )
                            )
                        } else {
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
				rvCommon.adapter = adapter

                for (item in list) {
                    if (item.setAsDefault == "Y" && NSApplication.getInstance().getSelectedAddress().addressId.isEmpty()) {
                        NSApplication.getInstance().addSelectedAddress(item)
                        break
                    }
                }

                adapter?.setData(list)
                checkButton()

            }
        }
    }

    private fun checkButton() {
        binding.apply {
            if (NSApplication.getInstance().getSelectedAddress().addressId.isNotEmpty()) {
                binding.btnSubmit.visible()
            } else {
                binding.btnSubmit.gone()
            }
        }
    }
}
