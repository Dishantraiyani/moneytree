package com.moneytree.app.ui.doctor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentDoctorBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem


class NSDoctorFragment : BaseViewModelFragment<NSDoctorViewModel, NsFragmentDoctorBinding>(),
	NSSearchCallback {

	private var dcAdapter: NSDoctorRecycleAdapter? = null
	private var pageList: MutableList<Int> = arrayListOf()

	override val viewModel: NSDoctorViewModel by lazy {
		ViewModelProvider(this)[NSDoctorViewModel::class.java]
	}

	companion object {
		fun newInstance() = NSDoctorFragment()
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): NsFragmentDoctorBinding {
		return NsFragmentDoctorBinding.inflate(inflater, container, false)
	}

	override fun setupViews() {
		super.setupViews()
		baseObserveViewModel(viewModel)
		HeaderUtils(binding.layoutHeader, requireActivity(), headerTitle = activity.resources.getString(
			R.string.doctors), clBackView = true, isSearch = true, searchCallback = this)
		setListener()
		getDoctorList(true)
	}

	private fun setListener() {
		binding.apply {
			srlRefresh.setOnRefreshListener {
				getDoctorList(false)
			}

			layoutHeader.ivClose.setOnClickListener {
				if (layoutHeader.etSearch.text.isNotEmpty()) {
					layoutHeader.etSearch.setText("")
				} else {
					layoutHeader.cardSearch.gone()
					dcAdapter?.setData(viewModel.tempDoctorList)
				}
			}
		}
	}

	private fun getDoctorList(isShowProgress: Boolean, page: Int = 1, search: String = binding.layoutHeader.etSearch.text.toString()) {
		viewModel.apply {
			getDoctorListApi(isShowProgress, page, search) { page, list ->
				binding.cvProgress.gone()
				binding.srlRefresh.isRefreshing = false
				if (list.isEmpty()) {
					pageList.remove(page)
				}
				setAdapter(page, list)
			}
		}
	}

	private fun setAdapter(page: Int, list: MutableList<DoctorDataItem>) {
		viewModel.apply {
			with(binding) {
				with(rvDoctorList) {
					if (dcAdapter == null) {
						dcAdapter = NSDoctorRecycleAdapter(activity, { model, isDelete ->

						}, {

							val pageIndex: Int = list.size/NSConstants.PAGINATION + 1
							if (!pageList.contains(pageIndex)) {
								binding.cvProgress.visible()
								getDoctorList(false,pageIndex)
							}
						})

						setupWithAdapter(dcAdapter!!)
						isNestedScrollingEnabled = false
					}
					if (page == 1) {
						dcAdapter?.setData(list)
					} else {
						dcAdapter?.addData(list)
					}
				}
			}
		}
	}

	override fun onSearch(search: String) {
		viewModel.apply {
			if (tempDoctorList.isEmpty()) {
				tempDoctorList.addAll(dcAdapter?.getData()?: arrayListOf())
			}
		}
		getDoctorList(true, search = search)
	}
}
