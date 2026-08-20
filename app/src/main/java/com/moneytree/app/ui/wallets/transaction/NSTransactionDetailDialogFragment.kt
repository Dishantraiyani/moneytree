package com.moneytree.app.ui.wallets.transaction

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import com.moneytree.app.R
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.DialogTransactionDetailBinding
import com.moneytree.app.repository.network.responses.NSWalletData
import com.google.gson.Gson

class NSTransactionDetailDialogFragment : DialogFragment() {

    private var _binding: DialogTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private var transactionData: NSWalletData? = null

    companion object {
        private const val ARG_TRANSACTION = "arg_transaction"

        fun newInstance(transaction: NSWalletData): NSTransactionDetailDialogFragment {
            val fragment = NSTransactionDetailDialogFragment()
            val args = Bundle()
            args.putString(ARG_TRANSACTION, Gson().toJson(transaction))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val json = it.getString(ARG_TRANSACTION)
            transactionData = Gson().fromJson(json, NSWalletData::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        bindData()
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
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun bindData() {
        transactionData?.let { data ->
            with(binding) {
                tvTransferId.text = data.transferid
                tvMemberId.text = data.memberid
                tvAmount.text = addText(requireActivity(), R.string.price_value, data.amount?.trim() ?: "0")
                tvDate.text = data.entryDate
                tvRemark.text = data.status
                
                val type = data.entryType?.trim()?.uppercase()
                tvType.text = type
                
                val isCredit = type?.lowercase() == "credit"
                tvType.setTextColor(if (isCredit) "#0FCE6E".toColorInt() else "#F51D46".toColorInt())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
