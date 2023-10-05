package com.moneytree.app.ui.mycart.kyc

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker1.ImagePicker
import com.github.drjacky.imagepicker1.constant.ImageProvider
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSAlertButtonClickEvent
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.SelectImageFiles
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.common.utils.setPlaceholderAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setupWithAdapterAndCustomLayoutManager
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentDoctorDetailBinding
import com.moneytree.app.databinding.NsFragmentKycDetailBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.KycListResponse
import com.moneytree.app.ui.doctor.detail.NSDoctorImagesRecycleAdapter
import com.moneytree.app.ui.doctor.history.NSDoctorHistoryActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


class NSKycFragment : BaseViewModelFragment<NSKycViewModel, NsFragmentKycDetailBinding>() {

	private var imageList: MutableList<String> = arrayListOf()

	private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			val uri: Uri? = result.data?.data
			// Handle the selected image URI
			uri?.let {
				// Display the selected image using the obtained Uri
				//imageView.setImageURI(uri)
				//imageFile = File(uri.path!!)
				imageList.add(uri.path!!)
				if (imageList.isValidList()) {
					Glide.with(requireActivity()).load(imageList[0]).into(binding.ivKycImg)
				}
			}
		}
	}

	override val viewModel: NSKycViewModel by lazy {
		ViewModelProvider(this)[NSKycViewModel::class.java]
	}

	companion object {
		fun newInstance() = NSKycFragment()
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): NsFragmentKycDetailBinding {
		return NsFragmentKycDetailBinding.inflate(inflater, container, false)
	}

	override fun setupViews() {
		super.setupViews()
		baseObserveViewModel(viewModel)
		HeaderUtils(binding.layoutHeader, requireActivity(), headerTitle = activity.resources.getString(
			R.string.kyc_verification), clBackView = true)
		setListener()
	}

	private fun setListener() {
		binding.apply {

			ivKycImg.setSafeOnClickListener {
				imagePicker()
			}

			btnSend.setSafeOnClickListener {
				if (imageList.isValidList()) {
					viewModel.kycVerification(true, imageList) {
						setAdapter(it.extractedData)
					}
				} else {
					Toast.makeText(activity, "Please Upload Image", Toast.LENGTH_SHORT).show()
				}
			}

			btnNext.setSafeOnClickListener {
				requireActivity().setResult(RESULT_OK)
				finish()
			}
		}
	}

	private fun setAdapter(map: HashMap<String, Any>) {
		val list: MutableList<KycListResponse> = arrayListOf()
		for ((key, value) in map) {
			if (value is String) {
				list.add(KycListResponse(key.replace("_", " "), value))
			}
		}

		binding.rvImages.layoutManager = LinearLayoutManager(activity)
		val adapter = NSKycRecycleAdapter()
		adapter.setData(list)
		binding.rvImages.adapter = adapter
		binding.btnNext.visible()
		binding.cardImg.visible()
		if (list.isValidList()) {
			binding.tvInformation.visible()
		}
	}

	private fun imagePicker() {
		imagePickerLauncher.launch(
			ImagePicker.with(requireActivity())
				.galleryOnly()
				.crop()
				.cropFreeStyle()
				.createIntent())
	}
}
