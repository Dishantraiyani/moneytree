package com.moneytree.app.ui.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.NsFragmentAddRedeemBinding
import com.moneytree.app.databinding.NsFragmentEditBinding
import com.moneytree.app.databinding.NsFragmentTransferBinding


class NSEditFragment : NSFragment() {
    private var _binding: NsFragmentEditBinding? = null
    private val adBinding get() = _binding!!

    companion object {
        fun newInstance() = NSEditFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentEditBinding.inflate(inflater, container, false)
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
                tvHeaderBack.text = activity.resources.getString(R.string.edit)
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