package com.moneytree.app.ui.activationForm

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityActivationFormBinding

class NSActivationFormActivity : NSActivity() {
    private lateinit var activateBinding: NsActivityActivationFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateBinding = NsActivityActivationFormBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSActivationFormFragment.newInstance(bundle), false, activateBinding.activateFormContainer.id)
    }
}
