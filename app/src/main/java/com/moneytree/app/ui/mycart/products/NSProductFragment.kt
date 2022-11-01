package com.moneytree.app.ui.mycart.products

import android.os.Bundle
import android.view.KeyEvent
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
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.NsFragmentProductsBinding
import com.moneytree.app.repository.NSRechargeRepository.getRechargeListData
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.mycart.cart.NSCartActivity
import com.moneytree.app.ui.mycart.productDetail.NSProductsDetailActivity
import com.moneytree.app.ui.productCategory.MTProductsCategoryActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(productModel) {
				categoryId = it.getString(NSConstants.KEY_PRODUCT_CATEGORY)
				categoryName = it.getString(NSConstants.KEY_PRODUCT_CATEGORY_NAME)
			}
		}
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
					clBack.visible()
					ivCart.visible()
					tvHeaderBack.text = activity.resources.getString(R.string.shop)
					ivSearch.visible()
					ivAddNew.visible()
					tvCategories.visible()
					tvCartCount.visible()
					cardCategoriesType.visible()
					setCartCount()
					setTotalAmount()
					ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
				}
                setVoucherAdapter()
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
                    getProductListData(pageIndex, "", false, isBottomProgress = false)
                }

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
												getProductListData(
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
				}
            }
        }
    }

	@Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == NSRequestCodes.REQUEST_PRODUCT_CART_UPDATE) {
			with(productModel) {
				if (productListAdapter != null) {
					setTotalAmount()
					updateProducts()
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
    private fun setVoucherAdapter() {
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
                                getProductListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    }, object : NSProductDetailCallback {
						override fun onResponse(productDetail: ProductDataDTO) {
							switchActivity(NSProductsDetailActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(productDetail)))
						}
					}, object : NSCartTotalAmountCallback {
						override fun onResponse() {
							setTotalAmount()
						}
					})
                rvProductList.adapter = productListAdapter
                pageIndex = "1"
                getProductListData(pageIndex, "", true, isBottomProgress = false)
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
								getProductListData(pageIndex, "", true, isBottomProgress = true)
							}
						}
					}, object : NSProductDetailCallback {
						override fun onResponse(productDetail: ProductDataDTO) {
							switchActivity(NSProductsDetailActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(productDetail)))
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

					val adapter = ArrayAdapter(activity, R.layout.layout_spinner, catList)
					categoriesTypeSpinner.adapter = adapter
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
					categoriesTypeSpinner.onItemSelectedListener =
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
						}
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
}
