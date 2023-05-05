package com.moneytree.app.config

import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.databinding.LayoutHeaderBinding

class HeaderConfig {
	companion object {
		fun setHeader(binding: LayoutHeaderBinding,
					  clBackB: Boolean = false,
					  ivMenuB: Boolean = false,
					  ivBackB: Boolean = true,
					  tvHeaderBackB: String = "",
					  ivHistoryB: Boolean = false,
					  ivSearchB: Boolean = false,
					  ivAddNewB: Boolean = false,
					  ivCartB: Boolean = false,
					  tvCartCountB: String = "",
					  cardSearchB: Boolean = false,
					  tvAmountDataB: String = "") {

			binding.apply {
				clBack.setVisibility(clBackB)
				ivMenu.setVisibility(ivMenuB)
				ivBack.setVisibility(ivBackB)
				tvHeaderBack.setVisibility(tvHeaderBackB.isNotEmpty())
				ivHistory.setVisibility(ivHistoryB)
				ivSearch.setVisibility(ivSearchB)
				ivAddNew.setVisibility(ivAddNewB)
				ivCart.setVisibility(ivCartB)
				tvCartCount.setVisibility(tvCartCountB.isNotEmpty())
				cardSearch.setVisibility(cardSearchB)
				tvAmountData.setVisibility(tvAmountDataB.isNotEmpty())
			}
		}
	}
}
