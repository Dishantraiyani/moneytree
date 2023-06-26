package com.moneytree.app.ui.youtube

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSDateTimeHelper
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.databinding.LayoutYoutubeItemBinding
import com.moneytree.app.repository.network.responses.YoutubeItems

class NSYoutubeListRecycleAdapter(
    activityNS: Activity,
    onPageChange: NSPageChangeCallback,
    onClick: NSInfoSelectCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activity: Activity = activityNS
    private val youtubeList: MutableList<YoutubeItems> = arrayListOf()
    private val onPageChangeCallback: NSPageChangeCallback = onPageChange
    private val onClickListener: NSInfoSelectCallback = onClick

    fun updateData(royaltyList: MutableList<YoutubeItems>) {
        youtubeList.addAll(royaltyList)
        notifyItemRangeChanged(0, youtubeList.size - 1)
    }

    fun clearData() {
        youtubeList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val royaltyView = LayoutYoutubeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRoyaltyViewHolder(royaltyView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRoyaltyViewHolder) {
            val holder: NSRoyaltyViewHolder = holderRec
            holder.bind(youtubeList[holder.absoluteAdapterPosition])
        }

        if (position == youtubeList.size - 1) {
            if (((position + 1) % NSConstants.PAGINATION) == 0) {
                onPageChangeCallback.onPageChange()
            }
        }
    }

    override fun getItemCount(): Int {
        return youtubeList.size
    }

    /**
     * The view holder for royalty list
     *
     * @property royaltyBinding The royalty list view binding
     */
    inner class NSRoyaltyViewHolder(private val royaltyBinding: LayoutYoutubeItemBinding) :
        RecyclerView.ViewHolder(royaltyBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: YoutubeItems) {
            with(royaltyBinding) {
                with(response) {
                    tvVideoTitle.text = snippet?.title
					var thumbUrl: String? = null
					if (snippet != null) {
						if (snippet.thumbnails != null) {
							if (snippet.thumbnails.medium != null) {
								thumbUrl = snippet.thumbnails.medium.url
							} else if (snippet.thumbnails.high != null) {
								thumbUrl = snippet.thumbnails.high.url
							} else if (snippet.thumbnails.jsonMemberDefault != null) {
								thumbUrl = snippet.thumbnails.jsonMemberDefault.url
							}
						}
					}
					Glide.with(activity.applicationContext).load(thumbUrl).error(R.drawable.placeholder).into(ivYoutubeThumb)
					tvDate.text = NSDateTimeHelper.getDateTimeForView(snippet?.publishTime)

                    clYoutube.setOnClickListener {
                        onClickListener.onClick(absoluteAdapterPosition)
                    }
                }
            }
        }
    }
}
