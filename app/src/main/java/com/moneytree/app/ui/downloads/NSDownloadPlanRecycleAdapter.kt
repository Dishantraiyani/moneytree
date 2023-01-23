package com.moneytree.app.ui.downloads

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.common.SingleClickListener
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.LayoutDownloadPlanItemBinding
import com.moneytree.app.repository.network.responses.NSDownloadData
import com.rajat.pdfviewer.PdfViewerActivity

class NSDownloadPlanRecycleAdapter(val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val downloadData: MutableList<NSDownloadData> = arrayListOf()

    fun updateData(downloadList: MutableList<NSDownloadData>) {
        downloadData.addAll(downloadList)
        if (downloadList.isValidList()) {
            notifyItemRangeChanged(0, downloadData.size - 1)
        } else {
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        downloadData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val voucherView = LayoutDownloadPlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSDownloadPlanViewHolder(voucherView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSDownloadPlanViewHolder) {
            val holder: NSDownloadPlanViewHolder = holderRec
            holder.bind(downloadData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return downloadData.size
    }

    /**
     * The view holder for voucher list
     *
     * @property voucherBinding The voucher list view binding
     */
    inner class NSDownloadPlanViewHolder(private val voucherBinding: LayoutDownloadPlanItemBinding) :
        RecyclerView.ViewHolder(voucherBinding.root) {

        /**
         * To bind the voucher details view into Recycler view with given data
         *
         * @param response The voucher details
         */
        fun bind(response: NSDownloadData) {
            with(voucherBinding) {
                with(response) {
                    tvPlanName.text = title
					clDownloadPlan.setOnClickListener(object : SingleClickListener() {
						override fun performClick(v: View?) {
							if (link != null) {
								val fileName: String? = title
								activity.startActivity(
									PdfViewerActivity.launchPdfFromUrl(
										activity,
										link,
										fileName,
										Environment.DIRECTORY_DOCUMENTS,
										true
									)
								)
							}
						}
					})
                }
            }
        }
    }
}
