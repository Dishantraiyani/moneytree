package com.moneytree.app.ui.invite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSInviteActivity : NSActivity() {
    private lateinit var activateBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activateBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        initView(activateBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSInviteFragment.newInstance(), false, activateBinding.commonContainer.id)
    }
}
