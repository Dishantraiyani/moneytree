package com.moneytree.app.ui.mycart.cart

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityCartBinding
import com.moneytree.app.databinding.NsActivityProductsDetailBinding
import com.moneytree.app.ui.mycart.productDetail.NSProductDetailFragment

class NSCartActivity : NSActivity() {
    private lateinit var productsBinding: NsActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsBinding = NsActivityCartBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSCartFragment.newInstance(), false, productsBinding.cartContainer.id)
    }
}