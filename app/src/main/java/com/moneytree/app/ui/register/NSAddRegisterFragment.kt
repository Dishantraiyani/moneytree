package com.moneytree.app.ui.register

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.OnSingleClickListener
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentAddRegisterBinding
import com.moneytree.app.ui.activationForm.NSActivationFormFragment


class NSAddRegisterFragment : NSFragment() {
	private val registerListModel: NSRegisterViewModel by lazy {
		ViewModelProvider(this).get(NSRegisterViewModel::class.java)
	}
	private var _binding: NsFragmentAddRegisterBinding? = null
	private val registerAddBinding get() = _binding!!

	companion object {
		fun newInstance() = NSActivationFormFragment()
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
				with(layoutHeader) {
					clBack.visibility = View.VISIBLE
					tvHeaderBack.text = activity.resources.getString(R.string.member_register)
					ivBack.visibility = View.VISIBLE
				}
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

				with(layoutHeader) {
					clBack.setOnClickListener {
						onBackPress()
					}

					ivSearch.setOnClickListener {
						cardSearch.visibility = View.VISIBLE
					}

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

					ivClose.setOnClickListener {
						cardSearch.visibility = View.GONE
						etSearch.setText("")
						hideKeyboard(cardSearch)
						pageIndex = "1"
						if (tempRegisterList.isValidList()) {
							registerList.clear()
							registerList.addAll(tempRegisterList)
							tempRegisterList.clear()
						}
					}

					etSearch.setOnKeyListener(object : View.OnKeyListener {
						override fun onKey(p0: View?, keyCode: Int, event: KeyEvent): Boolean {
							if (event.action == KeyEvent.ACTION_DOWN) {
								when (keyCode) {
									KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
										val strSearch = etSearch.text.toString()
										if (strSearch.isNotEmpty()) {
											hideKeyboard(cardSearch)
											tempRegisterList.addAll(registerList)
											getRegisterListData(
												pageIndex, strSearch, true,
												isBottomProgress = false
											)
										}
										return true
									}
								}
							}
							return false
						}
					})
				}
			}
		}
	}

	private fun setTerms() {
		with(registerAddBinding) {
			val html =
				"I agree to the <a href=\"http://www.google.com\">Terms & Conditions</a> and <a href=\"http://www.google.com\">Privacy Policy</a>"
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

				isRegisterDataAvailable.observe(
					viewLifecycleOwner
				) { isNotification ->
					Log.d(TAG, "onTabSelectEvent: $isNotification")
				}

				isRegisterSuccessAvailable.observe(viewLifecycleOwner) {
					if (it) {
						onBackPress()
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

}
