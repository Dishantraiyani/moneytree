package com.moneytree.app.ui.wallets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moneytree.app.R
import com.moneytree.app.common.BackPressEvent
import com.moneytree.app.databinding.FragmentMainBinding
import com.moneytree.app.databinding.NsFragmentWalletBinding
import org.greenrobot.eventbus.EventBus

class NSWalletFragment : Fragment() {
    private var _binding: NsFragmentWalletBinding? = null
    private val mainBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentWalletBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return mainBinding.root
    }

    /**
     * View created
     *
     */
    private fun viewCreated() {
        with(mainBinding) {
            with(layoutHeader) {
                tvHeaderBack.text = resources.getString(R.string.wallet)
                clBack.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Set listener
     *
     */
    private fun setListener() {
        with(mainBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    EventBus.getDefault().post(BackPressEvent())
                }
            }
        }
    }

    companion object {
        fun newInstance() = NSWalletFragment()
    }
}