package com.moneytree.app.ui.youtube

import android.os.Bundle
import com.moneytree.app.R
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityYoutubeBinding
import com.moneytree.app.databinding.NsActivityVouchersBinding
import com.moneytree.app.ui.vouchers.NSMainVoucherFragment

class YoutubeActivity : NSActivity() {
	private lateinit var binding: ActivityYoutubeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		binding = ActivityYoutubeBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment()
    }

	/**
	 * To initialize home fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSYoutubeFragment.newInstance(), false, binding.youtubeContainer.id)
	}
}
