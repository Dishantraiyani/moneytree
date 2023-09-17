package com.moneytree.app.ui.mycart.orders

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityOrdersBinding
import com.moneytree.app.databinding.NsActivityProductsBinding
import com.moneytree.app.ui.mycart.products.NSProductFragment

class NSOrderActivity : NSActivity() {
    private lateinit var productsBinding: NsActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsBinding = NsActivityOrdersBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSOrderFragment.newInstance(), false, productsBinding.ordersContainer.id)
    }
}
