package com.moneytree.app.ui.register.add

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityAddRegisterBinding

class NSAddRegisterActivity : NSActivity() {
    private lateinit var binding: NsActivityAddRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityAddRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSAddRegisterFragment.newInstance(), false, binding.registerContainer.id)
    }
}
