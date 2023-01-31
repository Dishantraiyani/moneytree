package com.moneytree.app.ui.memberActivation

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentActivationFormBinding
import com.moneytree.app.databinding.NsFragmentMemberActivationFormBinding
import com.moneytree.app.ui.activationForm.NSActivationFormModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSMemberActivationFormFragment : NSFragment() {
    private val activationFormModel: NSMemberActivationFormModel by lazy {
		ViewModelProvider(this)[NSMemberActivationFormModel::class.java]
    }
    private var _binding: NsFragmentMemberActivationFormBinding? = null
    private val activationFormBinding get() = _binding!!

	companion object {
		fun newInstance(bundle: Bundle?) = NSMemberActivationFormFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(activationFormModel) {
				activationFormDetail = it.getString(NSConstants.KEY_MEMBER_ACTIVATION_FORM)
				isMemberFormActive = it.getBoolean(NSConstants.KEY_MEMBER_FORM_ACTIVATION_FORM)
				if (isMemberFormActive) {
					memberFormDetail = it.getString(NSConstants.KEY_MEMBER_FORM_ACTIVATION_FORM_DETAIL)
					getMemberDetail()
				}
				getActivationDetail()
			}
		}
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentMemberActivationFormBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return activationFormBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(activationFormBinding) {
            with(activationFormModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.member_activation)
                    ivBack.visibility = View.VISIBLE
                }
				if (isMemberFormActive) {
					cardMemberId.visible()
					cardFullName.visible()
					tvMemberId.text = memberListData!!.username
					tvFullName.text = memberListData!!.fullName
				}
                addRegistrationType(activity)
                setSpinner()
                setTerms()
            }
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(activationFormBinding) {
            with(activationFormModel) {

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

					btnSubmit.setOnClickListener(object : OnSingleClickListener() {
						override fun onSingleClick(v: View?) {

							if (spinnerRegisterType.selectedItemPosition != 0) {
								var registerType = registrationType[spinnerRegisterType.selectedItemPosition]
								registerType = if (registerType == "Wallet Register") {
									"W"
								} else {
									"V"
								}

								if (spinnerPackageType.selectedItemPosition != 0) {
									val packageType =
										packageList[spinnerPackageType.selectedItemPosition - 1]
									val packageId = packageType.packageId

									if (cbChecked.isChecked) {

										if (packageId != null) {
											btnSubmit.isEnabled = false
											if (memberListData?.username != null) {
												saveActivation(
													memberListData!!.username!!,
													registerType,
													packageId,
													true
												)
											} else {
												Toast.makeText(
													activity,
													activity.resources.getString(R.string.member_id_not_available),
													Toast.LENGTH_SHORT
												).show()
											}
										}
									} else {
										Toast.makeText(
											activity,
											activity.resources.getString(R.string.please_accept_terms),
											Toast.LENGTH_SHORT
										).show()
									}
								} else {
									Toast.makeText(
										activity,
										activity.resources.getString(R.string.please_select_package_name),
										Toast.LENGTH_SHORT
									).show()
								}
							} else {
								Toast.makeText(
									activity,
									activity.resources.getString(R.string.please_select_activation_type),
									Toast.LENGTH_SHORT
								).show()
							}
						}
					})
                }
            }
        }
    }

    private fun setSpinner() {
        with(activationFormBinding) {
            with(activationFormModel) {
                val arrayAdapter: ArrayAdapter<String> =
                    ArrayAdapter<String>(
                        activity,
                        R.layout.layout_spinner,
                        registrationType
                    )
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerRegisterType.adapter = arrayAdapter
            }
        }
    }

	private fun setPackageSpinner() {
		with(activationFormBinding) {
			with(activationFormModel) {
				val arrayAdapter: ArrayAdapter<String> =
					ArrayAdapter<String>(
						activity,
						R.layout.layout_spinner,
						strPackageList
					)
				arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
				spinnerPackageType.adapter = arrayAdapter
			}
		}
	}

    private fun setTerms() {
        with(activationFormBinding) {
            val html = "I agree to the <a href=${NSUtilities.decrypt(BuildConfig.TERMS)}>Terms & Conditions</a> and <a href=${NSUtilities.decrypt(BuildConfig.PRIVACY)}>Privacy Policy</a>"
            tvTermsConditions.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(activationFormModel) {
            with(activationFormBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isPackageDataAvailable.observe(
                    viewLifecycleOwner
                ) { isPackageData ->
                    if (isPackageData) {
						setPackageSpinner()
						isPackageDataAvailable.value = false
					}
                }

				isActivePackageForm.observe(viewLifecycleOwner) {
					if (it) {
						if (activationResponse != null) {
							btnSubmit.isEnabled = true
							with(activity.resources) {
								showSuccessDialog(getString(R.string.app_name), activationResponse!!.message, NSConstants.MEMBER_TH_ACTIVATE_CLICK)
							}
						}
					}
				}

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
					btnSubmit.isEnabled = true
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
					btnSubmit.isEnabled = true
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
					btnSubmit.isEnabled = true
                    showNoNetworkAlertDialog(
                        getString(R.string.no_network_available),
                        getString(R.string.network_unreachable)
                    )
                }

                validationErrorId.observe(viewLifecycleOwner) { errorId ->
					btnSubmit.isEnabled = true
                    showAlertDialog(getString(errorId))
                }
            }
        }
    }

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
		if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.MEMBER_TH_ACTIVATE_CLICK) {
			val intent = Intent()
			activity.setResult(NSRequestCodes.REQUEST_MEMBER_ACTIVATION_FORM, intent)
			finish()
		}
	}
}
