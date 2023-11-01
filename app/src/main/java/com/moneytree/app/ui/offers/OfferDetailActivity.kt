package com.moneytree.app.ui.offers

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.common.NSConstants
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.ui.repurchaseInfo.NSRePurchaseInfoFragment
import com.moneytree.app.ui.retailInfo.NSRetailInfoFragment
import com.moneytree.app.ui.royaltyInfo.NSRoyaltyInfoFragment

class OfferDetailActivity : NSActivity() {
	private lateinit var offerBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		offerBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(offerBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize product category fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		if (bundle != null) {
			val offerType = bundle.getString(NSConstants.KEY_OFFER_DETAIL_TYPE)
			if (offerType.equals(NSConstants.REPURCHASE_HISTORY)) {
				replaceCurrentFragment(NSRePurchaseInfoFragment.newInstance(bundle), false, offerBinding.commonContainer.id)
			} else if (offerType.equals(NSConstants.RETAIL_LIST)) {
				replaceCurrentFragment(NSRetailInfoFragment.newInstance(bundle), false, offerBinding.commonContainer.id)
			} else if (offerType.equals(NSConstants.ROYALTY_LIST)) {
				replaceCurrentFragment(NSRoyaltyInfoFragment.newInstance(bundle), false, offerBinding.commonContainer.id)
			}
		}
	}
}
