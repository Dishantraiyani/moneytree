package com.moneytree.app.ui.home

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
import com.moneytree.app.databinding.DialogBankDetailBinding
import com.moneytree.app.ui.mycart.kyc.common.KycCommonViewModel

class BankDetailDialogFragment : DialogFragment() {

    private var _binding: DialogBankDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KycCommonViewModel by lazy {
        ViewModelProvider(this)[KycCommonViewModel::class.java]
    }

    companion object {
        fun newInstance(): BankDetailDialogFragment = BankDetailDialogFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBankDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setBankDetail()
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        isCancelable = false
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
            ivClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun setBankDetail() {
        viewModel.getUserDetail { userDetail ->
            binding.apply {
                userDetail.apply {
                    tvBankName.text = if (bankName.isNullOrEmpty()) getString(R.string.not_available) else bankName
                    tvAccountNo.text = if (acNo.isNullOrEmpty()) getString(R.string.not_available) else acNo
                    tvIfsc.text = if (ifscCode.isNullOrEmpty()) getString(R.string.not_available) else ifscCode
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}