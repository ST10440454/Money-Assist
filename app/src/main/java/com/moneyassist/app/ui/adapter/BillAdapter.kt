package com.moneyassist.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.Bill

/**
 * Adapter for displaying a list of bills in a RecyclerView.
 */
class BillAdapter(
    private val onMarkPaid: (Bill) -> Unit
) : ListAdapter<Bill, BillAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Bill>() {
            override fun areItemsTheSame(a: Bill, b: Bill) = a.id == b.id
            override fun areContentsTheSame(a: Bill, b: Bill) = a == b
        }
    }

    /** ViewHolder for bill items. */
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val cbPaid: CheckBox = view.findViewById(R.id.cb_mark_paid)
        val tvIcon: TextView = view.findViewById(R.id.tv_bill_icon)
        val tvName: TextView = view.findViewById(R.id.tv_bill_name)
        val tvDue: TextView = view.findViewById(R.id.tv_bill_due)
        val tvRecurring: TextView = view.findViewById(R.id.tv_bill_recurring)
        val tvAmount: TextView = view.findViewById(R.id.tv_bill_amount)
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
        
        // Indicate urgency with an icon and different text color
        val urgentPrefix = if (bill.isUrgent) "⚠️ " else "📅 "
        holder.tvDue.text = "${urgentPrefix}Due ${bill.dueDate}"
        holder.tvDue.setTextColor(
            holder.itemView.context.getColor(
                if (bill.isUrgent) R.color.urgent_orange else R.color.text_secondary
            )
        )
        
        holder.tvRecurring.text = bill.recurring
        holder.tvAmount.text = "R ${"%.2f".format(bill.amount)}"
        
        // Handle payment status and click event
        holder.cbPaid.isChecked = bill.isPaid
        holder.cbPaid.isEnabled = !bill.isPaid
        holder.cbPaid.setOnClickListener {
            if (!bill.isPaid) onMarkPaid(bill)
        }
    }
}
