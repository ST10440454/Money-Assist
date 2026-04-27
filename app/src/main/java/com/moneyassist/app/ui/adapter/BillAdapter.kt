package com.moneyassist.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.Bill

class BillAdapter(
    private val onMarkPaid: (Bill) -> Unit
) : ListAdapter<Bill, BillAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Bill>() {
            override fun areItemsTheSame(a: Bill, b: Bill) = a.id == b.id
            override fun areContentsTheSame(a: Bill, b: Bill) = a == b
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvCheck: TextView = view.findViewById(R.id.tvBillCheck)
        val tvIcon: TextView = view.findViewById(R.id.tvBillIcon)
        val tvName: TextView = view.findViewById(R.id.tvBillName)
        val tvDue: TextView = view.findViewById(R.id.tvBillDue)
        val tvRecurring: TextView = view.findViewById(R.id.tvBillRecurring)
        val tvAmount: TextView = view.findViewById(R.id.tvBillAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val bill = getItem(position)
        holder.tvIcon.text = bill.icon
        holder.tvName.text = bill.name
        val urgentPrefix = if (bill.isUrgent) "⚠️ " else "📅 "
        holder.tvDue.text = "${urgentPrefix}Due ${bill.dueDate}"
        holder.tvDue.setTextColor(
            holder.itemView.context.getColor(
                if (bill.isUrgent) R.color.urgent_orange else R.color.text_secondary
            )
        )
        holder.tvRecurring.text = "Recurring: ${bill.recurring}"
        holder.tvAmount.text = "R ${"%.2f".format(bill.amount)}"
        holder.tvCheck.text = if (bill.isPaid) "✓" else "○"
        holder.tvCheck.setOnClickListener {
            if (!bill.isPaid) onMarkPaid(bill)
        }
    }
}
