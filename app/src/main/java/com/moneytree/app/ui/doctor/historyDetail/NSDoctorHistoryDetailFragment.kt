package com.moneytree.app.ui.doctor.historyDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.NsFragmentDoctorDetailHistoryBinding


class NSDoctorHistoryDetailFragment : BaseViewModelFragment<NSDoctorHistoryDetailViewModel, NsFragmentDoctorDetailHistoryBinding>() {

	override val viewModel: NSDoctorHistoryDetailViewModel by lazy {
		ViewModelProvider(this)[NSDoctorHistoryDetailViewModel::class.java]
	}

	companion object {
		fun newInstance(bundle: Bundle?) = NSDoctorHistoryDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): NsFragmentDoctorDetailHistoryBinding {
		return NsFragmentDoctorDetailHistoryBinding.inflate(inflater, container, false)
	}

	override fun setupViews() {
		super.setupViews()
		baseObserveViewModel(viewModel)
		HeaderUtils(binding.layoutHeader, requireActivity(), headerTitle = activity.resources.getString(
			R.string.doctors_appointments), clBackView = true)
		val detail = arguments?.getString(NSConstants.DOCTOR_DETAIL)
		viewModel.getDoctorDetail(detail)
		viewModel.setupViewPager(requireActivity(), binding.viewPager)
		setDoctorDetail()
	}

	private fun setDoctorDetail() {
		binding.apply {
			viewModel.apply {
				tvDoctorName.text = doctorDetail?.doctorName
				tvPatientName.text = doctorDetail?.name
				tvCharges.text =  addText(activity, R.string.price_value, doctorDetail?.charges.toString())
				tvGender.text =  doctorDetail?.gender
				val ages = "Age: ${doctorDetail?.age}"
				tvAge.text =  ages
				tvRemarkDetail.text =  doctorDetail?.remark
			}
		}
	}
}
