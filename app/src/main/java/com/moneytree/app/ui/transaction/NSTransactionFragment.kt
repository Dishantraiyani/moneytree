package com.moneytree.app.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.NsFragmentTransactionBinding

class NSTransactionFragment : NSFragment() {
    private val transactionModel: NSTransactionViewModel by lazy {
        ViewModelProvider(this).get(NSTransactionViewModel::class.java)
    }
    private var _binding: NsFragmentTransactionBinding? = null

    private val transactionBinding get() = _binding!!
    private var transactionAdapter: TransactionRecycleAdapter? = null

    companion object {
        fun newInstance() = NSTransactionFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NsFragmentTransactionBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return transactionBinding.root
    }

    /**
     * View created
     *
     */
    private fun viewCreated() {
        with(transactionBinding) {
            tvHeaderTitle.text = resources.getString(R.string.transactions_title)
            tvWallet.text = resources.getString(R.string.wallet_data_dollar, 1779)
        }
        setTransactionAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(transactionBinding) {
            clBack.setOnClickListener {
                onBackPress()
            }

            with(transactionModel) {
                srlRefresh.setOnRefreshListener {
                    getTransactionData(false)
                }
            }
        }
    }

    /**
     * To add data of transaction in list
     */
    private fun setTransactionAdapter() {
        with(transactionBinding) {
            with(transactionModel) {
                rvTransaction.layoutManager = LinearLayoutManager(activity)
                transactionAdapter = TransactionRecycleAdapter(requireActivity())
                rvTransaction.adapter = transactionAdapter
                getTransactionData(true)
            }
        }
    }


    /**
     * Set transaction data
     *
     * @param isTransaction when data available it's true
     */
    private fun setTransactionData(isTransaction: Boolean) {
        with(transactionModel) {
            transactionDataManage(isTransaction)
            if (isTransaction) {
                transactionAdapter!!.clearData()
                transactionAdapter!!.updateData(transactionList)
            }
        }
    }

    /**
     * Order data manage
     *
     * @param isOrderVisible when order available it's visible
     */
    private fun transactionDataManage(isOrderVisible: Boolean) {
        with(transactionBinding) {
            rvTransaction.visibility = if (isOrderVisible) View.VISIBLE else View.GONE
            clTransactionNotFound.visibility = if (isOrderVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(transactionModel) {
            with(transactionBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner,
                    { shouldShowProgress ->
                        updateProgress(shouldShowProgress)
                    })

                isTransactionDataAvailable.observe(
                    viewLifecycleOwner,
                    { isTransaction ->
                        srlRefresh.isRefreshing = false
                        setTransactionData(isTransaction)
                    })

                failureErrorMessage.observe(viewLifecycleOwner, { errorMessage ->
                    srlRefresh.isRefreshing = false
                    showAlertDialog(errorMessage)
                })

                apiErrors.observe(viewLifecycleOwner, { apiErrors ->
                    srlRefresh.isRefreshing = false
                    parseAndShowApiError(apiErrors)
                })

                noNetworkAlert.observe(viewLifecycleOwner, {
                    srlRefresh.isRefreshing = false
                    showNoNetworkAlertDialog(getString(R.string.no_network_available), getString(R.string.network_unreachable))
                })

                validationErrorId.observe(viewLifecycleOwner, { errorId ->
                    showAlertDialog(getString(errorId))
                })

                isRefreshComplete.observe(viewLifecycleOwner, {
                    getTransactionData(!srlRefresh.isRefreshing)
                })
            }
        }
    }
}