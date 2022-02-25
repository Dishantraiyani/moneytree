package com.moneytree.app.ui.recharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.NsFragmentRechargeBinding
import com.moneytree.app.databinding.NsFragmentSubRechargeBinding


class NSSubRechargeFragment : NSFragment() {
    private var _binding: NsFragmentSubRechargeBinding? = null
    private val rgBinding get() = _binding!!

    companion object {
        fun newInstance() = NSSubRechargeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentSubRechargeBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return rgBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {

    }

    /**
     * Set listener
     */
    private fun setListener() {

    }
}