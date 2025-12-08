package com.moneytree.app.ui.productCategory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class MTProductsCategoryActivity : NSActivity() {
    private lateinit var productsCategoryBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        productsCategoryBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(productsCategoryBinding.root)
        initView(productsCategoryBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(MTProductCategoryFragment.newInstance(), false, productsCategoryBinding.commonContainer.id)
    }
}
