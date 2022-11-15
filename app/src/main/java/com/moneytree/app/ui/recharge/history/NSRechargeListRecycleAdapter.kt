package com.moneytree.app.ui.recharge.history

import android.app.Activity
import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.callbacks.NSMemberActiveSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.callbacks.NSRechargeRepeatCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.LayoutRechargeHistoryItemBinding
import com.moneytree.app.repository.network.responses.RechargeListDataItem
import com.moneytree.app.ui.slide.GridRecycleAdapter
import com.rajat.pdfviewer.PdfViewerActivity

class NSRechargeListRecycleAdapter(
	activityNS: Activity,
	private val isDisplayCountManage: Boolean = false,
	private val rechargeRepeatCallback: NSRechargeRepeatCallback,
	onPageChange: NSPageChangeCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val rechargeData: MutableList<RechargeListDataItem> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange

    fun updateData(rechargeList: MutableList<RechargeListDataItem>) {
        rechargeData.addAll(rechargeList)
        if (rechargeList.isValidList()) {
            notifyItemRangeChanged(0, rechargeData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        rechargeData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutRechargeHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRechargeViewHolder(view)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRechargeViewHolder) {
            val holder: NSRechargeViewHolder = holderRec
            holder.bind(rechargeData[holder.absoluteAdapterPosition])
        }

        if (position == rechargeData.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange()
            }
        }
    }

    override fun getItemCount(): Int {
		return if (isDisplayCountManage) {
			when (rechargeData.size) {
				2 -> {
					2
				}
				1 -> {
					1
				}
				else -> {
					2
				}
			}
		} else {
			rechargeData.size
		}
    }

    /**
     * The view holder for register list
     *
     * @property registerBinding The register list view binding
     */
    inner class NSRechargeViewHolder(private val registerBinding: LayoutRechargeHistoryItemBinding) :
        RecyclerView.ViewHolder(registerBinding.root) {

        /**
         * To bind the register details view into Recycler view with given data
         *
         * @param response The register details
         */
        fun bind(response: RechargeListDataItem) {
            with(registerBinding) {
                with(response) {
					tvAccountDisplay.text = accountDisplay
                    tvRechargeTypeValue.text = rechargeType
					tvAmountValue.text = addText(activity, R.string.price_value, amount!!)
					tvDateValue.text = createdAt
					val isCreditCheck = transactionStatus!!.lowercase() == "success"
					tvStatusValue.text = transactionStatus
					tvStatusValue.setTextColor(if(isCreditCheck) Color.parseColor("#0FCE6E") else Color.parseColor("#E74B3C"))

					btnActive.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							if (invoice != null) {
								val fileName: String =
									invoice?.substring(invoice!!.lastIndexOf('/') + 1)!!
								activity.startActivity(
									PdfViewerActivity.launchPdfFromUrl(
										activity,
										invoice,
										fileName,
										Environment.DIRECTORY_DOCUMENTS,
										true
									)
								)
							}
						}
					})

					btnRepeat.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							rechargeRepeatCallback.onClick(response)
						}
					})

					/*val isActive = directActivation.equals("NOT REQUIRED")
					btnActive.isEnabled = !isActive
					btnActive.setBackgroundResource(if (isActive) R.drawable.gray_button_order_border else R.drawable.blue_button_order_border)
					btnActive.text = directActivation
					*/
                }
            }
        }
    }
}
