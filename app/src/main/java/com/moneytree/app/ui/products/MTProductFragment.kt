package com.moneytree.app.ui.products

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
import com.moneytree.app.common.BackPressEvent
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSConstants.Companion.isGridMode
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSProductFilterCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSProductDetailCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.callbacks.NSSearchResponseCallback
import com.moneytree.app.common.utils.addTextChangeListener
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setPlaceholderAdapter
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.NsFragmentProductsBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSDiseasesData
import com.moneytree.app.repository.network.responses.NSJointCategoryDiseasesResponse
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.repository.network.responses.SearchData
import com.moneytree.app.ui.common.ProductCategoryViewModel
import com.moneytree.app.ui.mycart.cart.NSCartActivity
import com.moneytree.app.ui.mycart.productDetail.NSProductsDetailActivity
import com.moneytree.app.ui.mycart.products.ProductFilterDialogFragment
import com.moneytree.app.ui.productDetail.MTProductsDetailActivity
import org.greenrobot.eventbus.EventBus
import java.util.Locale

class MTProductFragment : NSFragment(), NSSearchCallback, NSProductFilterCallback {
    private val productModel: MTProductViewModel by lazy {
        ViewModelProvider(this)[MTProductViewModel::class.java]
    }

    private val productCategoryModel: ProductCategoryViewModel by lazy {
        ViewModelProvider(this)[ProductCategoryViewModel::class.java]
    }

    private var _binding: NsFragmentProductsBinding? = null

    private val productBinding get() = _binding!!
    private var productListAdapter: MTProductListRecycleAdapter? = null
    private var isSearchClick = false

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
        observeViewModel()
        return productBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(productBinding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = productModel.categoryName?:"", isSearch = true, isAddNew = true, searchCallback = this@MTProductFragment)
            with(layoutHeader) {
                ivAddNew.setImageResource(if(isGridMode) R.drawable.ic_list else R.drawable.ic_grid)
            }
            //setProductStockAdapter()
            setCategory()
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
                    setFirstPage()
                    getProductStocks(isShowProgress = false, isBottomProgress = false)
                }

                clBottomSheet.setOnClickListener {
                    clBottomSheet.gone()
                }

                clFilterChangeBtn.setOnClickListener {
                    val dialog = ProductFilterDialogFragment.newInstance(this@MTProductFragment, isCategoryHidden = true, isStockHidden = true)
                    dialog.show(childFragmentManager, ProductFilterDialogFragment::class.java.simpleName)
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
                            switchResultActivity(dataResult, NSCartActivity::class.java)
                        }
                    })
                }
            }
        }
    }

    private fun getProductStocks(search: String = productBinding.layoutHeader.etSearch.text.toString().trim(), isShowProgress: Boolean = false, isBottomProgress: Boolean) {
        productModel.apply {
            getProductListData(pageIndex, search, isShowProgress, isBottomProgress = isBottomProgress)
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
                        MTProductListRecycleAdapter(activity, isGridMode, object :
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

                productCategoryModel.isCategoryDataAvailable.observe(
                    viewLifecycleOwner
                ) {
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

    private fun setCategory() {
        with(productModel) {
            setFirstPage()
            diseasesId = ""

            val diseases = NSApplication.getInstance().getDiseasesFilterList()
            for (dat in diseases) {
                if (diseasesId?.isNotEmpty() == true) {
                    diseasesId += ",$dat"
                } else {
                    diseasesId = dat
                }
            }
            
            val isValid = !diseasesId.isNullOrEmpty()
            productBinding.clFilterChangeBtn.setBackgroundResource(if (isValid) R.drawable.green_border else R.drawable.gray_border)
            
            getProductStocks(isShowProgress = true, isBottomProgress = false)
        }
    }

    override fun onApplyFilters() {
        setCategory()
    }

    override fun onClearFilters() {
        setCategory()
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
