package com.moneytree.app.ui.slide

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.moneytree.app.R
import com.moneytree.app.databinding.NsFragmentProfileBinding
import com.moneytree.app.databinding.NsSlideFragmentBinding

class SliderFragment : Fragment() {
    private var _binding: NsSlideFragmentBinding? = null
    private val slideBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NsSlideFragmentBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return slideBinding.root
    }

    /**
     * Set listener
     *
     */
    private fun setListener() {

    }

    /**
     * View created
     *
     */
    private fun viewCreated() {

    }
}