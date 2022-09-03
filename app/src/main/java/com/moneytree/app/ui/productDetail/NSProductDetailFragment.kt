package com.moneytree.app.ui.productDetail

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentProductDetailBinding
import com.moneytree.app.databinding.NsFragmentProductsBinding
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.products.NSProductListRecycleAdapter
import com.moneytree.app.ui.products.NSProductViewModel

class NSProductDetailFragment : NSFragment() {
    private var _binding: NsFragmentProductDetailBinding? = null

    private val productBinding get() = _binding!!
	private var productDetail: ProductDataDTO? = null
	private var strProductDetail: String? = null

	companion object {
		fun newInstance(bundle: Bundle?) = NSProductDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			strProductDetail = it.getString(NSConstants.KEY_PRODUCT_DETAIL)
			getProductDetail()
		}
	}

	private fun getProductDetail() {
		productDetail = Gson().fromJson(strProductDetail, ProductDataDTO::class.java)
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentProductDetailBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
        return productBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(productBinding) {
			with(layoutHeader) {
				clBack.visible()
				with(productDetail!!) {
					tvHeaderBack.text = productName
					Glide.with(activity).load(productImage).placeholder(R.drawable.placeholder)
						.error(R.drawable.placeholder).into(ivProductImg)
					tvProductName.text = productName
					tvPrice.text = addText(activity, R.string.price_value, sdPrice!!)
					tvRate.text = addText(activity, R.string.rate_title, rate!!)
					tvDescription.text = description!!
				}
			}
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
		with(productBinding) {
			with(layoutHeader) {
				ivBack.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						onBackPress()
					}
				})
			}
		}
    }
}
