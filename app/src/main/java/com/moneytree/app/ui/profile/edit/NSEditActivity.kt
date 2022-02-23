package com.moneytree.app.ui.profile.edit

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityEditBinding

class NSEditActivity : NSActivity() {
    private lateinit var editBinding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBinding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(editBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSEditFragment.newInstance(), false, editBinding.editContainer.id)
    }
}