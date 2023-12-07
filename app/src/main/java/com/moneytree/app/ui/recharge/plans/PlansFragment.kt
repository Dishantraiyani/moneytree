package com.moneytree.app.ui.recharge.plans

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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
import com.moneytree.app.repository.network.responses.Info
import com.moneytree.app.repository.network.responses.PackItem
import com.moneytree.app.repository.network.responses.PlansResponse
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
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun setupView() {
        binding.apply {
            viewModel.apply {
                HeaderUtils(binding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(
                    R.string.recharge))

                binding.srlRefresh.setOnRefreshListener {
                    getPlans()
                }

                binding.layoutHeader.ivBack.setSafeOnClickListener {
                    dismiss()
                }

                binding.etSearchMobile.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(searchText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (searchText!!.length >= 10) {
                            mobileNumber = searchText.toString()
                            binding.progress.visible()
                            getPlans()
                        }
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                })

                if (selectedMobileNo.isNotEmpty() && selectedMobileNo.length >= 10) {
                    binding.etSearchMobile.setText(selectedMobileNo)
                }
            }
        }
    }

    private fun getPlans() {
        viewModel.apply {
            getPlans(true, accountDisplay = mobileNumber, callback = { model, map ->
                binding.srlRefresh.isRefreshing = false
                if (map.isNotEmpty()) {
                    binding.tvNoData.gone()
                    binding.llPlans.visible()
                    binding.progress.gone()
                    binding.clOperatorDetail.visible()
                    setCategoryList(model, map)
                } else {
                    binding.tvNoData.visible()
                }
            })
        }
    }

    companion object {

        private var selectedMobileNo: String = ""
        fun newInstance(mobileNo: String, listener: DialogDismissListener): PlansFragment =
            PlansFragment(listener).apply {
                selectedMobileNo = mobileNo
            }

    }

    fun getFirstLetters(inputString: String): String {
        val words = inputString.split("\\s+".toRegex()) // Split the input string into words
        val firstLetters = words.map { it.first() } // Get the first letter of each word
        return firstLetters.joinToString("") // Convert the list of first letters to a string
    }

    private fun setCategoryList(plansResponse: PlansResponse, map: HashMap<String, MutableList<PackItem>>) {
        binding.apply {
            tvFirstValue.text = getFirstLetters(plansResponse.mobileOperator?.info?.opr?:"NA")
            tvOperator.text = plansResponse.mobileOperator?.info?.opr
            val circle = "-  ${plansResponse.mobileOperator?.info?.circle}"
            tvCircle.text = circle
        }

        val list: MutableList<String> = arrayListOf()
        for ((key, value) in map) {
            list.add(key.uppercase())
        }
        list.reverse()
        var adapter: CategoryRecycleAdapter? = null
        adapter = CategoryRecycleAdapter { category, position, isLoad ->
            if (isLoad) {
                setPlans(plansResponse, map[category.lowercase()] ?: arrayListOf())
            } else {
                adapter?.notifyDataSetChanged()
            }
        }
        binding.rvCategoryList.setupWithAdapterAndCustomLayoutManager(adapter, LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false))
        adapter.setData(list)
    }

    private fun setPlans(plansResponse: PlansResponse, list: MutableList<PackItem>) {
        val adapter = PlansRecycleAdapter(requireActivity()) {
            plansResponse.data?.selectedPack = it
            listener.onClickDetail(plansResponse, viewModel.mobileNumber)
            dismiss()
        }
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

        fun onClickDetail(planResponse: PlansResponse, mobileNo: String)

    }
}