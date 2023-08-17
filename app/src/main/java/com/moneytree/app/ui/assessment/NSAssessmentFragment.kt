package com.moneytree.app.ui.assessment

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.databinding.NsFragmentAssessmentBinding


class NSAssessmentFragment : BaseViewModelFragment<NSAssessmentViewModel, NsFragmentAssessmentBinding>() {

	override val viewModel: NSAssessmentViewModel by lazy {
		ViewModelProvider(this)[NSAssessmentViewModel::class.java]
	}

	companion object {
		fun newInstance() = NSAssessmentFragment()
	}

	override fun getFragmentBinding(
		inflater: LayoutInflater,
		container: ViewGroup?
	): NsFragmentAssessmentBinding {
		return NsFragmentAssessmentBinding.inflate(inflater, container, false)
	}

	override fun setupViews() {
		super.setupViews()
		baseObserveViewModel(viewModel)
		/*HeaderUtils(binding.layoutHeader, requireActivity(), headerTitle = activity.resources.getString(
			R.string.perso), clBackView = true)*/

		binding.apply {
			webView.settings.javaScriptEnabled = true
			binding.webView.settings.userAgentString = WebSettings.getDefaultUserAgent(requireContext())
			webView.webChromeClient = WebChromeClient()
			webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

			webView.setOnKeyListener { _, keyCode, event ->
				if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP && webView.canGoBack()) {
					webView.goBack()
					true
				} else {
					false
				}
			}

			webView.webViewClient = WebViewClient()
			webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
			webView.settings.domStorageEnabled = true
			WebView.setWebContentsDebuggingEnabled(true)

			val url = "https://www.misupplement.com/start-assessment"
			webView.loadUrl(url)
		}
	}
}
