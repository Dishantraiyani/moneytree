package com.moneytree.app.ui.invite

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.databinding.NsFragmentInviteBinding
import com.moneytree.app.repository.network.responses.NSDataUser


class NSInviteFragment : NSFragment() {
    private val inviteModel: NSInviteModel by lazy {
		ViewModelProvider(this)[NSInviteModel::class.java]
    }
    private var _binding: NsFragmentInviteBinding? = null
    private val inviteBinding get() = _binding!!

	companion object {
		fun newInstance() = NSInviteFragment()
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentInviteBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return inviteBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(inviteBinding) {
            with(inviteModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.invite)
                    ivBack.visibility = View.VISIBLE
                }
				getUserDetail()
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(inviteBinding) {
            with(inviteModel) {

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }
                }

				llFacebookClickLayout.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						val url = "https://play.google.com/store/apps/details?id=${activity.packageName}&referrer=${nsUserData!!.referCode}"
						NSUtilities.shareOnFacebook(activity, url)
					}
				})

				llWhatsAppClickLayout.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						val url = "https://play.google.com/store/apps/details?id=${activity.packageName}&referrer=${nsUserData!!.referCode}"
						NSUtilities.shareOnWhatsApp(activity, url, false)
					}
				})

				llOthersClickLayout.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						val url = "https://play.google.com/store/apps/details?id=${activity.packageName}&referrer=${nsUserData!!.referCode}"
						NSUtilities.shareAll(activity, Uri.parse(url).toString())
					}
				})
            }
        }
    }

	private fun getUserDetail() {
		with(inviteModel) {
			MainDatabase.getUserData(object : NSUserDataCallback {
				override fun onResponse(userDetail: NSDataUser) {
					nsUserData = userDetail
					inviteBinding.tvInviteCode.text = nsUserData!!.referCode
				}
			})
		}
	}

}
