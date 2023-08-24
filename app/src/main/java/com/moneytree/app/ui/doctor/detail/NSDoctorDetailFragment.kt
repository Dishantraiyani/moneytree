package com.moneytree.app.ui.doctor.detail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.moneytree.app.common.utils.setCircleImage
import com.moneytree.app.common.utils.setPlaceholderAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setupWithAdapterAndCustomLayoutManager
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentDoctorDetailBinding
import com.moneytree.app.repository.network.responses.DoctorDataItem
import com.moneytree.app.ui.doctor.history.NSDoctorHistoryActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


class NSDoctorDetailFragment : BaseViewModelFragment<NSDoctorDetailViewModel, NsFragmentDoctorDetailBinding>() {

	private var imageFile: File? = null
	private var imageAdapter: NSDoctorImagesRecycleAdapter? = null
	private var imageList: MutableList<String> = arrayListOf()
	private var selectedGender: String? = null
	private var selectedDoctorId: String? = null
	private var selectedCharges: String? = null

	private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == RESULT_OK) {
			val uri: Uri? = result.data?.data
			// Handle the selected image URI
			uri?.let {
				// Display the selected image using the obtained Uri
				//imageView.setImageURI(uri)
				//imageFile = File(uri.path!!)
				imageList.add(uri.path!!)
				imageAdapter?.setData(imageList)
			}
		}
	}

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
		setImageAdapter()
		viewModel.getDoctorDetail(arguments?.getString(NSConstants.DOCTOR_DETAIL)) {
			setDoctorDetail(it)
		}
	}

	private fun setDoctorDetail(model: DoctorDataItem) {
		binding.apply {
			model.apply {
				selectedCharges = charges
				selectedDoctorId = model.doctorId
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
			ageTypeSpinner.setPlaceholderAdapter(resources.getStringArray(R.array.age_filter), requireContext()) {
				selectedGender = it
			}

			var whom = "My Self"
			tvMySelf.setOnClickListener {
				whom = "My Self"
				setWhomSelected(0)
			}

			tvSomeoneElse.setOnClickListener {
				whom = "Some One Else"
				setWhomSelected(1)
			}

			edtDob.setSafeOnClickListener {
				NSUtilities.selectDateOfBirth(requireActivity()) {
					edtDob.text = it
				}
			}

			btnSend.setSafeOnClickListener {
				val name = edtName.text.toString()
				val mobile = edtNumber.text.toString()
				val dob = edtDob.text.toString()
				val remark = edtRemark.text.toString()
				val age = edtAge.text.toString()

				val resource = activity.resources
				if (name.isEmpty()) {
					edtName.error = resource.getString(R.string.please_enter_name)
					return@setSafeOnClickListener
				} else if (mobile.isEmpty()) {
					edtNumber.error = resource.getString(R.string.please_enter_mobile_no)
					return@setSafeOnClickListener
				} else if (mobile.length < 10) {
					edtNumber.error = resource.getString(R.string.please_enter_valid_mobile_no)
					return@setSafeOnClickListener
				} else if (dob.isEmpty()) {
					edtDob.error = resource.getString(R.string.please_enter_dob)
					return@setSafeOnClickListener
				} else if (age.isEmpty()) {
					edtAge.error = resource.getString(R.string.please_enter_age)
					return@setSafeOnClickListener
				} else if ((selectedCharges?:"0.0").toDouble() > NSApplication.getInstance().getWalletBalance().toDouble()) {
					viewModel.showError("You don't have enough balance to get this service kindly recharge your wallet.")
					return@setSafeOnClickListener
				}

				if (selectedDoctorId != null) {
					val map: HashMap<String, String> = hashMapOf()
					map[NSConstants.DOCTOR_ID] = selectedDoctorId!!
					map[NSConstants.PATIENT_NAME] = name
					map[NSConstants.PATIENT_NUMBER] = mobile
					map[NSConstants.PATIENT_DOB] = dob
					map[NSConstants.PATIENT_REMARK] = remark
					map[NSConstants.PATIENT_GENDER] = selectedGender!!
					map[NSConstants.PATIENT_AGE] = age

					viewModel.sendDoctorRequest(true, imageList, map) {
						showSuccessDialog(title = activity.resources.getString(R.string.app_name), message = it, alertKey = NSConstants.KEY_ALERT_DOCTOR_SEND)

						//imageAdapter?.setData(arrayListOf())
						//ivPatientImg.setImageResource(0)
					}
				}
			}
		}
	}

	private fun imageSelect() {
		SelectImageFiles.selectClassifiedImage(requireActivity() as AppCompatActivity, 10) { list ->
			Log.d("TAG", "onActivityResult: ")
			imageList.addAll(list)
			imageAdapter?.setData(imageList)
		}
	}

	private fun setImageAdapter() {
		imageAdapter = NSDoctorImagesRecycleAdapter({
			ImagePicker.with(requireActivity())
				.crop(720f, 1020f)
				.createIntentFromDialog { intent, provider ->
					if (provider == ImageProvider.CAMERA) {
						imagePickerLauncher.launch(intent)
					} else {
						imageSelect()
					}
				}
		}) {
			imageList.removeAt(it)
			imageAdapter?.setData(imageList)
		}
		imageList.clear()
		imageList.add("ADD_IMAGE")
		imageAdapter?.setData(imageList)
		binding.rvImages.setupWithAdapterAndCustomLayoutManager(imageAdapter!!, LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false))
	}

	private fun setWhomSelected(position: Int) {
		binding.apply {
			tvMySelf.setTextColor(if(position == 0) Color.WHITE else Color.BLACK)
			tvSomeoneElse.setTextColor(if(position == 0) Color.BLACK else Color.WHITE)
			tvMySelf.setBackgroundResource(if (position == 0) R.drawable.login_button_border else R.drawable.gray_border)
			tvSomeoneElse.setBackgroundResource(if (position == 0) R.drawable.gray_border else R.drawable.login_button_border)
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.KEY_ALERT_DOCTOR_SEND) {
			binding.apply {
				viewModel.apply {
					edtName.setText("")
					edtDob.text = ""
					edtRemark.setText("")
					edtNumber.setText("")
					edtAge.setText("")
					switchActivity(NSDoctorHistoryActivity::class.java)
					finish()
				}
			}
		}
	}

}
