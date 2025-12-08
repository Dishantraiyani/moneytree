package com.moneytree.app.ui.memberActivation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSMemberActivationFormActivity : NSActivity() {
    private lateinit var activateBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activateBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        initView(activateBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSMemberActivationFormFragment.newInstance(bundle), false, activateBinding.commonContainer.id)
    }
}
