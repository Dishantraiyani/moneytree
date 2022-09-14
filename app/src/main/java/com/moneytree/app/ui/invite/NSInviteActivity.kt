package com.moneytree.app.ui.invite

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityActivateBinding
import com.moneytree.app.databinding.NsActivityActivationFormBinding
import com.moneytree.app.databinding.NsActivityInviteBinding
import com.moneytree.app.ui.activate.NSActivationFragment
import com.moneytree.app.ui.activationForm.NSActivationFormFragment
import com.moneytree.app.ui.productCategory.NSProductCategoryFragment

class NSInviteActivity : NSActivity() {
    private lateinit var activateBinding: NsActivityInviteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateBinding = NsActivityInviteBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSInviteFragment.newInstance(), false, activateBinding.activateInvite.id)
    }
}
