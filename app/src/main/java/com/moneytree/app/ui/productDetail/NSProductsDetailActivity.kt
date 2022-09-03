package com.moneytree.app.ui.productDetail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityProductsBinding
import com.moneytree.app.databinding.NsActivityProductsDetailBinding
import com.moneytree.app.ui.products.NSProductFragment
import com.moneytree.app.ui.vouchers.NSMainVoucherFragment

class NSProductsDetailActivity : NSActivity() {
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
        replaceCurrentFragment(NSProductDetailFragment.newInstance(bundle), false, productsBinding.productsContainer.id)
    }
}
