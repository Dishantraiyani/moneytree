package com.moneytree.app.ui.register.add

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.EmailValidator
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSAlertButtonClickEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.OnSingleClickListener
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentAddRegisterBinding
import com.moneytree.app.ui.register.NSRegisterViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSAddRegisterFragment : NSFragment() {
	private val registerListModel: NSRegisterViewModel by lazy {
		ViewModelProvider(this).get(NSRegisterViewModel::class.java)
	}
	private var _binding: NsFragmentAddRegisterBinding? = null
	private val registerAddBinding get() = _binding!!

	companion object {
		fun newInstance() = NSAddRegisterFragment()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentAddRegisterBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		return registerAddBinding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		with(registerAddBinding) {
			with(registerListModel) {
				HeaderUtils(
					layoutHeader,
					requireActivity(),
					clBackView = true,
					headerTitle = resources.getString(R.string.member_register)
				)
				addRegistrationType(activity)
				setTerms()
			}
		}
		observeViewModel()
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(registerAddBinding) {
			with(registerListModel) {
				etPassword.transformationMethod = PasswordTransformationMethod()
				etConfirmPassword.transformationMethod = PasswordTransformationMethod()
				btnSubmit.setOnClickListener(object : OnSingleClickListener() {
					override fun onSingleClick(v: View?) {
						with(activity.resources) {
							val fullName = etFullName.text.toString()
							val password = etPassword.text.toString()
							val cPassword = etConfirmPassword.text.toString()
							val email = etEmail.text.toString()
							val phone = etPhone.text.toString()

							if (fullName.isEmpty()) {
								etFullName.error = getString(R.string.please_enter_name)
								return
							} else if (phone.isEmpty() || phone.length < 10) {
								etPhone.error = getString(R.string.please_enter_valid_phone)
								return
							} else if (email.isEmpty()) {
								etEmail.error = getString(R.string.please_enter_email)
								return
							}else if (!EmailValidator.isValidEmail(email)) {
								etEmail.error = getString(R.string.please_enter_vaild_email)
								return
							} else if (password.isEmpty()) {
								etPassword.error = getString(R.string.please_enter_password)
								return
							} else if (cPassword.isEmpty()) {
								etConfirmPassword.error =
									getString(R.string.please_enter_password)
								return
							} else if (password != cPassword) {
								etConfirmPassword.error = getString(R.string.password_not_match)
								return
							} else if (!cbChecked.isChecked) {
								Toast.makeText(
									activity,
									activity.resources.getString(R.string.please_accept_terms),
									Toast.LENGTH_SHORT
								).show()
								return
							} else {
								saveRegisterData(fullName, email, phone, password, true)
							}
						}
					}
				})
			}
		}
	}

	private fun setTerms() {
		with(registerAddBinding) {
			val html = "I agree to the <a href=${NSUtilities.decrypt(BuildConfig.TERMS)}>Terms & Conditions</a> and <a href=${NSUtilities.decrypt(BuildConfig.PRIVACY)}>Privacy Policy</a>"
			tvTermsConditions.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
			tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()
		}
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(registerListModel) {
			with(registerAddBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				isRegisterSuccessAvailable.observe(viewLifecycleOwner) {
					if (it) {
						showSuccessDialog(getString(R.string.app_name), successResponse!!.message, NSConstants.MEMBER_REGISTER_SUCCESS)
					}
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
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.MEMBER_REGISTER_SUCCESS) {
			activity.setResult(RESULT_OK)
			onBackPress()
		}
	}

}
