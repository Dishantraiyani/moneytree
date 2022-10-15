package com.moneytree.app.ui.recharge.history

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSMemberActiveSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSRechargeRepeatCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.NsFragmentRechargeHistoryBinding
import com.moneytree.app.repository.network.responses.NSRegisterListData
import com.moneytree.app.repository.network.responses.RechargeListDataItem
import com.moneytree.app.ui.memberActivation.NSMemberActivationFormActivity
import com.moneytree.app.ui.recharge.NSRechargeActivity
import com.moneytree.app.ui.recharge.mobile.NSMobileRechargeFragment
import com.moneytree.app.ui.register.NSAddRegisterFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSRechargeHistoryFragment : NSFragment() {
    private val rechargeListModel: NSRechargeHistoryViewModel by lazy {
		ViewModelProvider(this)[NSRechargeHistoryViewModel::class.java]
    }
    private var _binding: NsFragmentRechargeHistoryBinding? = null

    private val rechargeBinding get() = _binding!!
    private var rechargeListAdapter: NSRechargeListRecycleAdapter? = null
	var rechargeSelectedType: String? = ""

	companion object {
		fun newInstance(bundle: Bundle?) = NSRechargeHistoryFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(rechargeListModel) {
				rechargeType = it.getString(NSConstants.KEY_RECHARGE_TYPE)
			}
		}
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRechargeHistoryBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return rechargeBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(rechargeBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.recharge_history)
                ivBack.visibility = View.VISIBLE
                ivSearch.visibility = View.VISIBLE
            }
            setRegisterAdapter()
			setServiceProvider(true)
			setStatusFilter()
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(rechargeBinding) {
            with(rechargeListModel) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getRechargeListData(pageIndex, "", false, isBottomProgress = false)
                }

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

                    ivSearch.setOnClickListener {
                        cardSearch.visibility = View.VISIBLE
                    }

                    ivAddNew.setOnClickListener {
						EventBus.getDefault()
							.post(NSFragmentChange(NSAddRegisterFragment.newInstance()))
                    }

                    ivClose.setOnClickListener {
                        cardSearch.visibility = View.GONE
                        etSearch.setText("")
                        hideKeyboard(cardSearch)
                        pageIndex = "1"
                        if (tempRechargeList.isValidList()) {
                            rechargeList.clear()
                            rechargeList.addAll(tempRechargeList)
                            tempRechargeList.clear()
                            setRegisterData(rechargeList.isValidList())
                        }
                    }

                    etSearch.setOnKeyListener(object: View.OnKeyListener{
                        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent): Boolean {
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                        val strSearch = etSearch.text.toString()
                                        if (strSearch.isNotEmpty()) {
                                            hideKeyboard(cardSearch)
                                            tempRechargeList.addAll(rechargeList)
                                            getRechargeListData(pageIndex, strSearch, true,
                                                isBottomProgress = false
                                            )
                                        }
                                        return true
                                    }
                                }
                            }
                            return false
                        }
                    })
                }
            }
        }
    }

	private fun setServiceProvider(isProviderAvailable: Boolean) {
		with(rechargeBinding) {
			with(rechargeListModel) {
				val rechargeListType: MutableList<String> = arrayListOf()
				rechargeListType.addAll(resources.getStringArray(R.array.recharge_list_history))
				val adapter = ArrayAdapter(activity, R.layout.layout_spinner, rechargeListType)
				rechargeTypeSpinner.adapter = adapter
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				rechargeTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(
						p0: AdapterView<*>?, view: View?, position: Int, id: Long
					) {
						pageIndex = "1"
						rechargeType = if (rechargeListType[position] == "All") "" else rechargeListType[position]
						getRechargeListData(pageIndex, "", true, isBottomProgress = false)
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				}

				rechargeListType.forEachIndexed { index, strData ->
					if (strData.lowercase() == rechargeType?.lowercase()) {
						rechargeTypeSpinner.setSelection(index)
					}
				}
			}
		}
	}

	private fun setStatusFilter() {
		with(rechargeBinding) {
			with(rechargeListModel) {
				val statusListType: MutableList<String> = arrayListOf()
				statusListType.addAll(resources.getStringArray(R.array.status_list))
				val adapter = ArrayAdapter(activity, R.layout.layout_spinner, statusListType)
				statusTypeSpinner.adapter = adapter
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				statusTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
					override fun onItemSelected(
						p0: AdapterView<*>?, view: View?, position: Int, id: Long
					) {
						pageIndex = "1"
						statusType = if (statusListType[position] == "All") "" else statusListType[position]
						getRechargeListData(pageIndex, "", true, isBottomProgress = false)
					}

					override fun onNothingSelected(p0: AdapterView<*>?) {
					}
				}
			}
		}
	}

    /**
     * To add data of register in list
     */
    private fun setRegisterAdapter() {
        with(rechargeBinding) {
            with(rechargeListModel) {
                rvRechargeList.layoutManager = LinearLayoutManager(activity)
				rechargeListAdapter = NSRechargeListRecycleAdapter(activity, false, object:
					NSRechargeRepeatCallback {
					override fun onClick(rechargeData: RechargeListDataItem) {
						switchActivity(
							NSRechargeActivity::class.java,
							bundle = bundleOf(NSConstants.KEY_RECHARGE_TYPE to rechargeData.rechargeType, NSConstants.KEY_RECHARGE_DETAIL to Gson().toJson(rechargeData)), flags = intArrayOf(
								Intent.FLAG_ACTIVITY_CLEAR_TOP)
						)
					}
				}, object : NSPageChangeCallback {
					override fun onPageChange() {
						if (rechargeResponse!!.nextPage) {
							val page: Int = rechargeList.size/NSConstants.PAGINATION + 1
							pageIndex = page.toString()
							getRechargeListData(pageIndex, "", false, isBottomProgress = true)
						}
					}
				})

                rvRechargeList.adapter = rechargeListAdapter
                pageIndex = "1"
                getRechargeListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(rechargeBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set register data
     *
     * @param isRegister when data available it's true
     */
    private fun setRegisterData(isRegister: Boolean) {
        with(rechargeListModel) {
            registerDataManage(isRegister)
            if (isRegister) {
                rechargeListAdapter!!.clearData()
                rechargeListAdapter!!.updateData(rechargeList)
            }
        }
    }

    /**
     * Register data manage
     *
     * @param isRegisterVisible when register available it's visible
     */
    private fun registerDataManage(isRegisterVisible: Boolean) {
        with(rechargeBinding) {
            rvRechargeList.visibility = if (isRegisterVisible) View.VISIBLE else View.GONE
            clRechargeListNotFound.visibility = if (isRegisterVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(rechargeListModel) {
            with(rechargeBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isBottomProgressShowing.observe(
                    viewLifecycleOwner
                ) { isBottomProgressShowing ->
                    bottomProgress(isBottomProgressShowing)
                }

                isRechargeDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setRegisterData(isNotification)
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
		if (event.resultCode == NSRequestCodes.REQUEST_MEMBER_ACTIVATION_FORM) {
			with(rechargeListModel) {
				pageIndex = "1"
				getRechargeListData(pageIndex, "", true, isBottomProgress = false)
			}
		}
	}
}
