package com.moneytree.app.common

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.NSUtilities
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
                  private val isSearch: Boolean = false,
                  private val searchCallback: NSSearchCallback? = null
) {

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

            ivSearch.setOnClickListener {
                cardSearch.visibility = View.VISIBLE
            }

            etSearch.setOnKeyListener(object: View.OnKeyListener{
                override fun onKey(p0: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                val strSearch = etSearch.text.toString()
                                if (strSearch.isNotEmpty()) {
                                    NSUtilities.hideKeyboard(activity, cardSearch)
                                    searchCallback?.onSearch(strSearch)
                                }
                                return true
                            }
                        }
                    }
                    return false
                }
            })
        }
    }

}