package com.moneytree.app.ui.onlineorder

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
import com.moneytree.app.repository.network.responses.NSBrandData
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.ui.mycart.products.SearchableFilterDialogFragment

class OnlineOrderFilterDialogFragment : DialogFragment() {

    private var _binding: DialogProductFilterBinding? = null
    private val binding get() = _binding!!
    private var callback: NSProductFilterCallback? = null
    private var categoryList: MutableList<NSCategoryData> = arrayListOf()
    private var brandList: MutableList<NSBrandData> = arrayListOf()

    private val productModel: OnlineOrderViewModel by lazy {
        ViewModelProvider(requireParentFragment())[OnlineOrderViewModel::class.java]
    }

    companion object {
        fun newInstance(callback: NSProductFilterCallback): OnlineOrderFilterDialogFragment {
            val fragment = OnlineOrderFilterDialogFragment()
            fragment.callback = callback
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
            tvDiseasesLabel.text = getString(R.string.brands)
            ivDiseasesType.setImageResource(R.drawable.ic_filter) // Brands also uses filter icon
            
            // Hide Stock Status as it wasn't in original rl_filter for OnlineOrder
            tvStockLabel.gone()
            clFilterType.gone()

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
                    getString(R.string.select_brands),
                    brandList = brandList,
                    onApply = { setCategory() },
                    onClear = { 
                        NSApplication.getInstance().clearBrandFilter()
                        setCategory()
                    }
                )
                dialog.show(childFragmentManager, "BrandFilter")
            }

            tvApply.setOnClickListener {
                callback?.onApplyFilters()
                dismiss()
            }

            tvClear.setSafeOnClickListener {
                NSApplication.getInstance().clearFilter()
                NSApplication.getInstance().clearBrandFilter()
                setCategory()
                callback?.onClearFilters()
                dismiss()
            }
        }
    }

    private fun observeViewModel() {
        productModel.isCategoryDataAvailable.observe(viewLifecycleOwner) { categoryData ->
            categoryList.clear()
            categoryList.addAll(categoryData.categoryList)
            brandList.clear()
            brandList.addAll(categoryData.brandList.filter { !it.brandName.isNullOrEmpty() })
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

        val brands = NSApplication.getInstance().getBrandFilterList()
        if (brands.isEmpty()) {
            binding.diseasesTypeSpinner.text = "All"
        } else {
            val itemSelected = "${brands.size} Item Selected"
            binding.diseasesTypeSpinner.text = itemSelected
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
