package com.moneytree.app.ui.mycart.productCategory

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
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentProductCategoryBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSJointCategoryDiseasesResponse
import com.moneytree.app.ui.common.ProductCategoryViewModel
import com.moneytree.app.ui.mycart.cart.NSCartActivity
import com.moneytree.app.ui.mycart.products.NSProductsActivity

class NSProductCategoryFragment : NSFragment() {
    private val productCategoryModel: ProductCategoryViewModel by lazy {
        ViewModelProvider(this)[ProductCategoryViewModel::class.java]
    }
    private var _binding: NsFragmentProductCategoryBinding? = null

    private val productCategoryBinding get() = _binding!!
    private var categoryListAdapter: NSCategoryListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSProductCategoryFragment()
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
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.categories), isCart = true)
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

				layoutHeader.ivCart.setOnClickListener(object : SingleClickListener() {
					override fun performClick(v: View?) {
						switchActivity(NSCartActivity::class.java)
					}
				})
            }
        }
    }

    /**
     * To add data of category in list
     */
    private fun setProductCategoryAdapter() {
        with(productCategoryBinding) {
            with(productCategoryModel) {
                rvCategoryList.layoutManager = LinearLayoutManager(activity)
                categoryListAdapter = NSCategoryListRecycleAdapter(object : NSProductCategoryCallback {
					override fun onResponse(categoryData: NSCategoryData) {
							switchActivity(NSProductsActivity::class.java, bundleOf(NSConstants.KEY_PRODUCT_CATEGORY to categoryData.categoryId, NSConstants.KEY_PRODUCT_CATEGORY_NAME to categoryData.categoryName))
					}
				})
				rvCategoryList.adapter = categoryListAdapter
				getProductCategory(true)
            }
        }
    }

    /**
     * Set Category data
     *
     * @param categoryData when data available it's true
     */
    private fun setCategoryData(categoryData: NSJointCategoryDiseasesResponse) {
        categoryDataManage(categoryData.categoryList.isValidList())
        categoryListAdapter?.clearData()
        categoryListAdapter?.updateData(categoryData.categoryList)
    }

    /**
     * category data manage
     *
     * @param isCategoryVisible when category available it's visible
     */
    private fun categoryDataManage(isCategoryVisible: Boolean) {
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
                ) { categoryList ->
                    srlRefresh.isRefreshing = false
                    setCategoryData(categoryList)
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
