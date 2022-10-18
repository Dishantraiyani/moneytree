package com.moneytree.app.ui.noNetwork

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NetworkCheckEvent
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.databinding.FragmentMemberTreeBinding
import com.moneytree.app.databinding.FragmentNoNetworkBinding
import com.moneytree.app.ui.memberTree.MemberTreeFragment
import com.muddassir.connection_checker.ConnectionChecker
import com.muddassir.connection_checker.checkConnection
import org.greenrobot.eventbus.EventBus

class NoNetworkFragment : NSFragment() {
	private var _binding: FragmentNoNetworkBinding? = null
	private val memberTreeBinding get() = _binding!!

	companion object {
		fun newInstance() = NoNetworkFragment()
	}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
		_binding = FragmentNoNetworkBinding.inflate(inflater, container, false)
        setListener()
        return memberTreeBinding.root
    }

	private fun setListener() {
		with(memberTreeBinding) {
			btnSubmit.setOnClickListener(object : SingleClickListener() {
				override fun performClick(v: View?) {
					if (NSApplication.isNetworkConnected()) {
						onBackPress()
					}
				}
			})
		}
	}
}
