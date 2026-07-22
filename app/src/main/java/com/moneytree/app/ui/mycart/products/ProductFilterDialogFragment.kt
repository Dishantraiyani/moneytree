package com.moneytree.app.ui.mycart.products

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.callbacks.NSProductFilterCallback
import com.moneytree.app.common.utils.*
import com.moneytree.app.databinding.DialogProductFilterBinding
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSDiseasesData
import com.moneytree.app.ui.common.ProductCategoryViewModel
import com.moneytree.app.common.NSConstants
import com.moneytree.app.ui.products.MTProductFragment
import com.moneytree.app.ui.products.MTProductViewModel

class ProductFilterDialogFragment : DialogFragment() {

    private var _binding: DialogProductFilterBinding? = null
    private val binding get() = _binding!!
    private var callback: NSProductFilterCallback? = null
    private var categoryList: MutableList<NSCategoryData> = arrayListOf()
    private var diseasesList: MutableList<NSDiseasesData> = arrayListOf()

    private val productCategoryModel: ProductCategoryViewModel by lazy {
        ViewModelProvider(requireParentFragment())[ProductCategoryViewModel::class.java]
    }

    private val productModel: NSProductViewModel by lazy {
        ViewModelProvider(requireParentFragment())[NSProductViewModel::class.java]
    }

    private val mtProductModel: MTProductViewModel by lazy {
        ViewModelProvider(requireParentFragment())[MTProductViewModel::class.java]
    }

    companion object {
        private const val ARG_IS_CATEGORY_HIDDEN = "is_category_hidden"
        private const val ARG_IS_STOCK_HIDDEN = "is_stock_hidden"

        fun newInstance(
            callback: NSProductFilterCallback,
            isCategoryHidden: Boolean = false,
            isStockHidden: Boolean = false
        ): ProductFilterDialogFragment {
            val fragment = ProductFilterDialogFragment()
            fragment.callback = callback
            val args = Bundle()
            args.putBoolean(ARG_IS_CATEGORY_HIDDEN, isCategoryHidden)
            args.putBoolean(ARG_IS_STOCK_HIDDEN, isStockHidden)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProductFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        setCategory()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        isCancelable = true
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.FullScreenDialog)
        }
    }

    private fun setupView() {
        binding.apply {
            val isCategoryHidden = arguments?.getBoolean(ARG_IS_CATEGORY_HIDDEN, false) ?: false
            val isStockHidden = arguments?.getBoolean(ARG_IS_STOCK_HIDDEN, false) ?: false

            if (isCategoryHidden) {
                tvCategoriesLabel.gone()
                cardCategoriesType.gone()
            } else {
                tvCategoriesLabel.visible()
                cardCategoriesType.visible()
            }

            if (isStockHidden) {
                tvStockLabel.gone()
                clFilterType.gone()
            } else {
                tvStockLabel.visible()
                clFilterType.visible()
            }

            ivClose.setOnClickListener {
                dismiss()
            }

            categoriesTypeSpinner.setOnClickListener {
                val dialog = SearchableFilterDialogFragment.newInstance(
                    getString(R.string.select_categroies),
                    categoryList = categoryList,
                    onApply = { setCategory() },
                    onClear = { 
                        NSApplication.getInstance().clearFilter()
                        setCategory()
                    }
                )
                dialog.show(childFragmentManager, "CategoryFilter")
            }

            diseasesTypeSpinner.setOnClickListener {
                val dialog = SearchableFilterDialogFragment.newInstance(
                    getString(R.string.select_diseases),
                    diseasesList = diseasesList,
                    onApply = { setCategory() },
                    onClear = { 
                        NSApplication.getInstance().clearDiseasesFilter()
                        setCategory()
                    }
                )
                dialog.show(childFragmentManager, "DiseasesFilter")
            }

            statusTypeSpinner.setPlaceholderAdapter(resources.getStringArray(R.array.in_stock_filter), requireContext()) {
                if (it != null) {
                    if (requireParentFragment() is NSProductFragment) {
                        productModel.selectedStock = it
                    } else if (requireParentFragment() is MTProductFragment) {
                        mtProductModel.selectedStock = it
                    }
                }
            }

            val stockArray = resources.getStringArray(R.array.in_stock_filter)
            val selectedStock = if (requireParentFragment() is NSProductFragment) {
                productModel.selectedStock
            } else {
                mtProductModel.selectedStock
            }
            val position = stockArray.indexOf(selectedStock)
            if (position != -1) {
                statusTypeSpinner.setSelection(position)
            }

            tvApply.setOnClickListener {
                callback?.onApplyFilters()
                dismiss()
            }

            tvClear.setSafeOnClickListener {
                NSApplication.getInstance().clearFilter()
                NSApplication.getInstance().clearDiseasesFilter()
                if (requireParentFragment() is NSProductFragment) {
                    productModel.selectedStock = "All"
                } else if (requireParentFragment() is MTProductFragment) {
                    mtProductModel.selectedStock = "All"
                }
                setCategory()
                callback?.onClearFilters()
                dismiss()
            }
        }
    }

    private fun observeViewModel() {
        productCategoryModel.isCategoryDataAvailable.observe(viewLifecycleOwner) { categoryData ->
            categoryList.clear()
            categoryList.addAll(categoryData.categoryList)
            diseasesList.clear()
            diseasesList.addAll(categoryData.diseasesList)
        }
    }

    private fun setCategory() {
        val data = NSApplication.getInstance().getFilterList()
        if (data.isEmpty()) {
            binding.categoriesTypeSpinner.text = "All"
        } else {
            val itemSelected = "${data.size} Item Selected"
            binding.categoriesTypeSpinner.text = itemSelected
        }

        val diseases = NSApplication.getInstance().getDiseasesFilterList()
        if (diseases.isEmpty()) {
            binding.diseasesTypeSpinner.text = "All"
        } else {
            val itemSelected = "${diseases.size} Item Selected"
            binding.diseasesTypeSpinner.text = itemSelected
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
