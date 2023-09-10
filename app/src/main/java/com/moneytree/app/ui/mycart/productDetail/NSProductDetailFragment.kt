package com.moneytree.app.ui.mycart.productDetail

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.moneytree.app.BuildConfig
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.NsFragmentProductDetailBinding
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.mycart.cart.NSCartActivity
import com.moneytree.app.ui.mycart.products.NSProductListRecycleAdapter
import com.moneytree.app.ui.mycart.products.NSProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSProductDetailFragment : NSFragment() {
	private val productModel: NSProductViewModel by lazy {
		ViewModelProvider(this)[NSProductViewModel::class.java]
	}
	private var _binding: NsFragmentProductDetailBinding? = null

	private val productBinding get() = _binding!!
	private var productDetail: ProductDataDTO? = null
	private var productResponse: NSProductListResponse? = null
	private var strProductDetail: String? = null
	private var strProductFullList: String? = null
	private var productListAdapter: NSProductDetailListRecycleAdapter? = null

	companion object {
		fun newInstance(bundle: Bundle?) = NSProductDetailFragment().apply {
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
						productModel.setupViewPager(activity, viewPager, productDetail!!)

						/*Glide.with(activity).load(NSUtilities.decrypt(BuildConfig.BASE_URL_IMAGE) + productImage)
							.diskCacheStrategy(DiskCacheStrategy.NONE)
							.skipMemoryCache(true).placeholder(R.drawable.placeholder)
							.error(R.drawable.placeholder).into(ivProductImg)*/
						tvProductName.text = productName
						tvPrice.text = addText(activity, R.string.price_value, sdPrice!!)
						tvRate.text = addText(activity, R.string.rate_title, rate!!)
						val spannedText: CharSequence = Html.fromHtml(description!!)

						tvDescription.text = spannedText
						val selectedItem = NSApplication.getInstance().getProduct(productDetail!!)
						if (selectedItem != null) {
							itemQty = selectedItem.itemQty
						}
						tvQtyGrid.text = itemQty.toString()
						tvStockQty.text = stockQty

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
				NSProductDetailListRecycleAdapter(activity, object : NSPageChangeCallback {
					override fun onPageChange(pageNo: Int) {

					}
				}, object : NSProductDetailCallback {
					override fun onResponse(productDetail: ProductDataDTO) {
						switchResultActivity(dataResult, NSProductsDetailActivity::class.java, bundleOf(
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
		if (event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE_DETAIL) {
			productListAdapter?.notifyDataSetChanged()
			setTotalAmount()
			updateProducts()
		}
	}

	private fun setTotalAmount() {
		with(productBinding) {
			CoroutineScope(Dispatchers.IO).launch {
				var totalAmountValue = 0
				for (data in NSApplication.getInstance().getProductList()) {
					val amount1: Int = data.sdPrice?.toInt() ?: 0
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
			val instance = NSApplication.getInstance()
			val selectedItem = instance.getProduct(productDetail!!)
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
			with(NSApplication.getInstance().getProductList()) {
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
						switchResultActivity(dataResult, NSCartActivity::class.java)
					}
				})

				addGrid.setOnClickListener {
					with(productDetail!!) {
						val amount: Int = sdPrice?.toInt() ?: 0
						val finalAmount = itemQty * amount
						isProductValid = finalAmount > 0

						addCart(productDetail!!, finalAmount)
					}
				}

				removeGrid.setOnClickListener {
					with(productDetail!!) {
						val amount: Int = sdPrice?.toInt() ?: 0
						val finalAmount = itemQty * amount
						isProductValid = finalAmount > 0

						removeCart(productDetail!!, finalAmount)
					}
				}

				proceed.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						switchResultActivity(dataResult, NSCartActivity::class.java)
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
					stockQty?.toInt() ?: 0
				} catch (e: Exception) {
					0
				}
				if (itemQty < stock && stock != 0) {
					itemQty += 1
					tvQtyGrid.text = itemQty.toString()

					val amount1: Int = sdPrice?.toInt() ?: 0
					val finalAmount1 = itemQty * amount1
					isProductValid = finalAmount > 0

					NSApplication.getInstance().setProductList(response)
					tvPrice.text = addText(activity, R.string.price_value, finalAmount1.toString())
					setTotalAmount()
				} else {
					Toast.makeText(activity, "No Stock Available", Toast.LENGTH_SHORT).show()
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

					val amount1: Int = sdPrice?.toInt() ?: 0
					val finalAmount1 = itemQty * amount1
					isProductValid = finalAmount > 0

					if (itemQty == 0) {
						NSApplication.getInstance().removeProduct(response)
					} else {
						NSApplication.getInstance().setProductList(response)
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
