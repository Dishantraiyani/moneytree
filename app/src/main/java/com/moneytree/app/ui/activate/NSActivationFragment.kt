package com.moneytree.app.ui.activate

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentActivationBinding
import com.moneytree.app.ui.activationForm.NSActivationFormActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSActivationFragment : NSFragment(), NSSearchCallback {
    private val activationModel: NSActivationViewModel by lazy {
        ViewModelProvider(this)[NSActivationViewModel::class.java]
    }
    private var _binding: NsFragmentActivationBinding? = null

    private val activationBinding get() = _binding!!
    private var activationListAdapter: NSActivationListRecycleAdapter? = null

	companion object {
		fun newInstance() = NSActivationFragment()
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentActivationBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return activationBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(activationBinding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.activation_detail), isSearch = true, searchCallback = this@NSActivationFragment)
            setActivationAdapter()
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(activationModel) {
            with(activationBinding) {

                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getActivationListData(pageIndex, "", false, isBottomProgress = false)
                }

                btnActivation.setOnClickListener(object : SingleClickListener() {
                    override fun performClick(v: View?) {
                        getActivationPackage(true)
                    }
                })

				with(layoutHeader) {
                    ivClose.setOnClickListener {
						cardSearch.visibility = View.GONE
						etSearch.setText("")
						hideKeyboard(cardSearch)
						with(activationModel) {
							pageIndex = "1"
							if (tempActivationList.isValidList()) {
								activationList.clear()
								activationList.addAll(tempActivationList)
								tempActivationList.clear()
								setActivationData(activationList.isValidList())
							}
						}
					}
				}
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setActivationAdapter() {
        with(activationBinding) {
            with(activationModel) {
                rvActivationList.layoutManager = LinearLayoutManager(activity)
                activationListAdapter =
                    NSActivationListRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (productResponse!!.nextPage) {
                                val page: Int = activationList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getActivationListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    })
                rvActivationList.adapter = activationListAdapter
                pageIndex = "1"
                getActivationListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(activationBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setActivationData(isVoucher: Boolean) {
        with(activationModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                activationListAdapter!!.clearData()
                activationListAdapter!!.updateData(activationList)
            }
        }
    }

    /**
     * Voucher data manage
     *
     * @param isVoucherVisible when voucher available it's visible
     */
    private fun voucherDataManage(isVoucherVisible: Boolean) {
        with(activationBinding) {
            rvActivationList.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
            clActivationNotFound.visibility = if (isVoucherVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(activationModel) {
            with(activationBinding) {
                isBottomProgressShowing.observe(
                    viewLifecycleOwner
                ) { isBottomProgressShowing ->
                    bottomProgress(isBottomProgressShowing)
                }

                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

				isActivationPackageDataAvailable.observe(
					viewLifecycleOwner
				) { isActivation ->
					srlRefresh.isRefreshing = false
					if (isActivation) {
						if (activationPackageList.isValidList()) {
							switchResultActivity(dataResult,
								NSActivationFormActivity::class.java,
								bundleOf(
									NSConstants.KEY_MEMBER_ACTIVATION_FORM to Gson().toJson(
										activationPackageResponse
									)
								)
							)
						}
						isActivationPackageDataAvailable.value = false
					}
				}

                isActivationDataAvailable.observe(
                    viewLifecycleOwner
                ) { isProduct ->
                    srlRefresh.isRefreshing = false
                    setActivationData(isProduct)
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    srlRefresh.isRefreshing = false
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    srlRefresh.isRefreshing = false
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
                    srlRefresh.isRefreshing = false
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
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_ACTIVATION_FORM) {
			with(activationModel) {
				pageIndex = "1"
				getActivationListData(pageIndex, "", true, isBottomProgress = false)
			}
		}
	}

    override fun onSearch(search: String) {
        with(activationModel) {
            tempActivationList.addAll(activationList)
            getActivationListData(
                pageIndex,
                search,
                true,
                isBottomProgress = false
            )
        }
    }
}
