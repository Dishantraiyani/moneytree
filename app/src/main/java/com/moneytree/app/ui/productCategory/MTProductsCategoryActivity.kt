package com.moneytree.app.ui.productCategory

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityProductsCategoryBinding

class MTProductsCategoryActivity : NSActivity() {
    private lateinit var productsCategoryBinding: NsActivityProductsCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsCategoryBinding = NsActivityProductsCategoryBinding.inflate(layoutInflater)
        setContentView(productsCategoryBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(MTProductCategoryFragment.newInstance(), false, productsCategoryBinding.productCategoryContainer.id)
    }
}