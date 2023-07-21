package com.moneytree.app.ui.mycart.products

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
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.LayoutSearchableDialogFilterBinding
import com.moneytree.app.databinding.NsFragmentProductsBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSDiseasesData
import com.moneytree.app.repository.network.responses.NSJointCategoryDiseasesResponse
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.repository.network.responses.SearchData
import com.moneytree.app.ui.common.ProductCategoryViewModel
import com.moneytree.app.ui.mycart.cart.NSCartActivity
import com.moneytree.app.ui.mycart.history.NSRepuhaseOrStockHistoryActivity
import com.moneytree.app.ui.mycart.productDetail.NSProductsDetailActivity
import com.moneytree.app.ui.mycart.products.diseases.NSMyDiseasesFilterRecycleAdapter
import com.rajat.pdfviewer.util.hide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class NSProductFragment : NSFragment(), NSSearchCallback {
    private val productModel: NSProductViewModel by lazy {
		ViewModelProvider(this)[NSProductViewModel::class.java]
    }
	private val productCategoryModel: ProductCategoryViewModel by lazy {
		ViewModelProvider(this)[ProductCategoryViewModel::class.java]
    }
    private var _binding: NsFragmentProductsBinding? = null

    private val productBinding get() = _binding!!
    private var productListAdapter: NSProductListRecycleAdapter? = null

	companion object {
		fun newInstance() = NSProductFragment()
	}

	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentProductsBinding.inflate(inflater, container, false)
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
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString( R.string.shop), isCart = true, isSearch = true, isAddNew = true, isHistoryBtn = true, searchCallback = this@NSProductFragment)
			with(layoutHeader) {
				NSConstants.tabName = this@NSProductFragment.javaClass
				tvCategories.visible()
				tvDiseases.visible()
				tvCartCount.visible()
				cardCategoriesType.visible()
				cardDiseasesType.visible()
				setCartCount()
				setTotalAmount()
				ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
			}
			setProductStockAdapter()

			//Spinner Product Category
			productCategoryModel.getProductCategory(false, isDiseases = true)
		}
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(productModel) {
            with(productBinding) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getProductStockListData(pageIndex, productBinding.layoutHeader.etSearch.toString().trim(), false, isBottomProgress = false)
                }

				layoutHeader.ivHistory.setOnClickListener {
					if (NSConstants.SOCKET_TYPE.isNullOrEmpty()) {
						switchActivity(NSRepuhaseOrStockHistoryActivity::class.java, bundleOf(NSConstants.STOCK_HISTORY_LIST to NSConstants.REPURCHASE_HISTORY))
					} else {
						if (NSConstants.SOCKET_TYPE.equals(NSConstants.SUPER_SOCKET_TYPE)) {
							clBottomSheet.visible()
						} else {
							switchActivity(NSRepuhaseOrStockHistoryActivity::class.java, bundleOf(NSConstants.STOCK_HISTORY_LIST to NSConstants.REPURCHASE_HISTORY))
						}
					}
				}

				clBottomSheet.setOnClickListener {
					clBottomSheet.gone()
				}

				btnRepurchase.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						clBottomSheet.gone()
						switchActivity(NSRepuhaseOrStockHistoryActivity::class.java, bundleOf(NSConstants.STOCK_HISTORY_LIST to NSConstants.REPURCHASE_HISTORY))
					}
				})

				btnStock.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						clBottomSheet.gone()
						switchActivity(NSRepuhaseOrStockHistoryActivity::class.java, bundleOf(NSConstants.STOCK_HISTORY_LIST to NSConstants.SOCKET_HISTORY))
					}
				})

				with(layoutHeader) {
					ivBack.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							EventBus.getDefault().post(BackPressEvent())
						}
					})

					ivAddNew.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							isGridMode = !isGridMode
							ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
							setProductListGrid(isGridMode)
						}

					})

					proceed.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							switchResultActivity(dataResult, NSCartActivity::class.java)
						}
					})

					ivClose.setOnClickListener {
						cardSearch.visibility = View.GONE
						etSearch.setText("")
						hideKeyboard(cardSearch)
						with(productModel) {
							pageIndex = "1"
							if (tempProductList.isValidList()) {
								productList.clear()
								productList.addAll(tempProductList)
								tempProductList.clear()
								setVoucherData(productList.isValidList())
							}
						}
					}

					etSearch.addTextChangedListener(object : TextWatcher {
						override fun beforeTextChanged(
							s: CharSequence?,
							start: Int,
							count: Int,
							after: Int
						) {

						}

						override fun onTextChanged(
							s: CharSequence?,
							start: Int,
							before: Int,
							count: Int
						) {

						}

						override fun afterTextChanged(s: Editable?) {
							searchAll(s.toString())
						}

					})

					ivCart.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							switchResultActivity(dataResult, NSCartActivity::class.java)
						}
					})
				}
            }
        }
    }

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE_DETAIL || event.resultCode == NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL) {
			with(productModel) {
				if (productListAdapter != null) {
					setTotalAmount()
					if (NSConstants.STOCK_UPDATE == NSRequestCodes.REQUEST_PRODUCT_STOCK_UPDATE_DETAIL) {
						pageIndex = "1"
						getProductStockListData(pageIndex, productBinding.layoutHeader.etSearch.toString().trim(), true, isBottomProgress = false)
					} else {
						updateProducts()
					}
				}
			}
		}
	}

	private fun updateProducts() {
		CoroutineScope(Dispatchers.IO).launch {
			with(productModel) {
				val instance = NSApplication.getInstance()
				for (data in productList) {
					if (data.itemQty > 0) {
						val selectedItem = instance.getProduct(data)
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
			with(NSApplication.getInstance().getProductList()) {
				tvCartCount.text = size.toString()
			}
		}
	}

    /**
     * To add data of product in list
     */
    private fun setProductStockAdapter() {
        with(productBinding) {
            with(productModel) {
				if (!isGridMode) {
					rvProductList.layoutManager = LinearLayoutManager(activity)
				} else {
					rvProductList.layoutManager = GridLayoutManager(activity,2)
				}
                productListAdapter =
					NSProductListRecycleAdapter(activity, isGridMode, object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (productResponse!!.nextPage) {
                                val page: Int = productList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getProductStockListData(pageIndex, productBinding.layoutHeader.etSearch.toString().trim(), false, isBottomProgress = true)
                            }
                        }
                    }, object : NSProductDetailCallback {
						override fun onResponse(productDetail: ProductDataDTO) {
							switchResultActivity(dataResult, NSProductsDetailActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(productDetail), NSConstants.KEY_PRODUCT_FULL_LIST to Gson().toJson(
								NSProductListResponse(data = productList)
							)))
						}
					}, object : NSCartTotalAmountCallback {
						override fun onResponse() {
							setTotalAmount()
						}
					})
                rvProductList.adapter = productListAdapter
				setCategory()
            }
        }
    }

	private fun setTotalAmount() {
		with(productBinding) {
			CoroutineScope(Dispatchers.IO).launch {
				var totalAmountValue = 0
				for (data in NSApplication.getInstance().getProductList()) {
					val amount1 : Int = data.sdPrice?.toInt() ?: 0
					val finalAmount1 = data.itemQty * amount1
					totalAmountValue += finalAmount1
				}
				withContext(Dispatchers.Main) {
					totalAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
					llItem.setVisibility(totalAmountValue > 0)
				}
			}

			setCartCount()
		}
	}

	private fun setProductListGrid(isGrid: Boolean) {
		with(productBinding) {
			with(productModel) {
				if (!isGrid) {
					rvProductList.layoutManager = LinearLayoutManager(activity)
				} else {
					rvProductList.layoutManager = GridLayoutManager(activity,2)
				}
				productListAdapter =
					NSProductListRecycleAdapter(activity, isGrid, object : NSPageChangeCallback{
						override fun onPageChange(pageNo: Int) {
							if (productResponse!!.nextPage) {
								val page: Int = productList.size/NSConstants.PAGINATION + 1
								pageIndex = page.toString()
								getProductStockListData(pageIndex, productBinding.layoutHeader.etSearch.toString().trim(), false, isBottomProgress = true)
							}
						}
					}, object : NSProductDetailCallback {
						override fun onResponse(productDetail: ProductDataDTO) {
							switchResultActivity(dataResult, NSProductsDetailActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(productDetail)))
						}
					}, object : NSCartTotalAmountCallback {
						override fun onResponse() {
							setTotalAmount()
						}
					})
				rvProductList.adapter = productListAdapter
				productListAdapter?.clearData()
				productListAdapter?.updateData(productList)
			}
		}
	}

    private fun bottomProgress(isShowProgress: Boolean) {
        with(productBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(isVoucher: Boolean) {
        with(productModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                productListAdapter?.clearData()
                productListAdapter?.updateData(productList)
            }
        }
    }

    /**
     * Voucher data manage
     *
     * @param isVoucherVisible when voucher available it's visible
     */
    private fun voucherDataManage(isVoucherVisible: Boolean) {
        with(productBinding) {
            rvProductList.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
            clProductNotFound.visibility = if (isVoucherVisible) View.GONE else View.VISIBLE
        }
    }

	private fun setCategoryData(categoryResponse: NSJointCategoryDiseasesResponse) {
		productBinding.apply {
			categoriesTypeSpinner.setOnClickListener {
				showFilterDialog(activity, categoryResponse.categoryList)
			}

			diseasesTypeSpinner.setOnClickListener {
				showDiseasesFilterDialog(activity, categoryResponse.diseasesList)
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
                    bottomProgress(isBottomProgressShowing)
                }

				productCategoryModel.isCategoryDataAvailable.observe(
					viewLifecycleOwner
				) { categoryData ->
					setCategoryData(categoryData)
				}

                isProductsDataAvailable.observe(
                    viewLifecycleOwner
                ) { isProduct ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(isProduct)
                }

				isSearchDataAvailable.observe(
					viewLifecycleOwner
				) { searchList ->
					showSuggestions(searchList)
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
					//etSearch.showDropDown()
					etSearch.onItemClickListener =
						AdapterView.OnItemClickListener { _, _, position, _ ->
							val selectedItem = adapter.getItem(position)
							etSearch.dismissDropDown()
							pageIndex = "1"
							getProductStockListData(
								pageIndex,
								selectedItem?:"",
								true,
								isBottomProgress = false
							)
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
			listItems.layoutManager = LinearLayoutManager(activity)
			val listAdapter = NSMyFilterRecycleAdapter(activity)
			listItems.adapter = listAdapter
			listAdapter.clearData()
			listAdapter.updateData(categoryData)

			tvApply.setOnClickListener {
				dialog.dismiss()
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

	private fun showDiseasesFilterDialog(activity: Activity, diseasesData: MutableList<NSDiseasesData>) {
		val builder = AlertDialog.Builder(activity)
		val view: View = activity.layoutInflater.inflate(R.layout.layout_searchable_dialog_filter, null)
		builder.setView(view)
		val bind: LayoutSearchableDialogFilterBinding = LayoutSearchableDialogFilterBinding.bind(view)
		val dialog = builder.create()
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		with(bind) {
			listItems.layoutManager = LinearLayoutManager(activity)
			val listAdapter = NSMyDiseasesFilterRecycleAdapter(activity)
			listItems.adapter = listAdapter
			listAdapter.clearData()
			listAdapter.updateData(diseasesData)

			tvApply.setOnClickListener {
				dialog.dismiss()
				setCategory()
			}

			etSearch.addTextChangedListener(object : TextWatcher {
				override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

				}

				override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
					val tempData = ArrayList<NSDiseasesData>()
					for (nsCategoryData in diseasesData) {
						if (nsCategoryData.diseasesName != null) {
							if (nsCategoryData.diseasesName!!.lowercase(Locale.getDefault())
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

			val diseases = NSApplication.getInstance().getDiseasesFilterList()
			if (diseases.isEmpty()) {
				productBinding.diseasesTypeSpinner.text = "All"
			} else {
				val itemSelected = "${diseases.size} Item Selected"
				productBinding.diseasesTypeSpinner.text = itemSelected
			}

			pageIndex = "1"
			categoryId = ""
			diseasesId = ""
			//var tempCategoryId = ""
			for (dat in data) {
				if (categoryId?.isNotEmpty() == true) {
					categoryId += ",$dat"
				} else {
					categoryId = dat
				}
			}

			for (dat in diseases) {
				if (diseasesId?.isNotEmpty() == true) {
					diseasesId += ",$dat"
				} else {
					diseasesId = dat
				}
			}

			//categoryId = tempCategoryId + diseasesId
			getProductStockListData(pageIndex, productBinding.layoutHeader.etSearch.toString().trim(), true, isBottomProgress = false)
		}
	}



	override fun onSearch(search: String) {
		with(productModel) {
			tempProductList.addAll(productList)
			getProductStockListData(
				pageIndex,
				search,
				true,
				isBottomProgress = false
			)
		}
	}
}
