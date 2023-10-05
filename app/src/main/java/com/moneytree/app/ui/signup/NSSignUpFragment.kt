package com.moneytree.app.ui.signup

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.security.PFSecurityManager
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSAlertButtonClickEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.OnSingleClickListener
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentSignupBinding
import com.moneytree.app.ui.lock.LockActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSSignUpFragment : NSFragment() {
	private val signUpModel: NSSignUpViewModel by lazy {
		ViewModelProvider(this)[NSSignUpViewModel::class.java]
	}
	private var _binding: NsFragmentSignupBinding? = null
	private val signUpBinding get() = _binding!!
	private var referrerClient: InstallReferrerClient? = null
	private var referCode: String? = ""

	companion object {
		fun newInstance() = NSSignUpFragment()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentSignupBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		return signUpBinding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		with(signUpBinding) {
			with(signUpModel) {
				getNotificationToken()
				HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.sign_up))
				setTerms()
			}
		}
		observeViewModel()
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(signUpBinding) {
			etPassword.transformationMethod = PasswordTransformationMethod()
			etConfirmPassword.transformationMethod = PasswordTransformationMethod()
			btnSubmit.setOnClickListener(object : OnSingleClickListener() {
				override fun onSingleClick(v: View?) {
					with(activity.resources) {
						registerUser(true)
					}
				}
			})
		}
	}

	private fun registerUser(isReferal: Boolean) {
		signUpBinding.apply {
			signUpModel.apply {
				val referral = etRefer.text.toString()
				val fullName = etFullName.text.toString()
				val password = etPassword.text.toString()
				val cPassword = etConfirmPassword.text.toString()
				val email = etEmail.text.toString()
				val phone = etPhone.text.toString()

				if (fullName.isEmpty()) {
					etFullName.error = getString(R.string.please_enter_name)
					return
				} else if (phone.isEmpty() || phone.length < 10 || !NSUtilities.isValidMobile(phone)) {
					etPhone.error = getString(R.string.please_enter_valid_phone)
					return
				} else if (email.isEmpty()) {
					etEmail.error = getString(R.string.please_enter_email)
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
				} else if (referral.isEmpty() && isReferal) {
					showCommonDialog("SignUp", "Are you sure you want to register without referral?", "Yes", "No", NSConstants.SIGNUP_CLICK)
					return
				} else {
					saveRegisterData(etRefer.text.toString(), fullName, email, phone, password, true)
				}
			}
		}
	}

	private fun setTerms() {
		with(signUpBinding) {
			val html = "I agree to the <a href=${NSUtilities.decrypt(BuildConfig.TERMS)}>Terms & Conditions</a> and <a href=${NSUtilities.decrypt(BuildConfig.PRIVACY)}>Privacy Policy</a>"
			tvTermsConditions.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
			tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()
		}
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(signUpModel) {
			with(signUpBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				isRegisterDataAvailable.observe(
					viewLifecycleOwner
				) { isNotification ->
					Log.d(TAG, "onTabSelectEvent: $isNotification")
				}

				isRegisterSuccessAvailable.observe(viewLifecycleOwner) {
					if (it) {
						PFPinCodeViewModel().isPinCodeEncryptionKeyExist.observe(
							requireActivity(),
							object : Observer<PFResult<Boolean?>?> {
								override fun onChanged(result: PFResult<Boolean?>?) {
									if (result == null) {
										onBackPress()
										return
									}
									if (result.error != null) {
										onBackPress()
										return
									}
									result.result?.let { it ->
										if (it) {
											onBackPress()
										} else {
											switchActivity(LockActivity::class.java, bundleOf(
												NSConstants.KEY_LOCK_SCREEN to 1))
											finish()
										}
									}
								}
							}
						)
						//onBackPress()
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

	private fun getReferrerCode() {
		referrerClient = InstallReferrerClient.newBuilder(activity).build()
		referrerClient?.startConnection(object : InstallReferrerStateListener {
			override fun onInstallReferrerSetupFinished(responseCode: Int) {
				if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
					Log.d("Install_referral", "Connection establish")
					try {
						val referrerDetails = referrerClient?.installReferrer
						if (!referrerDetails?.installReferrer?.contains("utm_source")!!) {
							referCode = referrerDetails.installReferrer
							signUpBinding.etRefer.setText(referCode)
						}
						referrerClient?.endConnection()
					} catch (e: RemoteException) {
						e.printStackTrace()
					}
				}
			}

			override fun onInstallReferrerServiceDisconnected() {
				Log.d("TAG", "onInstallReferrerServiceDisconnected: ")
			}
		})
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.SIGNUP_CLICK) {
			registerUser(false)
		}
	}
}
