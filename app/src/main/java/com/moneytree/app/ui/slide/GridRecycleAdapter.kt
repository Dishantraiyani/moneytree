package com.moneytree.app.ui.slide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moneytree.app.databinding.LayoutItemRechargesBinding
import com.moneytree.app.repository.network.responses.GridModel

class GridRecycleAdapter(
    nsOfferList: MutableList<GridModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val offerList: MutableList<GridModel> = nsOfferList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rechargeView = LayoutItemRechargesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSRechargeViewHolder(rechargeView)
    }

    override fun onBindViewHolder(holderRec: RecyclerView.ViewHolder, position: Int) {
        if (holderRec is NSRechargeViewHolder) {
            val holder: NSRechargeViewHolder = holderRec
            holder.bind(offerList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    /**
     * The view holder for recharge list
     *
     * @property profileBinding The recharge list view binding
     */
    inner class NSRechargeViewHolder(private val profileBinding: LayoutItemRechargesBinding) :
        RecyclerView.ViewHolder(profileBinding.root) {

        /**
         * To bind the recharge view into Recycler view with given data
         *
         * @param response The recharge list
         */
        fun bind(response: GridModel) {
            with(profileBinding) {
                with(response) {
                    tvFieldName.text = fieldName
                    ivFieldImage.setImageResource(fieldImage)
                    if (absoluteAdapterPosition == 7) {
                        tvTick.visibility = View.VISIBLE
                    } else {
                        tvTick.visibility = View.GONE
                    }
                }
            }
        }
    }
}