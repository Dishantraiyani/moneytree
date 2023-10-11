package com.moneytree.app.ui.products

import android.os.Bundle
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
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSConstants.Companion.isGridMode
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentProductsBinding
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.productDetail.MTProductsDetailActivity

class MTProductFragment : NSFragment(), NSSearchCallback {
    private val productModel: MTProductViewModel by lazy {
        ViewModelProvider(this).get(MTProductViewModel::class.java)
    }
    private var _binding: NsFragmentProductsBinding? = null

    private val productBinding get() = _binding!!
    private var productListAdapter: MTProductListRecycleAdapter? = null

	companion object {
		fun newInstance(bundle: Bundle?) = MTProductFragment().apply {
			arguments = bundle
		}
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
                clFilterChangeBtn.gone()
				HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = categoryName?:"", isSearch = true, isAddNew = true, searchCallback = this@MTProductFragment)
				with(layoutHeader) {
					ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
				}
                setVoucherAdapter()
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
				}
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
					MTProductListRecycleAdapter(activity, isGridMode, object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (productResponse!!.nextPage) {
                                val page: Int = productList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getProductListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    }, object : NSProductDetailCallback {
						override fun onResponse(productDetail: ProductDataDTO) {
							switchActivity(MTProductsDetailActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(productDetail)))
						}
					})
                rvProductList.adapter = productListAdapter
                pageIndex = "1"
                getProductListData(pageIndex, "", true, isBottomProgress = false)
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
					MTProductListRecycleAdapter(activity, isGrid, object : NSPageChangeCallback{
						override fun onPageChange(pageNo: Int) {
							if (productResponse!!.nextPage) {
								val page: Int = productList.size/NSConstants.PAGINATION + 1
								pageIndex = page.toString()
								getProductListData(pageIndex, "", true, isBottomProgress = true)
							}
						}
					}, object : NSProductDetailCallback {
						override fun onResponse(productDetail: ProductDataDTO) {
							switchActivity(MTProductsDetailActivity::class.java,  bundleOf(
                                NSConstants.KEY_PRODUCT_DETAIL to Gson().toJson(
                                    productDetail
                                ), NSConstants.KEY_PRODUCT_FULL_LIST to Gson().toJson(
                                    NSProductListResponse(data = productList)
                                )
                            ))
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

    override fun onSearch(search: String) {
        with(productModel) {
            tempProductList.addAll(productList)
            getProductListData(
                pageIndex,
                search,
                true,
                isBottomProgress = false
            )
        }
    }
}
