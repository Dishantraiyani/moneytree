package com.moneytree.app.ui.doctor.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentDoctorBinding
import com.moneytree.app.databinding.NsFragmentDoctorDetailBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.ui.activationForm.NSActivationFormFragment


class NSDoctorDetailFragment : BaseViewModelFragment<NSDoctorDetailViewModel, NsFragmentDoctorDetailBinding>() {

	override val viewModel: NSDoctorDetailViewModel by lazy {
		ViewModelProvider(this)[NSDoctorDetailViewModel::class.java]
	}

	companion object {
		fun newInstance(bundle: Bundle?) = NSDoctorDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): NsFragmentDoctorDetailBinding {
		return NsFragmentDoctorDetailBinding.inflate(inflater, container, false)
	}

	override fun setupViews() {
		super.setupViews()
		baseObserveViewModel(viewModel)
		HeaderUtils(binding.layoutHeader, requireActivity(), headerTitle = activity.resources.getString(
			R.string.doctor_details), clBackView = true)
		setListener()
		viewModel.getDoctorDetail(arguments?.getString(NSConstants.DOCTOR_DETAIL)) {
			setDoctorDetail(it)
		}
	}

	private fun setDoctorDetail(model: DoctorDataItem) {
		binding.apply {
			model.apply {
				ivDoctorImg.setCircleImage(R.drawable.placeholder, image)
				tvDoctorName.text = doctorName
				tvMobile.text = mobile
				tvAddress.text = address
				tvEmail.text = email
				tvCharge.text = addText(activity, R.string.price_value, charges.toString())
				tvExperience.text = experience
				tvEducation.text = education
			}
		}
	}

	private fun setListener() {
		binding.apply {

		}
	}
}
