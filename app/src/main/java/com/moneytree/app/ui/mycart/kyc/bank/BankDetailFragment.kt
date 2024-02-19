package com.moneytree.app.ui.mycart.kyc.bank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.gone
import com.moneytree.app.databinding.FragmentBankDetailBinding
import com.moneytree.app.ui.mycart.kyc.common.KycCommonViewModel


class BankDetailFragment : BaseViewModelFragment<KycCommonViewModel, FragmentBankDetailBinding>() {

    companion object {
        fun newInstance(bundle: Bundle?) = BankDetailFragment().apply {
            arguments = bundle
        }
    }

    override val viewModel: KycCommonViewModel by lazy {
        ViewModelProvider(this)[KycCommonViewModel::class.java]
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBankDetailBinding {
        return FragmentBankDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {

                btnSubmit.setOnClickListener(object : SingleClickListener() {
                    override fun performClick(v: View?) {
                        val bankName = binding.etBankName.text.toString()
                        val ifsc = binding.etIfsc.text.toString()
                        val accountNo = binding.etAccountNo.text.toString()

                        if (bankName.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.enter_bank_name))
                            return
                        } else if (accountNo.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.enter_bank_account_no))
                            return
                        } else if (ifsc.isEmpty()) {
                            showAlertDialog(activity.resources.getString(R.string.enter_bank_ifsc))
                            return
                        } else {
                            val map: HashMap<String, String> = hashMapOf()
                            map["bank_name"] = bankName
                            map["ac_no"] = accountNo
                            map["ifsc_code"] = ifsc

                            updateProfile(true, map) {
                                binding.btnSubmit.gone()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(binding) {
            with(viewModel) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isChangeDataAvailable.observe(viewLifecycleOwner) { isChange ->
                    if (isChange) {
                        btnSubmit.isEnabled = true
                        onBackPress()
                    }
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    btnSubmit.isEnabled = true
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    btnSubmit.isEnabled = true
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
                    btnSubmit.isEnabled = true
                    showNoNetworkAlertDialog(
                        getString(R.string.no_network_available), getString(
                            R.string.network_unreachable
                        )
                    )
                }

                validationErrorId.observe(viewLifecycleOwner) { errorId ->
                    btnSubmit.isEnabled = true
                    showAlertDialog(getString(errorId))
                }
            }
        }
    }
}