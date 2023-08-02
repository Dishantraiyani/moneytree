package com.moneytree.app.ui.slide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.moneytree.app.common.NSConstants
import com.moneytree.app.databinding.NsSlideFragmentBinding
import com.moneytree.app.ui.productDetail.MTProductDetailFragment

class SliderFragment : Fragment() {
    private var _binding: NsSlideFragmentBinding? = null
    private val slideBinding get() = _binding!!
	private var position = 0
	private var url: String? = null

	companion object {
		fun newInstance(bundle: Bundle?) = SliderFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			url = it.getString(NSConstants.KEY_BANNER_URL)
			position = it.getInt(NSConstants.KEY_BANNER_POSITION)
		}
	}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
		Glide.with(requireActivity()).load(url).into(slideBinding.ivBanenr)
    }
}
