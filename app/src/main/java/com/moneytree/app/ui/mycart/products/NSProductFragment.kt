package com.moneytree.app.ui.mycart.products

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.LayoutSearchableDialogFilterBinding
import com.moneytree.app.databinding.NsFragmentProductsBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.mycart.cart.NSCartActivity
import com.moneytree.app.ui.mycart.history.NSRepuhaseOrStockHistoryActivity
import com.moneytree.app.ui.mycart.productDetail.NSProductsDetailActivity
import com.moneytree.app.ui.mycart.purchaseComplete.PurchaseCompleteActivity
import com.moneytree.app.ui.mycart.stockComplete.StockCompleteActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class NSProductFragment : NSFragment() {
    private val productModel: NSProductViewModel by lazy {
        ViewModelProvider(this).get(NSProductViewModel::class.java)
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
        return productBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(productBinding) {
            with(productModel) {
				with(layoutHeader) {
					NSConstants.tabName = this@NSProductFragment.javaClass
					clBack.visible()
					ivCart.visible()
					tvHeaderBack.text = activity.resources.getString(R.string.shop)
					ivSearch.visible()
					ivAddNew.visible()
					tvCategories.visible()
					tvCartCount.visible()
					cardCategoriesType.visible()
					ivHistory.visible()
					setCartCount()
					setTotalAmount()
					ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
				}
                setProductStockAdapter()
				getProductCategory(true)
            }
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(productModel) {
            with(productBinding) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getProductStockListData(pageIndex, "", false, isBottomProgress = false)
                }

				layoutHeader.ivHistory.setOnClickListener {
					if (NSConstants.SOCKET_TYPE.isNullOrEmpty()) {
						switchActivity(NSRepuhaseOrStockHistoryActivity::class.java, bundleOf(NSConstants.STOCK_HISTORY_LIST to NSConstants.REPURCHASE_HISTORY))
					} else {
						if (NSConstants.SOCKET_TYPE.equals(NSConstants.SUPER_SOCKET_TYPE) || NSConstants.SOCKET_TYPE.equals(
								NSConstants.NORMAL_SOCKET_TYPE
							)
						) {
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

					ivSearch.setOnClickListener {
						cardSearch.visibility = View.VISIBLE
					}

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

					etSearch.setOnKeyListener(object: View.OnKeyListener{
						override fun onKey(p0: View?, keyCode: Int, event: KeyEvent): Boolean {
							if (event.action == KeyEvent.ACTION_DOWN) {
								when (keyCode) {
									KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
										val strSearch = etSearch.text.toString()
										if (strSearch.isNotEmpty()) {
											hideKeyboard(cardSearch)
											with(productModel) {
												tempProductList.addAll(productList)
												getProductStockListData(
													pageIndex,
													strSearch,
													true,
													isBottomProgress = false
												)
											}
										}
										return true
									}
								}
							}
							return false
						}
					})

					ivCart.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							switchResultActivity(dataResult, NSCartActivity::class.java)
						}
					})

					categoriesTypeSpinner.setOnClickListener {
						showFilterDialog(activity, categoryList)
					}
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
						getProductStockListData(pageIndex, "", true, isBottomProgress = false)
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
     * To add data of vouchers in list
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
                        override fun onPageChange() {
                            if (productResponse!!.nextPage) {
                                val page: Int = productList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getProductStockListData(pageIndex, "", false, isBottomProgress = true)
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
				setCategory()
            }
        }
    }

	private fun setTotalAmount() {
		with(productBinding) {
			with(productModel) {
				CoroutineScope(Dispatchers.IO).launch {
					var totalAmountValue = 0
					for (data in NSApplication.getInstance().getProductList()) {
						val amount1 : Int = data.sdPrice?.toInt() ?: 0
						val finalAmount1 = data.itemQty * amount1
						totalAmountValue += finalAmount1
					}
					withContext(Dispatchers.Main) {
						totalAmount.text = addText(activity, R.string.price_value, totalAmountValue.toString())
					}
				}

				setCartCount()
			}
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
						override fun onPageChange() {
							if (productResponse!!.nextPage) {
								val page: Int = productList.size/NSConstants.PAGINATION + 1
								pageIndex = page.toString()
								getProductStockListData(pageIndex, "", false, isBottomProgress = true)
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
				productListAdapter!!.clearData()
				productListAdapter!!.updateData(productList)
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
                productListAdapter!!.clearData()
                productListAdapter!!.updateData(productList)
            }
        }
    }

	/**
	 * Set voucher data
	 *
	 * @param isVoucher when data available it's true
	 */
	private fun setCategoriesData(isVoucher: Boolean) {
		with(productBinding) {
			with(productModel) {
				if (isVoucher) {
					val catList : ArrayList<String> = arrayListOf()
					val catIdList : ArrayList<String> = arrayListOf()
					catList.add("All")
					for (data in categoryList) {
						data.categoryName?.let { catList.add(it) }
						data.categoryId?.let { catIdList.add(it) }
					}

					/*categoryId = "0"
					categoryName = "All"*/
					//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					/*categoriesTypeSpinner.onItemSelectedListener =
						object : AdapterView.OnItemSelectedListener {
							override fun onItemSelected(
								p0: AdapterView<*>?, view: View?, position: Int, id: Long
							) {
								pageIndex = "1"
								categoryId = catIdList[position]
								categoryName = catList[position]
								getProductListData(pageIndex, "", true, isBottomProgress = false)
							}

							override fun onNothingSelected(p0: AdapterView<*>?) {
							}
						}*/
				}
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

                isProductsDataAvailable.observe(
                    viewLifecycleOwner
                ) { isProduct ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(isProduct)
                }

				isCategoryDataAvailable.observe(
					viewLifecycleOwner
				) { isCategory ->
					srlRefresh.isRefreshing = false
					setCategoriesData(isCategory)
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

	private fun setCategory() {
		with(productBinding) {
			with(productModel) {
				val data = NSApplication.getInstance().getFilterList()
				if (data.isEmpty()) {
					productBinding.categoriesTypeSpinner.text = "All"
					categoryName = "All"
					categoryId = ""
				} else {
					val itemSelected = "${data.size} Item Selected"
					productBinding.categoriesTypeSpinner.text = itemSelected
				}
				pageIndex = "1"
				categoryId = ""
				for (dat in data) {
					if (categoryId?.isNotEmpty() == true) {
						categoryId += ",$dat"
					} else {
						categoryId = dat
					}
				}
				getProductStockListData(pageIndex, "", true, isBottomProgress = false)
			}
		}
	}
}
