package com.moneytree.app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentLoginBinding
import com.moneytree.app.ui.main.NSMainActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSLoginFragment : NSFragment() {
	private val loginViewModel: NSLoginViewModel by lazy {
		ViewModelProvider(this).get(NSLoginViewModel::class.java)
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
				if (!loginPref!!.prefUserName.isNullOrEmpty()) {
					strUserName = loginPref!!.prefUserName
					strPassword = loginPref!!.prefPassword
					etUserName.setText(strUserName)
					etPassword.setText(strPassword)
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

		switchActivity(
			NSMainActivity::class.java,
			bundleOf(
				NSConstants.KEY_LOGIN_DATA to Gson().toJson(loginEvent.data)
			)
		)
	}
}
