package com.moneytree.app.ui.meeting

import android.app.Activity.RESULT_OK
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSDialogClickCallback
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.FragmentMeetingBinding
import com.moneytree.app.repository.network.responses.MeetingsDataResponse
import com.moneytree.app.ui.meeting.edit.MeetingAddEditActivity

class MeetingFragment : BaseViewModelFragment<MeetingViewModel, FragmentMeetingBinding>() {

    override val viewModel: MeetingViewModel by lazy {
        ViewModelProvider(this)[MeetingViewModel::class.java]
    }

    private var adapter: MeetingRecycleAdapter? = null

    private val meetingResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                getMeetings(true)
            }
        }

	companion object {
		fun newInstance() = MeetingFragment()
	}

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMeetingBinding {
        return FragmentMeetingBinding.inflate(inflater, container, false)
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
        with(binding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.meetings), isAddNew = true)
            getMeetings(true)
        }
        observeViewModel()
    }

    private fun setListener() {
        binding.apply {
            viewModel.apply {
                layoutHeader.ivAddNew.setSafeOnClickListener {
                    switchResultActivity(meetingResult,
                        MeetingAddEditActivity::class.java,
                        bundleOf(NSConstants.KEY_IS_ADD_MEETING to true)
                    )
                }

                srlRefresh.setOnRefreshListener {
                    getMeetings(false)
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

    private fun getMeetings(isShowProgress: Boolean) {
        binding.apply {
            viewModel.apply {
                getMeetingList(isShowProgress) {
                    setAdapter(it)
                }
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setAdapter(list: MutableList<MeetingsDataResponse>) {
        with(binding) {
            with(viewModel) {
                srlRefresh.isRefreshing = false
                rvCommon.layoutManager = LinearLayoutManager(activity)
                if (adapter == null) {
                    adapter = MeetingRecycleAdapter { model, isEdit, isDelete, position ->
                        if (isDelete) {
                            activity.resources.apply {
                                showCommonDialog(
                                    getString(R.string.app_name),
                                    getString(R.string.are_you_sure_delete),
                                    getString(R.string.yes_title),
                                    getString(R.string.no_title),
                                    callback = object : NSDialogClickCallback {
                                        override fun onClick(isOk: Boolean) {
                                            if (isOk) {
                                                deleteMeetings(model.eventId) {
                                                    if (it.status) {
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
                            if (isEdit) {
                                switchResultActivity(
                                    meetingResult,
                                    MeetingAddEditActivity::class.java,
                                    bundleOf(
                                        NSConstants.KEY_IS_ADD_MEETING to false,
                                        NSConstants.KEY_IS_SELECTED_MEETING to Gson().toJson(model)
                                    )
                                )
                            } else {
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                    rvCommon.adapter = adapter
                }

                adapter?.setData(list)

            }
        }
    }
}
