package com.moneytree.app.ui.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSProductCategoryCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentDownloadPlansBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.ui.productCategory.MTCategoryListRecycleAdapter


class NSDownloadPlanFragment : NSFragment() {
    private val downloadModel: NSDownloadPlanModel by lazy {
		ViewModelProvider(this)[NSDownloadPlanModel::class.java]
    }
    private var _binding: NsFragmentDownloadPlansBinding? = null
    private val binding get() = _binding!!
	private var adapter: NSDownloadPlanRecycleAdapter? = null

	companion object {
		fun newInstance() = NSDownloadPlanFragment()
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentDownloadPlansBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return binding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(binding) {
            with(downloadModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.downloads)
                    ivBack.visibility = View.VISIBLE
                }

            }
        }
		setDownloadPlanAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(downloadModel) {

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

                }
            }
        }
    }

	/**
	 * To add data of vouchers in list
	 */
	private fun setDownloadPlanAdapter() {
		with(binding) {
			with(downloadModel) {
				rvDownloadList.layoutManager = LinearLayoutManager(activity)
				adapter = NSDownloadPlanRecycleAdapter(requireActivity())
				rvDownloadList.adapter = adapter
				setDownloadData()
			}
		}
	}

	private fun setDownloadData() {
		binding.apply {
			downloadModel.apply {
				setDownloadList()
				adapter?.updateData(downloadList)
			}
		}
	}

	/**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(downloadModel) {
            with(binding) {
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