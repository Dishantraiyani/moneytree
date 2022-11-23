package com.moneytree.app.ui.mycart.history

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityCartBinding
import com.moneytree.app.databinding.NsActivityProductsDetailBinding
import com.moneytree.app.databinding.NsActivityRepurchaseStockBinding
import com.moneytree.app.ui.mycart.cart.NSCartFragment
import com.moneytree.app.ui.mycart.productDetail.NSProductDetailFragment

class NSRepuhaseOrStockHistoryActivity : NSActivity() {
	private lateinit var productsBinding: NsActivityRepurchaseStockBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		productsBinding = NsActivityRepurchaseStockBinding.inflate(layoutInflater)
		setContentView(productsBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize product fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(
			RSHistoryFragment.newInstance(bundle),
			false,
			productsBinding.repurchaseStockContainer.id
		)
	}
}
