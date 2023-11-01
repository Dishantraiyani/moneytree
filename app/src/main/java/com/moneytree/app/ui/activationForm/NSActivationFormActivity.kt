package com.moneytree.app.ui.activationForm

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSActivationFormActivity : NSActivity() {
    private lateinit var activateBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSActivationFormFragment.newInstance(bundle), false, activateBinding.commonContainer.id)
    }
}
