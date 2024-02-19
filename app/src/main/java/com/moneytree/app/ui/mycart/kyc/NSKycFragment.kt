package com.moneytree.app.ui.mycart.kyc

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker1.ImagePicker
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.utils.buildAlertDialog
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutCustomAlertDialogBinding
import com.moneytree.app.databinding.NsFragmentKycDetailBinding
import com.moneytree.app.repository.network.responses.KycListResponse
import com.moneytree.app.ui.main.NSMainActivity


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
				binding.ivView.gone()
				binding.tvInformation.gone()
				binding.btnNext.gone()
				binding.cardImg.gone()
				imageList.clear()
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
		fun newInstance(bundle: Bundle?) = NSKycFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			viewModel.kycType = it.getString(NSConstants.KEY_KYC_TYPE)?:"pancard"
		}
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

			ivBack.setOnClickListener {
				onBackPress()
			}

			ivKycImg.setSafeOnClickListener {
				imagePicker()
			}

			btnSend.setSafeOnClickListener {
				if (imageList.isValidList()) {
					viewModel.kycVerification(true, imageList) { data, isSuccess ->
						if (isSuccess) {
							setAdapter(data.extractedData)
						}
					}
				} else {
					Toast.makeText(activity, "Please Upload Image", Toast.LENGTH_SHORT).show()
				}
			}

			tvSkip.setSafeOnClickListener {
				pref.isKycVerifiedSkip = true
				activity.startActivity(Intent(activity, NSMainActivity::class.java))
				activity.finish()
			}
		}
	}

	private fun setAdapter(map: HashMap<String, Any>) {
		val keyOrder = listOf("pan_no","aadhar_id", "name", "father_name", "dob", "gender", "address")
		val list: MutableList<KycListResponse> = arrayListOf()

		/*val model: ExtractedData = Gson().fromJson(Gson().toJson(map), ExtractedData::class.java)

		list.add(KycListResponse("Your Id", model.aadharId))
		list.add(KycListResponse("Name", model.name))
		list.add(KycListResponse("Father Name", model.fatherName))
		list.add(KycListResponse("Dob", model.dob))
		list.add(KycListResponse("Gender", model.gender))
		list.add(KycListResponse("Address", model.address))*/

		//{"father_name":"VISHWNATH MISHRA","dob":"03/06/1980","name":"DINESH KUMAR MISHRA","pan_no":"AWJPM4059J"}
		for (key in keyOrder) {
			val value = map[key]
			if (value != null) {
				if (value is String) {
					list.add(KycListResponse(key.replace("_", " "), value))
				}
			}
		}

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
		ImagePicker.with(requireActivity())
			.crop(16f, 9f)
			.createIntentFromDialog { intent, provider ->
				imagePickerLauncher.launch(intent)
			}
		/*imagePickerLauncher.launch(
			ImagePicker.with(requireActivity())
				.cameraOnly()
				.crop(16f, 9f)
				.createIntent())*/
	}
}
