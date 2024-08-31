package com.moneytree.app.ui.productDetail

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentProductDetailBinding
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.mycart.productDetail.NSProductDetailListRecycleAdapter
import com.moneytree.app.ui.mycart.products.NSProductViewModel

class MTProductDetailFragment : NSFragment() {
	private val productModel: NSProductViewModel by lazy {
		ViewModelProvider(this)[NSProductViewModel::class.java]
	}

    private var _binding: NsFragmentProductDetailBinding? = null

    private val productBinding get() = _binding!!
	private var productDetail: ProductDataDTO? = null
	private var strProductDetail: String? = null
	private var strProductFullList: String? = null
	private var isFromOrder: Boolean = false
	private var productResponse: NSProductListResponse? = null

	companion object {
		fun newInstance(bundle: Bundle?) = MTProductDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			strProductDetail = it.getString(NSConstants.KEY_PRODUCT_DETAIL)
			strProductDetail = it.getString(NSConstants.KEY_PRODUCT_DETAIL)
			strProductFullList = it.getString(NSConstants.KEY_PRODUCT_FULL_LIST)
			isFromOrder = it.getBoolean(NSConstants.KEY_IS_FROM_ORDER)?:false
			getProductDetail()
		}
	}

	private fun getProductDetail() {
		productDetail = Gson().fromJson(strProductDetail, ProductDataDTO::class.java)
		productResponse = Gson().fromJson(strProductFullList, NSProductListResponse::class.java)
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
		_binding = NsFragmentProductDetailBinding.inflate(inflater, container, false)
		viewCreated()
        return productBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(productBinding) {
			HeaderUtils(layoutHeader, requireActivity(), clBackView = true)
			with(layoutHeader) {
				if (productDetail != null) {
					with(productDetail!!) {
						tvHeaderBack.visible()
						tvHeaderBack.text = productName
						tvSimilarProducts.gone()
						rvProductList.gone()
						tvProductName.text = productName

						val productData = productModel.removeTrailingComma(productDetail?.multiImageList?:"")

						if (productData.isNotEmpty() && productData.contains(",")) {
							ivProductImg.gone()
							viewPager.visible()
							productModel.setupViewPager(activity, viewPager, productDetail!!)
						} else {
							ivProductImg.visible()
							viewPager.gone()
							Glide.with(activity.applicationContext).load(NSUtilities.decrypt(BuildConfig.BASE_URL_IMAGE) + productImage)
								.diskCacheStrategy(DiskCacheStrategy.NONE)
								.skipMemoryCache(true).placeholder(R.drawable.placeholder)
								.error(R.drawable.placeholder).into(ivProductImg)
						}

						tvPrice.text = addText(activity, R.string.price_value, sdPrice!!)
						tvRate.text = addText(activity, R.string.rate_title, sdPrice)

						if (sdPrice == rate) {
							tvRate.gone()
						}

						tvDescription.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
							Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
						} else {
							Html.fromHtml(description)
						}
					}
				}
			}
        }
    }
}
