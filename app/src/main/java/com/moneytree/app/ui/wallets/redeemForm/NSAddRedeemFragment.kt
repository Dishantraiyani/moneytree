package com.moneytree.app.ui.wallets.redeemForm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.NsFragmentAddRedeemBinding


class NSAddRedeemFragment : NSFragment() {
    private var _binding: NsFragmentAddRedeemBinding? = null
    private val adBinding get() = _binding!!

    companion object {
        fun newInstance() = NSAddRedeemFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentAddRedeemBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return adBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(adBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.redeem)
                ivBack.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(adBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    onBackPress()
                }
            }
        }
    }
}