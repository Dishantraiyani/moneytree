package com.moneytree.app.ui.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSFragmentChange
import com.moneytree.app.databinding.NsFragmentMainOffersBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSMainOfferFragment : NSFragment() {
    private var _binding: NsFragmentMainOffersBinding? = null
    private val offerBinding get() = _binding!!

    companion object {
        fun newInstance() = NSMainOfferFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentMainOffersBinding.inflate(inflater, container, false)
        viewCreated()
        return offerBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
		replaceFragment(NSOfferFragment.newInstance(), false, offerBinding.flMainOfferContainer.id)
    }

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onFragmentEvent(event: NSFragmentChange) {
		with(offerBinding) {
			replaceFragment(event.fragment, event.isBackStack, flMainOfferContainer.id)
		}
	}
}
