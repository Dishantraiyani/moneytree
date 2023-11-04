package com.moneytree.app.ui.paymentSummary

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.kal.rackmonthpicker.RackMonthPicker
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.databinding.NsFragmentPaymentSummaryBinding
import com.rajat.pdfviewer.PdfViewerActivity
import java.util.Locale


class NSPaymentSummaryFragment : BaseViewModelFragment<NSPaymentSummaryViewModel, NsFragmentPaymentSummaryBinding>() {

    override val viewModel: NSPaymentSummaryViewModel by lazy {
        ViewModelProvider(this)[NSPaymentSummaryViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSPaymentSummaryFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentPaymentSummaryBinding {
        return NsFragmentPaymentSummaryBinding.inflate(inflater, container, false)
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
        with(binding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.payment_summary))
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {
                tvStartDate.setSafeOnClickListener {
                    showDatePicker()
                }

                btnSubmit.setSafeOnClickListener {
                    if (viewModel.selectedDateValue.isNotEmpty()) {
                        paymentSummary(requireContext(), viewModel.selectedDateValue) {
                            val fileName: String =
                                it.substring(it.lastIndexOf('/') + 1)
                            activity.startActivity(
                                PdfViewerActivity.launchPdfFromPath(
                                    activity,
                                    it,
                                    fileName,
                                    NSConstants.DIRECTORY_PATH,
                                    true
                                )
                            )
                            viewModel.hideProgress()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please Select Month & Year", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showDatePicker() {
        binding.apply {
            var picker: RackMonthPicker? = null
            picker = RackMonthPicker(requireActivity())
                .setLocale(Locale.ENGLISH)
                .setPositiveButton { month, startDate, endDate, year, monthLabel ->
                    val selectedDate = "${year}-$month"
                    tvStartDate.text = selectedDate
                    viewModel.selectedDateValue = selectedDate
                }
                .setNegativeButton {
                    picker?.dismiss()
                }
            picker.show()
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
}
