package com.moneytree.app.ui.slots

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivitySlotsBinding
import com.moneytree.app.databinding.NsActivityVouchersBinding

class NSSlotsActivity : NSActivity() {
    private lateinit var slotsBinding: NsActivitySlotsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slotsBinding = NsActivitySlotsBinding.inflate(layoutInflater)
        setContentView(slotsBinding.root)
        loadInitialFragment(intent!!.extras!!)
    }

    /**
     * To initialize slots fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle) {
        replaceCurrentFragment(NSSlotFragment.newInstance(bundle), false, slotsBinding.slotsContainer.id)
    }
}