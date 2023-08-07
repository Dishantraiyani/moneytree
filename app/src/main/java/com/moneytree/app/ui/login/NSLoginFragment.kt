package com.moneytree.app.ui.login

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
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

	private val loginBinding get() = _binding!!
	private var loginPref: NSLoginPreferences? = null


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
		viewCreated()
		setListener()
		return loginBinding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		observeViewModel()
		with(loginBinding) {
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

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(loginBinding) {
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

				tvSignup.setOnClickListener(object : OnSingleClickListener() {
					override fun onSingleClick(v: View?) {
						switchActivity(
							SignUpActivity::class.java
						)
					}
				})
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
		with(loginBinding) {
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
		switchActivity(
			NSMainActivity::class.java,
			bundleOf(
				NSConstants.KEY_LOGIN_DATA to Gson().toJson(data)
			),
			flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
		)
	}
}
