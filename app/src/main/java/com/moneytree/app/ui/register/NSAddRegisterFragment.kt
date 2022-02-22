package com.moneytree.app.ui.register

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentAddRegisterBinding


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
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.member_register)
                    ivBack.visibility = View.VISIBLE
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
        with(registerAddBinding) {
            with(registerListModel) {

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

                    ivSearch.setOnClickListener {
                        cardSearch.visibility = View.VISIBLE
                    }

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

    private fun setSpinner() {
        with(registerAddBinding) {
            with(registerListModel) {
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

    private fun setTerms() {
        with(registerAddBinding) {
            val html = "I agree to the <a href=\"http://www.google.com\">Terms & Conditions</a> and <a href=\"http://www.google.com\">Privacy Policy</a>"
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