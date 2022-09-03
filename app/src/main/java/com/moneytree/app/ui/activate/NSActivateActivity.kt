package com.moneytree.app.ui.activate

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityActivateBinding
import com.moneytree.app.ui.productCategory.NSProductCategoryFragment

class NSActivateActivity : NSActivity() {
    private lateinit var activateBinding: NsActivityActivateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateBinding = NsActivityActivateBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSActivationFragment.newInstance(), false, activateBinding.activateContainer.id)
    }
}
