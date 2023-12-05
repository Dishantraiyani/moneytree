package com.moneytree.app.ui.recharge.plans

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.callbacks.NSSearchResponseCallback
import com.moneytree.app.common.utils.addTextChangeListener
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.common.utils.setupWithAdapterAndCustomLayoutManager
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentPlansBinding
import com.moneytree.app.repository.network.responses.PackItem
import com.moneytree.app.repository.network.responses.SearchData

class PlansFragment(private val listener: DialogDismissListener) : DialogFragment() {

    private lateinit var binding: FragmentPlansBinding
    private val viewModel: NSPlansViewModel by lazy {
        ViewModelProvider(this)[NSPlansViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        viewModel.apply {
            HeaderUtils(binding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(
                R.string.recharge))

            binding.srlRefresh.setOnRefreshListener {
                getPlans()
            }

            binding.layoutHeader.ivBack.setSafeOnClickListener {
                dismiss()
            }

            binding.etSearch.addTextChangeListener { searchText ->
                if (searchText.length >= 10) {
                    mobileNumber = searchText
                    binding.progress.visible()
                    getPlans()
                }
            }
        }
    }

    private fun getPlans() {
        viewModel.apply {
            getPlans(true, accountDisplay = mobileNumber, callback = { model, map ->
                binding.srlRefresh.isRefreshing = false
                binding.llPlans.visible()
                binding.progress.gone()
                setCategoryList(map)
            })
        }
    }

    companion object {

        fun newInstance(listener: DialogDismissListener): PlansFragment =
            PlansFragment(listener).apply {
               /* arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                }*/
            }

    }

    private fun setCategoryList(map: HashMap<String, MutableList<PackItem>>) {
        val list: MutableList<String> = arrayListOf()
        for ((key, value) in map) {
            list.add(key.uppercase())
        }

        var adapter: CategoryRecycleAdapter? = null
        adapter = CategoryRecycleAdapter { category, position, isLoad ->
            if (isLoad) {
                setPlans(map[category.lowercase()] ?: arrayListOf())
            } else {
                adapter?.notifyDataSetChanged()
            }
        }
        binding.rvCategoryList.setupWithAdapterAndCustomLayoutManager(adapter, LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false))
        adapter.setData(list)
    }

    private fun setPlans(list: MutableList<PackItem>) {
        val adapter = PlansRecycleAdapter(requireActivity())
        binding.rvPlansList.setupWithAdapter(adapter)
        adapter.setData(list)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onDismiss()
    }

    interface DialogDismissListener {

        fun showProgress(isShowProgress: Boolean)
        fun onDismiss()
    }
}