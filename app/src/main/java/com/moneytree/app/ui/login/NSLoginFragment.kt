package com.moneytree.app.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentLoginBinding
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.ui.lock.LockActivity
import com.moneytree.app.ui.main.NSMainActivity
import com.moneytree.app.ui.signup.SignUpActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSLoginFragment : NSFragment() {
	private val loginViewModel: NSLoginViewModel by lazy {
		ViewModelProvider(this)[NSLoginViewModel::class.java]
	}
	private var _binding: NsFragmentLoginBinding? = null

	private val binding get() = _binding!!
	private var loginPref: NSLoginPreferences? = null
	private var isPasswordVisible = false

	companion object {
		fun newInstance() = NSLoginFragment()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = NsFragmentLoginBinding.inflate(inflater, container, false)
		loginPref = NSLoginPreferences(activity)
		setupInputFocus()
		setupPasswordToggle()
		setupSignupText()
		viewCreated()
		setListener()
		return binding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		observeViewModel()
		with(binding) {
			with(loginViewModel) {
				getNotificationToken()
				if (!loginPref!!.prefUserName.isNullOrEmpty()) {
					strUserName = loginPref!!.prefUserName
					strPassword = loginPref!!.prefPassword
					etUserName.setText(strUserName)
					etPassword.setText(strPassword)
					etPassword.transformationMethod = PasswordTransformationMethod()
				}
			}
		}
	}
	
	private fun setupInputFocus() {
		binding.etUserName.setOnFocusChangeListener { _, hasFocus ->
			binding.userNameContainer.setBackgroundResource(
				if (hasFocus) R.drawable.bg_input_active else R.drawable.bg_input_normal
			)
		}
		
		binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
			binding.passwordContainer.setBackgroundResource(
				if (hasFocus) R.drawable.bg_input_active else R.drawable.bg_input_normal
			)
		}
		
		binding.etUserName.requestFocus()
	}
	
	private fun setupPasswordToggle() {
		binding.btnTogglePassword.setOnClickListener {
			isPasswordVisible = !isPasswordVisible
			
			binding.etPassword.transformationMethod = if (isPasswordVisible) {
				HideReturnsTransformationMethod.getInstance()
			} else {
				PasswordTransformationMethod.getInstance()
			}
			
			binding.btnTogglePassword.setImageResource(
				if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
			)
			binding.btnTogglePassword.contentDescription = getString(
				if (isPasswordVisible) R.string.hide_password else R.string.show_password
			)
			binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
		}
	}
	
	private fun setupSignupText() {
		val fullText = getString(R.string.signup_text)
		val signUpStart = fullText.indexOf("SignUp")
		val spannable = SpannableString(fullText)
		
		if (signUpStart >= 0) {
			spannable.setSpan(object : ClickableSpan() {
				override fun onClick(widget: View) {
					switchActivity(
						SignUpActivity::class.java
					)
				}
				
				override fun updateDrawState(ds: TextPaint) {
					super.updateDrawState(ds)
					ds.color = ContextCompat.getColor(requireContext(), R.color.green_mid)
					ds.isUnderlineText = false
					ds.isFakeBoldText = true
				}
			}, signUpStart, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		}
		
		binding.tvSignup.text = spannable
		binding.tvSignup.highlightColor = Color.TRANSPARENT
		binding.tvSignup.movementMethod = LinkMovementMethod.getInstance()
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(binding) {
			with(loginViewModel) {
				btnLogin.setOnClickListener (
					object : OnSingleClickListener() {
						override fun onSingleClick(v: View?) {
							strUserName = etUserName.text.toString()
							strPassword = etPassword.text.toString()
							login()
						}
					})
				}

				/*tvSignup.setOnClickListener(object : OnSingleClickListener() {
					override fun onSingleClick(v: View?) {
						switchActivity(
							SignUpActivity::class.java
						)
					}
				})*/
			}
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(loginViewModel) {
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

	@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
	fun onLoginRegisterEvent(loginEvent: NSLoginRegisterEvent) {
		NSConstants.IS_LOGIN_SUCCESS = true
		with(binding) {
			with(loginViewModel) {
				if (cbRememberPassword.isChecked) {
					if (strUserName!!.isNotEmpty() && strPassword!!.isNotEmpty()) {
						loginPref!!.prefUserName = strUserName
						loginPref!!.prefPassword = strPassword
					}
				}
			}
		}

		PFPinCodeViewModel().isPinCodeEncryptionKeyExist.observe(
			requireActivity(),
			object : Observer<PFResult<Boolean?>?> {
				override fun onChanged(result: PFResult<Boolean?>?) {
					if (result == null) {
						openMainScreen(loginEvent.data)
						return
					}
					if (result.error != null) {
						openMainScreen(loginEvent.data)
						return
					}
					result.result?.let { it ->
						if (it) {
							openMainScreen(loginEvent.data)
						} else {
							switchActivity(
								LockActivity::class.java, bundleOf(
								NSConstants.KEY_LOCK_SCREEN to 2))
							finish()
						}
					}
				}
			}
		)
	}

	private fun openMainScreen(data: NSDataUser?) {
		NSUtilities.checkUserVerified(activity) {
			switchActivity(
				NSMainActivity::class.java,
				bundleOf(
					NSConstants.KEY_LOGIN_DATA to Gson().toJson(data)
				),
				flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
			)
		}
	}
}
