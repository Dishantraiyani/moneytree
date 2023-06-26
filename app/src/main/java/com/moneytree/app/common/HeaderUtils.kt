package com.moneytree.app.common

import android.app.Activity
import com.moneytree.app.common.utils.invisible
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutHeaderBinding

class HeaderUtils(private val binding: LayoutHeaderBinding,
                  private val activity: Activity,
                  private val clBackView: Boolean = false,
                  private val isMenu: Boolean = false,
                  private val headerTitle: String = "",
                  private val isHistoryBtn: Boolean = false,
                  private val isAddNew: Boolean = false,
                  private val isCart: Boolean = false,
                  private val cartCountValue: String = "",
                  private val amountData: String = "",
                  private val isSearch: Boolean = false) {

    init {
        setHeader()
    }

    private fun setHeader() {
        binding.apply {
            clBack.setVisibility(clBackView)
            ivHistory.setVisibility(isHistoryBtn)
            ivSearch.setVisibility(isSearch)
            ivAddNew.setVisibility(isAddNew)
            ivCart.setVisibility(isCart)
            tvCartCount.setVisibility(cartCountValue.isNotEmpty())
            tvCartCount.text = cartCountValue
            tvAmountData.setVisibility(amountData.isNotEmpty())
            tvAmountData.text = amountData


            if (isMenu) {
                ivMenu.visible()
            } else {
                ivMenu.invisible()
            }
            tvHeaderBack.text = headerTitle
            tvHeaderBack.setVisibility(headerTitle.isNotEmpty())

            clBack.setOnClickListener {
                activity.onBackPressed()
            }
        }
    }

}