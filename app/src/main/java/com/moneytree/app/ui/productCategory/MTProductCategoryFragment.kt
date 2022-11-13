package com.moneytree.app.ui.productCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSProductCategoryCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentProductCategoryBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.ui.products.MTProductsActivity

class MTProductCategoryFragment : NSFragment() {
    private val productCategoryModel: MTProductCategoryViewModel by lazy {
        ViewModelProvider(this).get(MTProductCategoryViewModel::class.java)
    }
    private var _binding: NsFragmentProductCategoryBinding? = null

    private val productCategoryBinding get() = _binding!!
    private var categoryListAdapter: MTCategoryListRecycleAdapter? = null
    companion object {
        fun newInstance() = MTProductCategoryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentProductCategoryBinding.inflate(inflater, container, false)
		viewCreated()
		setListener()
        return productCategoryBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(productCategoryBinding) {
            with(productCategoryModel) {
				NSConstants.tabName = this@MTProductCategoryFragment.javaClass
				with(layoutHeader) {
					clBack.visible()
					tvHeaderBack.text = activity.resources.getString(R.string.categories)
				}
                setProductCategoryAdapter()
            }
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(productCategoryModel) {
            with(productCategoryBinding) {
                srlRefresh.setOnRefreshListener {
                    getProductCategory(false)
                }

				layoutHeader.ivBack.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						onBackPress()
					}
				})
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setProductCategoryAdapter() {
        with(productCategoryBinding) {
            with(productCategoryModel) {
                rvCategoryList.layoutManager = LinearLayoutManager(activity)
                categoryListAdapter = MTCategoryListRecycleAdapter(object : NSProductCategoryCallback {
					override fun onResponse(categoryData: NSCategoryData) {
							switchActivity(MTProductsActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_CATEGORY to categoryData.categoryId, NSConstants.KEY_PRODUCT_CATEGORY_NAME to categoryData.categoryName))
					}
				})
				rvCategoryList.adapter = categoryListAdapter
				getProductCategory(true)
            }
        }
    }

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(isVoucher: Boolean) {
        with(productCategoryModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                categoryListAdapter!!.clearData()
                categoryListAdapter!!.updateData(categoryList)
            }
        }
    }

    /**
     * Voucher data manage
     *
     * @param isCategoryVisible when voucher available it's visible
     */
    private fun voucherDataManage(isCategoryVisible: Boolean) {
        with(productCategoryBinding) {
            rvCategoryList.visibility = if (isCategoryVisible) View.VISIBLE else View.GONE
            clCategoryNotFound.visibility = if (isCategoryVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(productCategoryModel) {
            with(productCategoryBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isCategoryDataAvailable.observe(
                    viewLifecycleOwner
                ) { isCategory ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(isCategory)
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
