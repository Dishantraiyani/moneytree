package com.moneytree.app.ui.mycart.products

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityProductsBinding
import com.moneytree.app.ui.mycart.products.NSProductFragment

class NSProductsActivity : NSActivity() {
    private lateinit var productsBinding: NsActivityProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsBinding = NsActivityProductsBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSProductFragment.newInstance(), false, productsBinding.productsContainer.id)
    }
}
