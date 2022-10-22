package com.moneytree.app.ui.productDetail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityProductsDetailBinding

class MTProductsDetailActivity : NSActivity() {
    private lateinit var productsBinding: NsActivityProductsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsBinding = NsActivityProductsDetailBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(MTProductDetailFragment.newInstance(bundle), false, productsBinding.productsContainer.id)
    }
}
