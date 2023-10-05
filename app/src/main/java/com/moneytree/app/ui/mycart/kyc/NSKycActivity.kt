package com.moneytree.app.ui.mycart.kyc

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityKycBinding
import com.moneytree.app.databinding.NsActivityOrdersBinding
import com.moneytree.app.databinding.NsActivityProductsBinding
import com.moneytree.app.ui.mycart.products.NSProductFragment

class NSKycActivity : NSActivity() {
    private lateinit var productsBinding: NsActivityKycBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsBinding = NsActivityKycBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSKycFragment.newInstance(), false, productsBinding.ordersContainer.id)
    }
}
