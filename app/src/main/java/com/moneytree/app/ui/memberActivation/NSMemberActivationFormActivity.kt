package com.moneytree.app.ui.memberActivation

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityActivateBinding
import com.moneytree.app.databinding.NsActivityActivationFormBinding
import com.moneytree.app.databinding.NsActivityMemberActivationFormBinding
import com.moneytree.app.ui.activate.NSActivationFragment
import com.moneytree.app.ui.activationForm.NSActivationFormFragment
import com.moneytree.app.ui.productCategory.NSProductCategoryFragment

class NSMemberActivationFormActivity : NSActivity() {
    private lateinit var activateBinding: NsActivityMemberActivationFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateBinding = NsActivityMemberActivationFormBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSMemberActivationFormFragment.newInstance(bundle), false, activateBinding.activateFormContainer.id)
    }
}
