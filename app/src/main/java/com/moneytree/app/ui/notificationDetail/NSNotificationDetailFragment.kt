package com.moneytree.app.ui.notificationDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentNotificationDetailBinding
import com.moneytree.app.repository.network.responses.NSNotificationListData
import com.moneytree.app.repository.network.responses.ProductDataDTO

class NSNotificationDetailFragment : NSFragment() {
    private var _binding: NsFragmentNotificationDetailBinding? = null

    private val notificationBinding get() = _binding!!
	private var notificationDetail: NSNotificationListData? = null
	private var strNotificationDetail: String? = null

	companion object {
		fun newInstance(bundle: Bundle?) = NSNotificationDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			strNotificationDetail = it.getString(NSConstants.KEY_NOTIFICATION_DETAIL)
			getNotificationDetail()
		}
	}

	private fun getNotificationDetail() {
		notificationDetail = Gson().fromJson(strNotificationDetail, NSNotificationListData::class.java)
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
		_binding = NsFragmentNotificationDetailBinding.inflate(inflater, container, false)
		viewCreated()
        return notificationBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(notificationBinding) {
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.notification_details))
			if (notificationDetail != null) {
				with(notificationDetail!!) {
					Glide.with(activity).load(img)
						.diskCacheStrategy(DiskCacheStrategy.NONE)
						.skipMemoryCache(true)
						.error(R.drawable.placeholder).into(ivNotificationImg)
					tvProductName.text = title
					tvDescription.text = body!!
				}
			}
        }
    }
}
