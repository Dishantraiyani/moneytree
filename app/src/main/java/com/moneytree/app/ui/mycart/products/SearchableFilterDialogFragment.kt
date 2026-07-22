package com.moneytree.app.ui.mycart.products

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.DialogSearchableFilterBinding
import com.moneytree.app.repository.network.responses.NSBrandData
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSDiseasesData
import com.moneytree.app.ui.mycart.orders.brands.NSBrandFilterRecycleAdapter
import java.util.Locale

class SearchableFilterDialogFragment : DialogFragment() {

    private var _binding: DialogSearchableFilterBinding? = null
    private val binding get() = _binding!!

    private var title: String? = null
    private var categoryList: MutableList<NSCategoryData>? = null
    private var diseasesList: MutableList<NSDiseasesData>? = null
    private var brandList: MutableList<NSBrandData>? = null
    private var onApply: (() -> Unit)? = null
    private var onClear: (() -> Unit)? = null

    companion object {
        private const val ARG_TITLE = "title"

        fun newInstance(
            title: String,
            categoryList: MutableList<NSCategoryData>? = null,
            diseasesList: MutableList<NSDiseasesData>? = null,
            brandList: MutableList<NSBrandData>? = null,
            onApply: () -> Unit,
            onClear: () -> Unit
        ): SearchableFilterDialogFragment {
            val fragment = SearchableFilterDialogFragment()
            fragment.title = title
            fragment.categoryList = categoryList
            fragment.diseasesList = diseasesList
            fragment.brandList = brandList
            fragment.onApply = onApply
            fragment.onClear = onClear
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSearchableFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        isCancelable = true
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.FullScreenDialog)
        }
    }

    private fun setupView() {
        binding.apply {
            tvTop.text = arguments?.getString(ARG_TITLE)
            listItems.layoutManager = LinearLayoutManager(requireActivity())

            if (categoryList != null) {
                val listAdapter = NSMyFilterRecycleAdapter(requireActivity())
                listItems.adapter = listAdapter
                listAdapter.clearData()
                listAdapter.updateData(categoryList!!)

                setupSearch(listAdapter, null, null)
            } else if (diseasesList != null) {
                val listAdapter = com.moneytree.app.ui.mycart.products.diseases.NSMyDiseasesFilterRecycleAdapter(requireActivity())
                listItems.adapter = listAdapter
                listAdapter.clearData()
                listAdapter.updateData(diseasesList!!)

                setupSearch(null, listAdapter, null)
            } else if (brandList != null) {
                val listAdapter = NSBrandFilterRecycleAdapter(requireActivity())
                listItems.adapter = listAdapter
                listAdapter.clearData()
                listAdapter.updateData(brandList!!)

                setupSearch(null, null, listAdapter)
            }

            tvApply.setOnClickListener {
                onApply?.invoke()
                dismiss()
            }

            tvClear.setSafeOnClickListener {
                onClear?.invoke()
                dismiss()
            }
            
            ivClose.setOnClickListener {
                dismiss()
            }

            ivCloseSearch.setOnClickListener {
                etSearch.setText("")
            }
            ivCloseSearch.gone()
        }
    }

    private fun setupSearch(
        categoryAdapter: NSMyFilterRecycleAdapter?,
        diseasesAdapter: com.moneytree.app.ui.mycart.products.diseases.NSMyDiseasesFilterRecycleAdapter?,
        brandAdapter: NSBrandFilterRecycleAdapter?
    ) {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (charSequence?.isNotEmpty() == true) {
                    binding.ivCloseSearch.visible()
                } else {
                    binding.ivCloseSearch.gone()
                }
                val searchText = charSequence.toString().lowercase(Locale.getDefault())
                if (categoryAdapter != null && categoryList != null) {
                    val tempData = if (searchText.isEmpty()) {
                        categoryList!!
                    } else {
                        categoryList!!.filter { 
                            it.categoryName?.lowercase(Locale.getDefault())?.contains(searchText) == true 
                        }.toMutableList()
                    }
                    categoryAdapter.clearData()
                    categoryAdapter.updateData(tempData)
                } else if (diseasesAdapter != null && diseasesList != null) {
                    val tempData = if (searchText.isEmpty()) {
                        diseasesList!!
                    } else {
                        diseasesList!!.filter { 
                            it.diseasesName?.lowercase(Locale.getDefault())?.contains(searchText) == true 
                        }.toMutableList()
                    }
                    diseasesAdapter.clearData()
                    diseasesAdapter.updateData(tempData)
                } else if (brandAdapter != null && brandList != null) {
                    val tempData = if (searchText.isEmpty()) {
                        brandList!!
                    } else {
                        brandList!!.filter { 
                            it.brandName?.lowercase(Locale.getDefault())?.contains(searchText) == true 
                        }.toMutableList()
                    }
                    brandAdapter.clearData()
                    brandAdapter.updateData(tempData)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
