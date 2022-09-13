package com.moneytree.app.ui.register

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.BackPressEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSFragmentChange
import com.moneytree.app.common.callbacks.NSMemberActiveSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.NsFragmentRegisterBinding
import com.moneytree.app.repository.NSRegisterRepository.getRegisterListData
import com.moneytree.app.repository.network.responses.NSRegisterListData
import com.moneytree.app.ui.activationForm.NSActivationFormActivity
import org.greenrobot.eventbus.EventBus

class NSRegisterFragment : NSFragment() {
    private val registerListModel: NSRegisterViewModel by lazy {
        ViewModelProvider(this).get(NSRegisterViewModel::class.java)
    }
    private var _binding: NsFragmentRegisterBinding? = null

    private val registerBinding get() = _binding!!
    private var registerListAdapter: NSRegisterListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSRegisterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRegisterBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return registerBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(registerBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.register)
                ivBack.visibility = View.VISIBLE
                ivSearch.visibility = View.VISIBLE
				if (pref.isActive) {
					ivAddNew.visibility = View.VISIBLE
				}
            }
            setRegisterAdapter()
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(registerBinding) {
            with(registerListModel) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getRegisterListData(pageIndex, "", false, isBottomProgress = false)
                }

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        EventBus.getDefault().post(BackPressEvent())
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
                        if (tempRegisterList.isValidList()) {
                            registerList.clear()
                            registerList.addAll(tempRegisterList)
                            tempRegisterList.clear()
                            setRegisterData(registerList.isValidList())
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
                                            tempRegisterList.addAll(registerList)
                                            getRegisterListData(pageIndex, strSearch, true,
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

    /**
     * To add data of register in list
     */
    private fun setRegisterAdapter() {
        with(registerBinding) {
            with(registerListModel) {
                rvRegisterList.layoutManager = LinearLayoutManager(activity)
				registerListAdapter = NSRegisterListRecycleAdapter(activity, object: NSMemberActiveSelectCallback {
					override fun onClick(data: NSRegisterListData) {
						dataMember = data
						getActivationPackage(true)
					}
				}, object : NSPageChangeCallback {
					override fun onPageChange() {
						if (registerResponse!!.nextPage) {
							val page: Int = registerList.size/NSConstants.PAGINATION + 1
							pageIndex = page.toString()
							getRegisterListData(pageIndex, "", true, isBottomProgress = true)
						}
					}
				})

                rvRegisterList.adapter = registerListAdapter
                pageIndex = "1"
                getRegisterListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(registerBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set register data
     *
     * @param isRegister when data available it's true
     */
    private fun setRegisterData(isRegister: Boolean) {
        with(registerListModel) {
            registerDataManage(isRegister)
            if (isRegister) {
                registerListAdapter!!.clearData()
                registerListAdapter!!.updateData(registerList)
            }
        }
    }

    /**
     * Register data manage
     *
     * @param isRegisterVisible when register available it's visible
     */
    private fun registerDataManage(isRegisterVisible: Boolean) {
        with(registerBinding) {
            rvRegisterList.visibility = if (isRegisterVisible) View.VISIBLE else View.GONE
            clRegisterNotFound.visibility = if (isRegisterVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(registerListModel) {
            with(registerBinding) {
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
									),
									NSConstants.KEY_MEMBER_FORM_ACTIVATION_FORM_DETAIL to if (dataMember != null) Gson().toJson(dataMember) else "",
									NSConstants.KEY_MEMBER_FORM_ACTIVATION_FORM to true
								)
							)
						}
						isActivationPackageDataAvailable.value = false
					}
				}

                isRegisterDataAvailable.observe(
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

}
