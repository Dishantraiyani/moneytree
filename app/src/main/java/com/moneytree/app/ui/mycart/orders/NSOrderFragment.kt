package com.moneytree.app.ui.mycart.orders

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.NSConstants.Companion.isGridMode
import com.moneytree.app.common.callbacks.NSCartTotalAmountCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.callbacks.NSSearchResponseCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.LayoutSearchableDialogFilterBinding
import com.moneytree.app.databinding.NsFragmentOrdersBinding
import com.moneytree.app.repository.network.responses.NSBrandData
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSJointCategoryDiseasesResponse
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.repository.network.responses.SearchData
import com.moneytree.app.ui.mycart.cart.NSCartActivity
import com.moneytree.app.ui.mycart.orders.brands.NSBrandFilterRecycleAdapter
import com.moneytree.app.ui.mycart.orders.history.NSOrderHistoryActivity
import com.moneytree.app.ui.mycart.productDetail.NSProductsDetailActivity
import com.moneytree.app.ui.mycart.products.NSMyFilterRecycleAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class NSOrderFragment : NSFragment(), NSSearchCallback {
    private val productModel: NSOrderViewModel by lazy {
		ViewModelProvider(this)[NSOrderViewModel::class.java]
    }
    private var _binding: NsFragmentOrdersBinding? = null

    private val productBinding get() = _binding!!
    private var productListAdapter: NSOrderListRecycleAdapter? = null
	private var isSearchClick = false

	companion object {
		fun newInstance() = NSOrderFragment()
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentOrdersBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
		observeViewModel()
        return productBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(productBinding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.orders), isCart = true, isSearch = true, isAddNew = true, isHistoryBtn = true, searchCallback = this@NSOrderFragment)
			with(layoutHeader) {
				NSConstants.tabName = this@NSOrderFragment.javaClass
				tvCategories.visible()
				tvDiseases.visible()
				tvCartCount.visible()
				cardCategoriesType.visible()
				cardDiseasesType.visible()
				setCartCount()
				setTotalAmount()
				ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
			}
			//setProductStockAdapter()
			setCategory()
			//Spinner Product Category
			productModel.getOnlineOrderCategory(false, isDiseases = true)
		}
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(productModel) {
            with(productBinding) {
                srlRefresh.setOnRefreshListener {
					setFirstPage()
					getProductStocks(isShowProgress = false, isBottomProgress = false)
                }

				layoutHeader.ivHistory.setOnClickListener {
					switchActivity(NSOrderHistoryActivity::class.java, bundleOf(NSConstants.KEY_IS_FROM_ORDER to true))
				}

				clFilterChangeBtn.setOnClickListener {
					rlFilter.visible()
				}

				viewFilter.setOnClickListener {
					rlFilter.gone()
				}

				with(layoutHeader) {
					ivBack.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							onBackPress()
						}
					})

					ivAddNew.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							isGridMode = !isGridMode
							ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
							//setProductListGrid(isGridMode)
							/*val list = productAdapter?.getData()
							productAdapter?.setLoadingState(false)
							productAdapter = null
							setAdapter(list?: arrayListOf())*/
							productListAdapter = null
							setProductStockAdapter(productList)
						}
					})

					proceed.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							switchResultActivity(dataResult, NSCartActivity::class.java, bundleOf(NSConstants.KEY_IS_FROM_ORDER to true))
						}
					})

					ivClose.setOnClickListener {
						if (etSearch.text.toString().isEmpty()) {
							cardSearch.visibility = View.GONE
							hideKeyboard(cardSearch)
							setFirstPage()
							getProductStocks(isShowProgress = true, isBottomProgress = false)
						} else {
							etSearch.setText("")
						}
					}

					etSearch.addTextChangeListener { searchText ->
						searchAll(searchText, object : NSSearchResponseCallback {
							override fun onSearch(searchList: MutableList<SearchData>) {
								if (!isSearchClick) {
									showSuggestions(searchList)
								} else {
									isSearchClick = false
								}
							}
						})
					}

					ivCart.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							switchResultActivity(dataResult, NSCartActivity::class.java, bundleOf(NSConstants.KEY_IS_FROM_ORDER to true))
						}
					})
				}
            }
        }
    }

	fun isFromOrder(): Boolean {
		var isFromOrder: Boolean = true
		for (data in NSApplication.getInstance().getOrderList()) {
			if (!data.isFromOrder) {
				isFromOrder = false
			}
		}
		return isFromOrder
	}

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE_DETAIL || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL) {
			with(productModel) {
				if (productListAdapter != null) {
					setTotalAmount()
					if (NSConstants.STOCK_UPDATE == NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL) {
						setFirstPage()
						getProductStocks(isShowProgress = true, isBottomProgress = false)
					} else {
						updateProducts()
					}
				}
			}
		}
	}

	private fun getProductStocks(search: String = productBinding.layoutHeader.etSearch.text.toString().trim(), isShowProgress: Boolean = false, isBottomProgress: Boolean) {
		productModel.apply {
			getProductStockListData(pageIndex, search, isShowProgress, isBottomProgress = isBottomProgress)
		}
	}

	private fun updateProducts() {
		CoroutineScope(Dispatchers.IO).launch {
			with(productModel) {
				val instance = NSApplication.getInstance()
				for (data in productList) {
					if (data.itemQty > 0) {
						val selectedItem = instance.getOrder(data)
						if (selectedItem == null) {
							data.itemQty = 0
						}
					}
				}

				withContext(Dispatchers.Main) {
					productListAdapter?.clearData()
					productListAdapter?.updateData(productList)
				}
			}
		}
	}

	private fun setCartCount() {
		with(productBinding.layoutHeader) {
			val size = NSApplication.getInstance().getOrderList().filter { it.isFromOrder }.size
			tvCartCount.text = size.toString()
		}
	}

	private fun setProductStockAdapter(productList: MutableList<ProductDataDTO>) {
		with(productBinding) {
			with(productModel) {
				if (productListAdapter == null) {
					if (!isGridMode) {
						rvProductList.layoutManager = LinearLayoutManager(activity)
					} else {
						rvProductList.layoutManager = GridLayoutManager(activity,2)
					}

					productListAdapter =
						NSOrderListRecycleAdapter(activity, isGridMode, object :
							NSPageChangeCallback {
							override fun onPageChange(pageNo: Int) {
								val page: Int = productList.size / NSConstants.PAGINATION + 1
								pageIndex = page.toString()

								if (!pageList.contains(pageIndex)) {
									pageList.add(pageIndex)
									getProductStocks(isShowProgress = false, isBottomProgress = true)
								}
							}
						}, object : NSProductDetailCallback {
							override fun onResponse(productDetail: ProductDataDTO) {
								switchResultActivity(
									dataResult, NSProductsDetailActivity::class.java, bundleOf(
										NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(
											productDetail
										), NSConstants.KEY_PRODUCT_FULL_LIST to Gson().toJson(
											NSProductListResponse(data = productList)
										), NSConstants.KEY_IS_FROM_ORDER to true
									)
								)
							}
						}, object : NSCartTotalAmountCallback {
							override fun onResponse() {
								setTotalAmount()
							}
						})
					rvProductList.adapter = productListAdapter
					productListAdapter?.clearData()
					productListAdapter?.updateData(productList)
				} else {
					productListAdapter?.clearData()
					productListAdapter?.updateData(productList)
				}
			}
		}
	}

	private fun setFirstPage() {
		productModel.apply {
			pageList.clear()
			pageIndex = "1"
		}
	}

	private fun setTotalAmount() {
		with(productBinding) {
			CoroutineScope(Dispatchers.IO).launch {
				var totalAmountValue = 0
				for (data in NSApplication.getInstance().getOrderList()) {
					if (data.isFromOrder) {
						val amount1: Int = data.rate?.toInt() ?: 0
						val finalAmount1 = data.itemQty * amount1
						totalAmountValue += finalAmount1
					}
				}
				withContext(Dispatchers.Main) {
					totalAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
					llItem.setVisibility(totalAmountValue > 0)
				}
			}

			setCartCount()
		}
	}

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(productList: MutableList<ProductDataDTO>) {
		if (productModel.pageIndex == "1") {
			productDataManage(productList.isValidList())
		}
		setProductStockAdapter(productList)
    }

    /**
     * Voucher data manage
     *
     * @param isVoucherVisible when voucher available it's visible
     */
    private fun productDataManage(isVoucherVisible: Boolean) {
        with(productBinding) {
            rvProductList.setVisibility(isVoucherVisible)
            clProductNotFound.setVisibility(!isVoucherVisible)
        }
    }

	private fun setCategoryData(categoryResponse: NSJointCategoryDiseasesResponse) {
		productBinding.apply {
			categoriesTypeSpinner.setOnClickListener {
				showFilterDialog(activity, categoryResponse.categoryList)
			}

			brandsTypeSpinner.setOnClickListener {
				showBrandFilterDialog(activity, categoryResponse.brandList)
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

                isBottomProgressShowing.observe(
                    viewLifecycleOwner
                ) { isBottomProgressShowing ->
					cvProgress.setVisibility(isBottomProgressShowing)
                }

				isCategoryDataAvailable.observe(
					viewLifecycleOwner
				) { categoryData ->
					setCategoryData(categoryData)
				}

                isProductsDataAvailable.observe(
                    viewLifecycleOwner
                ) { productList ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(productList)
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    srlRefresh.isRefreshing = false
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    srlRefresh.isRefreshing = false
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
                    srlRefresh.isRefreshing = false
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

	private fun showSuggestions(suggestions: MutableList<SearchData>) {
		val suggestionsStr = suggestions.map { it.searchName }
		productBinding.apply {
			productModel.apply {
				layoutHeader.apply {
					val adapter = ArrayAdapter(
						requireActivity(),
						R.layout.layout_spinner_item,
						suggestionsStr
					)
					etSearch.setAdapter(adapter)
					if (suggestions.isValidList()) {
						etSearch.showDropDown()
					} else {
						etSearch.dismissDropDown()
					}
					etSearch.onItemClickListener =
						AdapterView.OnItemClickListener { _, _, position, _ ->
							isSearchClick = true
							val selectedItem = adapter.getItem(position)
							getSearchBeforeData()
							etSearch.dismissDropDown()
							setFirstPage()
							getProductStocks(isShowProgress = true, isBottomProgress = false)
						}
				}
			}
		}
	}

	private fun showFilterDialog(activity: Activity, categoryData: MutableList<NSCategoryData>) {
		val builder = AlertDialog.Builder(activity)
		val view: View = activity.layoutInflater.inflate(R.layout.layout_searchable_dialog_filter, null)
		builder.setView(view)
		val bind: LayoutSearchableDialogFilterBinding = LayoutSearchableDialogFilterBinding.bind(view)
		val dialog = builder.create()
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		with(bind) {
			tvTop.text = activity.resources.getString(R.string.select_categroies)
			listItems.layoutManager = LinearLayoutManager(activity)
			val listAdapter = NSMyFilterRecycleAdapter(activity)
			listItems.adapter = listAdapter
			listAdapter.clearData()
			listAdapter.updateData(categoryData)

			tvApply.setOnClickListener {
				dialog.dismiss()
				setCategory()
			}

			tvClear.setSafeOnClickListener {
				dialog.dismiss()
				NSApplication.getInstance().clearFilter()
				setCategory()
			}

			etSearch.addTextChangedListener(object : TextWatcher {
				override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

				}

				override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
					val tempData = ArrayList<NSCategoryData>()
					for (nsCategoryData in categoryData) {
						if (nsCategoryData.categoryName != null) {
							if (nsCategoryData.categoryName!!.lowercase(Locale.getDefault())
									.contains(
										charSequence.toString().lowercase(
											Locale.getDefault()
										)
									)
							) {
								tempData.add(nsCategoryData)
							}
						}
					}
					if (charSequence.toString().isEmpty()) {
						tempData.clear()
						tempData.addAll(categoryData)
					}
					listAdapter.clearData()
					listAdapter.updateData(tempData)
				}

				override fun afterTextChanged(p0: Editable?) {

				}
			})
		}

		dialog.show()
	}

	private fun showBrandFilterDialog(activity: Activity, diseasesData: MutableList<NSBrandData>) {
		val builder = AlertDialog.Builder(activity)
		val view: View = activity.layoutInflater.inflate(R.layout.layout_searchable_dialog_filter, null)
		builder.setView(view)
		val bind: LayoutSearchableDialogFilterBinding = LayoutSearchableDialogFilterBinding.bind(view)
		val dialog = builder.create()
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		with(bind) {
			tvTop.text = activity.resources.getString(R.string.select_brands)
			listItems.layoutManager = LinearLayoutManager(activity)
			val listAdapter = NSBrandFilterRecycleAdapter(activity)
			listItems.adapter = listAdapter
			listAdapter.clearData()
			listAdapter.updateData(diseasesData)

			tvApply.setOnClickListener {
				dialog.dismiss()
				setCategory()
			}

			tvClear.setSafeOnClickListener {
				dialog.dismiss()
				NSApplication.getInstance().clearBrandFilter()
				setCategory()
			}

			etSearch.addTextChangedListener(object : TextWatcher {
				override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

				}

				override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
					val tempData = ArrayList<NSBrandData>()
					for (nsCategoryData in diseasesData) {
						if (nsCategoryData.brandName != null) {
							if (nsCategoryData.brandName!!.lowercase(Locale.getDefault())
									.contains(
										charSequence.toString().lowercase(
											Locale.getDefault()
										)
									)
							) {
								tempData.add(nsCategoryData)
							}
						}
					}
					if (charSequence.toString().isEmpty()) {
						tempData.clear()
						tempData.addAll(diseasesData)
					}
					listAdapter.clearData()
					listAdapter.updateData(tempData)
				}

				override fun afterTextChanged(p0: Editable?) {

				}
			})
		}

		dialog.show()
	}

	private fun setCategory() {
		with(productModel) {
			val data = NSApplication.getInstance().getFilterList()
			if (data.isEmpty()) {
				productBinding.categoriesTypeSpinner.text = "All"
			} else {
				val itemSelected = "${data.size} Item Selected"
				productBinding.categoriesTypeSpinner.text = itemSelected
			}

			val brands = NSApplication.getInstance().getBrandFilterList()
			if (brands.isEmpty()) {
				productBinding.brandsTypeSpinner.text = "All"
			} else {
				val itemSelected = "${brands.size} Item Selected"
				productBinding.brandsTypeSpinner.text = itemSelected
			}

			setFirstPage()
			categoryId = ""
			brandId = ""
			//var tempCategoryId = ""
			for (dat in data) {
				if (categoryId?.isNotEmpty() == true) {
					categoryId += ",$dat"
				} else {
					categoryId = dat
				}
			}

			for (dat in brands) {
				if (brandId?.isNotEmpty() == true) {
					brandId += ",$dat"
				} else {
					brandId = dat
				}
			}

			//categoryId = tempCategoryId + diseasesId
			getProductStocks(isShowProgress = true, isBottomProgress = false)
		}
	}



	override fun onSearch(search: String) {
		with(productModel) {
			setFirstPage()
			getSearchBeforeData()
			getProductStocks(search, isShowProgress = true, isBottomProgress = false)
		}
	}

	private fun getSearchBeforeData() {
		with(productModel) {
			/*if (!tempProductList.isValidList()) {
				tempProductList = arrayListOf()
				tempProductList.addAll(productList)
			}*/
		}
	}
}
