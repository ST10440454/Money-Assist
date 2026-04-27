package com.moneyassist.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.Transaction

class TransactionAdapter(
    private val onLongClick: ((Transaction) -> Unit)? = null
) : ListAdapter<Transaction, TransactionAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(a: Transaction, b: Transaction) = a.id == b.id
            override fun areContentsTheSame(a: Transaction, b: Transaction) = a == b
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvTxIcon)
        val tvName: TextView = view.findViewById(R.id.tvTxName)
        val tvDate: TextView = view.findViewById(R.id.tvTxDate)
        val tvAmount: TextView = view.findViewById(R.id.tvTxAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val tx = getItem(position)
        holder.tvIcon.text = tx.icon
        holder.tvName.text = tx.name
        holder.tvDate.text = tx.date
        val isIncome = tx.type == "income"
        val prefix = if (isIncome) "+" else ""
        holder.tvAmount.text = "${prefix}R ${"%.2f".format(tx.amount)}"
        holder.tvAmount.setTextColor(
            holder.itemView.context.getColor(
                if (isIncome) R.color.green_primary else R.color.text_primary
            )
        )
        holder.itemView.setOnLongClickListener {
            onLongClick?.invoke(tx)
            true
        }
    }
}
