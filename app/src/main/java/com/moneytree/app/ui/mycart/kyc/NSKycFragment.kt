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
import com.google.gson.Gson
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
import com.moneytree.app.common.utils.buildAlertDialog
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.common.utils.setPlaceholderAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setupWithAdapterAndCustomLayoutManager
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutCustomAlertDialogBinding
import com.moneytree.app.databinding.NsFragmentDoctorDetailBinding
import com.moneytree.app.databinding.NsFragmentKycDetailBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.repository.network.responses.ExtractedData
import com.moneytree.app.repository.network.responses.KycListResponse
import com.moneytree.app.ui.doctor.detail.NSDoctorImagesRecycleAdapter
import com.moneytree.app.ui.doctor.history.NSDoctorHistoryActivity
import com.moneytree.app.ui.main.NSMainActivity
import com.moneytree.app.ui.verified.NSKycVerifiedActivity
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
		binding.tvHeaderBack.text = activity.resources.getString(R.string.kyc_verification)
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
		}
	}

	private fun setAdapter(map: HashMap<String, Any>) {
		val model: ExtractedData = Gson().fromJson(Gson().toJson(map), ExtractedData::class.java)
		val list: MutableList<KycListResponse> = arrayListOf()
		list.add(KycListResponse("Aadhar Id", model.aadharId))
		list.add(KycListResponse("Name", model.name))
		list.add(KycListResponse("Dob", model.dob))
		list.add(KycListResponse("Gender", model.gender))
		list.add(KycListResponse("Address", model.address))

		/*for ((key, value) in map) {
			if (value is String) {
				list.add(KycListResponse(key.replace("_", " "), value))
			}
		}*/

		binding.rvImages.layoutManager = LinearLayoutManager(activity)
		val adapter = NSKycRecycleAdapter()
		adapter.setData(list)
		binding.rvImages.adapter = adapter
		if (list.isValidList()) {
			binding.ivView.visible()
			binding.tvInformation.visible()
			binding.btnNext.visible()
			binding.cardImg.visible()
		} else {
			Toast.makeText(activity, "Please Upload Valid Document", Toast.LENGTH_SHORT).show()
		}

		binding.btnNext.setSafeOnClickListener {
			showAlertDialog(activity.resources.getString(R.string.kyc_verification), "To confirm, please verify that the following details belong to you and are accurate.", map = map, isDirectApi = true)
		}
	}

	private fun showAlertDialog(title: String, message: String, isSuccess: Boolean = false, map: HashMap<String, Any>, isDirectApi: Boolean) {
		buildAlertDialog(activity, LayoutCustomAlertDialogBinding::inflate) { dialog, binding ->
			binding.apply {
				tvTitle.visible()
				tvTitle.text = title
				tvSubTitle.text = message
				tvOk.text = activity.resources.getString(R.string.ok)
				tvCancel.text = activity.resources.getString(R.string.cancel)
				if (isDirectApi) {
					tvCancel.visible()
					viewLine2.visible()
				}
//9e24f8c9a0316693a125b6b215d3ab16 
				tvOk.setOnClickListener {
					dialog.dismiss()
					if (isDirectApi) {
						viewModel.sendKycRequest(Gson().toJson(map), imageList) { str, isSuccess ->
							showAlertDialog(activity.resources.getString(R.string.app_name), str, isSuccess, map, false)
						}
					} else if (isSuccess) {
						activity.startActivity(Intent(activity, NSMainActivity::class.java))
						activity.finish()
					}
				}

				tvCancel.setOnClickListener {
					dialog.dismiss()
				}
			}
		}
	}

	private fun imagePicker() {
		imagePickerLauncher.launch(
			ImagePicker.with(requireActivity())
				.galleryOnly()
				.crop(16f, 9f)
				.createIntent())
	}
}
