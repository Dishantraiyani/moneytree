package com.moneytree.app.ui.onlineorder.productDetail

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSActivityEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRequestCodes
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.config.ApiConfig
import com.moneytree.app.databinding.NsFragmentProductDetailBinding
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.mycart.products.NSProductViewModel
import com.moneytree.app.ui.onlineorder.OnlineOrderHelper
import com.moneytree.app.ui.onlineorder.cart.OnlineCartActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class OnlineOrderProductDetailFragment : NSFragment() {
	private val productModel: NSProductViewModel by lazy {
		ViewModelProvider(this)[NSProductViewModel::class.java]
	}
	private var _binding: NsFragmentProductDetailBinding? = null

	private val productBinding get() = _binding!!
	private var productDetail: ProductDataDTO? = null
	private var productResponse: NSProductListResponse? = null
	private var strProductDetail: String? = null
	private var strProductFullList: String? = null
	private var productListAdapter: OnlineOrderProductDetailListRecycleAdapter? = null

	companion object {
		fun newInstance(bundle: Bundle?) = OnlineOrderProductDetailFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			strProductDetail = it.getString(NSConstants.KEY_PRODUCT_DETAIL)
			strProductFullList = it.getString(NSConstants.KEY_PRODUCT_FULL_LIST)
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
		setListener()
		observeViewModel()
		productModel.categoryId = productDetail?.categoryId
		productModel.getProductStockListData("1", "", true, isBottomProgress = false)
		return productBinding.root
	}

	/**
	 * View created
	 */
	private fun viewCreated() {
		with(productBinding) {
			HeaderUtils(layoutHeader, requireActivity(), headerTitle = resources.getString(R.string.product_detail), clBackView = true, isCart = true)
			with(layoutHeader) {
				if (productDetail != null) {
					cardBottom.visible()
					tvCartCount.visible()
					with(productDetail!!) {
						val intent = Intent()
						activity.setResult(
							NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE_DETAIL,
							intent
						)
						NSConstants.STOCK_UPDATE = NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE_DETAIL
						tvHeaderBack.text = productName

						val productData = productModel.removeTrailingComma(productDetail?.multiImageList?:"")

						if (productData.isNotEmpty() && productData.contains(",")) {
							ivProductImg.gone()
							viewPager.visible()
							productModel.setupViewPager(activity, viewPager, productDetail!!)
						} else {
							ivProductImg.visible()
							viewPager.gone()
							Glide.with(activity).load(ApiConfig.baseUrlImage + productImage)
								.diskCacheStrategy(DiskCacheStrategy.NONE)
								.skipMemoryCache(true).placeholder(R.drawable.placeholder)
								.error(R.drawable.placeholder).into(ivProductImg)
						}
						/**/
						tvProductName.text = productName
						tvPrice.text = addText(activity, R.string.price_value, rate!!)
						tvRate.text = addText(activity, R.string.rate_title, sdPrice!!)
						tvRate.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
						val spannedText: CharSequence = Html.fromHtml(description!!)

						if (sdPrice == rate) {
							tvRate.gone()
						}

						tvDescription.text = spannedText
						val selectedItem = OnlineOrderHelper.getOrder(productDetail!!)
						if (selectedItem != null) {
							itemQty = selectedItem.itemQty
						}
						tvQtyGrid.text = itemQty.toString()
						tvStockQty.text = maxOrderQty

						if (brandName?.isNotEmpty() == true) {
							llBrandName.visible()
							tvBrandName.text = brandName
						}

						if (diseasesName?.isNotEmpty() == true) {
							llDiseasesName.visible()
							tvDiseasesName.text = diseasesName
						}

						if (categoryTagName?.isNotEmpty() == true) {
							llTagsName.visible()
							tvTagsName.text = categoryTagName.replace(",", "\n")
						}


						setCartCount()
						setTotalAmount()
					}
				}
			}
		}
	}

	private fun setProductStockAdapter() {
		with(productBinding) {
			rvProductList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
			productListAdapter =
				OnlineOrderProductDetailListRecycleAdapter(activity,  object : NSPageChangeCallback {
					override fun onPageChange(pageNo: Int) {

					}
				}, object : NSProductDetailCallback {
					override fun onResponse(productDetail: ProductDataDTO) {
						switchResultActivity(dataResult, OnlineOrderProductsDetailActivity::class.java, bundleOf(
							NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(productDetail), NSConstants.KEY_PRODUCT_FULL_LIST to Gson().toJson(
								NSProductListResponse(data = productModel.productList)
							))
						)
						finish()
					}
				}, object : NSCartTotalAmountCallback {
					override fun onResponse() {
						setTotalAmount()
					}
				})
			rvProductList.adapter = productListAdapter
			productListAdapter?.updateData(productModel.productList.filterNot { it.productId == productDetail?.productId } as MutableList<ProductDataDTO>)
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE_DETAIL || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL) {
			productListAdapter?.notifyDataSetChanged()
			setTotalAmount()
			updateProducts()
		}
	}

	private fun setTotalAmount() {
		with(productBinding) {
			CoroutineScope(Dispatchers.IO).launch {
				var totalAmountValue = 0
				for (data in OnlineOrderHelper.getOrderList()) {
					val amount1: Int = data.rate?.toInt() ?: 0
					val finalAmount1 = data.itemQty * amount1
					totalAmountValue += finalAmount1
				}
				withContext(Dispatchers.Main) {
					totalAmount.text =
						addText(activity, R.string.price_value, totalAmountValue.toString())
				}
			}

			setCartCount()
		}
	}

	private fun updateProducts() {
		CoroutineScope(Dispatchers.IO).launch {
			val selectedItem = OnlineOrderHelper.getOrder(productDetail!!)
			if (selectedItem == null) {
				productDetail!!.itemQty = 0
			}
			withContext(Dispatchers.Main) {
				with(productBinding) {
					tvQtyGrid.text = productDetail!!.itemQty.toString()
				}
			}
		}

	}

	private fun setCartCount() {
		with(productBinding.layoutHeader) {
			with(OnlineOrderHelper.getOrderList()) {
				tvCartCount.text = size.toString()
			}
		}
	}

	override fun onResume() {
		super.onResume()
		with(productBinding) {
			/*if (nsApp.isProductAdded(productDetail!!)) {
				btnAddToCart.text = activity.resources.getString(R.string.added)
			} else {
				btnAddToCart.text = activity.resources.getString(R.string.add_to_cart)
			}*/
		}
	}

	/**
	 * Set listener
	 */
	private fun setListener() {
		with(productBinding) {
			with(layoutHeader) {
				ivCart.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						switchResultActivity(dataResult, OnlineCartActivity::class.java, bundleOf(NSConstants.KEY_IS_FROM_ORDER to true))
					}
				})

				addGrid.setOnClickListener {
					with(productDetail!!) {
						val amount: Int = rate?.toInt() ?: 0
						val finalAmount = itemQty * amount
						isProductValid = finalAmount > 0

						addCart(productDetail!!, finalAmount)
					}
				}

				removeGrid.setOnClickListener {
					with(productDetail!!) {
						val amount: Int = rate?.toInt() ?: 0
						val finalAmount = itemQty * amount
						isProductValid = finalAmount > 0

						removeCart(productDetail!!, finalAmount)
					}
				}

				proceed.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						switchResultActivity(dataResult, OnlineCartActivity::class.java, bundleOf(NSConstants.KEY_IS_FROM_ORDER to true))
					}
				})

				/*btnAddToCart.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						productDetail?.let {
							if (nsApp.isProductAdded(it)) {
								nsApp.removeProduct(it)
							} else {
								nsApp.setProductList(it)
							}
							if (nsApp.isProductAdded(it)) {
								btnAddToCart.text = activity.resources.getString(R.string.added)
							} else {
								btnAddToCart.text = activity.resources.getString(R.string.add_to_cart)
							}
						}
					}
				})*/
			}
		}
	}

	private fun addCart(response: ProductDataDTO, finalAmount: Int) {
		with(productBinding) {
			with(response) {
				var stock = 0
				stock = try {
					maxOrderQty?.toInt() ?: 0
				} catch (e: Exception) {
					0
				}
				if ((itemQty < stock && stock != 0)) {
					itemQty += 1
					tvQtyGrid.text = itemQty.toString()

					val amount1: Int = rate?.toInt() ?: 0
					val finalAmount1 = itemQty * amount1
					isProductValid = finalAmount > 0
					
					OnlineOrderHelper.setOrderList(response)

					//tvPrice.text = addText(activity, R.string.price_value, finalAmount1.toString())
					tvPrice.text = addText(activity, R.string.price_value, amount1.toString())
					setTotalAmount()
				} else {
					Toast.makeText(activity, NSConstants.MAX_TITLE + maxOrderQty?.toInt() + NSConstants.MAX_TITLE_SECOND, Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	private fun removeCart(response: ProductDataDTO, finalAmount: Int) {
		with(productBinding) {
			with(response) {
				if (itemQty > 0) {
					itemQty -= 1
					tvQtyGrid.text = itemQty.toString()

					val amount1: Int = rate?.toInt() ?: 0
					val finalAmount1 = itemQty * amount1
					isProductValid = finalAmount > 0
					
					if (itemQty == 0) {
						OnlineOrderHelper.removeOrder(response)
					} else {
						OnlineOrderHelper.setOrderList(response)
					}

					setTotalAmount()
				}
			}
		}

	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(productModel) {
			with(productBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				isProductsDataAvailable.observe(
					viewLifecycleOwner
				) { isProduct ->
					setProductStockAdapter()
				}

				failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
					showAlertDialog(errorMessage)
				}

				apiErrors.observe(viewLifecycleOwner) { apiErrors ->
					parseAndShowApiError(apiErrors)
				}

				noNetworkAlert.observe(viewLifecycleOwner) {
					showNoNetworkAlertDialog(
						getString(R.string.no_network_available),
						getString(R.string.network_unreachable)
					)
				}

				validationErrorId.observe(viewLifecycleOwner) { errorId ->
					showAlertDialog(getString(errorId))
				}
			}
		}
	}
}
