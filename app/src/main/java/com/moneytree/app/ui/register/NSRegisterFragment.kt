package com.moneytree.app.ui.register

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
import com.moneytree.app.common.BackPressEvent
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSActivityEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSFragmentChange
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSRegisterActiveSelectCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.NsFragmentRegisterBinding
import com.moneytree.app.repository.network.responses.NSRegisterListData
import com.moneytree.app.ui.memberActivation.NSMemberActivationFormActivity
import com.moneytree.app.ui.register.add.NSAddRegisterFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSRegisterFragment : NSFragment(), NSSearchCallback {
    private val registerListModel: NSRegisterViewModel by lazy {
        ViewModelProvider(this).get(NSRegisterViewModel::class.java)
    }
    private var _binding: NsFragmentRegisterBinding? = null
    val statusListType: MutableList<String> = arrayListOf()

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
        statusListType.addAll(resources.getStringArray(R.array.register_filter))
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
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.register), isSearch = true, isAddNew = pref.isActive, searchCallback = this@NSRegisterFragment)
                NSConstants.tabName = this@NSRegisterFragment.javaClass
            }
            setFilter()
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
                    getRegisterListData(pageIndex, "",
                        statusListType[filterSpinner.selectedItemPosition],false, isBottomProgress = false)
                }

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        EventBus.getDefault().post(BackPressEvent())
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


                }
            }
        }
    }

    private fun setFilter() {
        with(registerBinding) {
            with(registerListModel) {
                val adapter = ArrayAdapter(activity, R.layout.layout_spinner, statusListType)
                filterSpinner.adapter = adapter
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                var statusFilter: String = statusListType[0].lowercase()
                filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?, view: View?, position: Int, id: Long
                    ) {
                        if (statusFilter != statusListType[position]) {
                            statusFilter = statusListType[position]
                            pageIndex = "1"
                            getRegisterListData(pageIndex, "", statusListType[filterSpinner.selectedItemPosition],false, isBottomProgress = false)
                        }
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
        with(registerBinding) {
            with(registerListModel) {
                rvRegisterList.layoutManager = LinearLayoutManager(activity)
                registerListAdapter = NSRegisterListRecycleAdapter(activity, object:
                    NSRegisterActiveSelectCallback {
                    override fun onClick(data: NSRegisterListData) {
                        dataMember = data
                        getActivationPackage(data.username!!, true)
                    }

                    override fun onDefault(data: NSRegisterListData) {
                        if (data.userId != null) {
                            setDefault(data.userId!!, statusListType[filterSpinner.selectedItemPosition], true)
                        }
                    }

                    override fun onMessageSend(data: NSRegisterListData) {
                        if (data.userId != null) {
                            sendMessage(data.userId!!, true)
                        }
                    }
                }, object : NSPageChangeCallback {
                    override fun onPageChange(pageNo: Int) {
                        if (registerResponse!!.nextPage) {
                            val page: Int = registerList.size/NSConstants.PAGINATION + 1
                            pageIndex = page.toString()
                            getRegisterListData(pageIndex, "", statusListType[filterSpinner.selectedItemPosition], false, isBottomProgress = true)
                        }
                    }
                })

                rvRegisterList.adapter = registerListAdapter
                pageIndex = "1"
                getRegisterListData(pageIndex, "", statusListType[filterSpinner.selectedItemPosition], true, isBottomProgress = false)
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
                                NSMemberActivationFormActivity::class.java,
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultEvent(event: NSActivityEvent) {
        if (event.resultCode == NSRequestCodes.REQUEST_MEMBER_ACTIVATION_FORM) {
            with(registerListModel) {
                pageIndex = "1"
                getRegisterListData(pageIndex, "", statusListType[registerBinding.filterSpinner.selectedItemPosition], true, isBottomProgress = false)
            }
        }
    }

    override fun onSearch(search: String) {
        registerListModel.apply {
            tempRegisterList.addAll(registerList)
            getRegisterListData(pageIndex, search, statusListType[registerBinding.filterSpinner.selectedItemPosition], true,
                isBottomProgress = false
            )
        }
    }
}
