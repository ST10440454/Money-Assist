package com.moneyassist.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.BudgetCategory

class BudgetAdapter : ListAdapter<BudgetCategory, BudgetAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<BudgetCategory>() {
            override fun areItemsTheSame(a: BudgetCategory, b: BudgetCategory) = a.id == b.id
            override fun areContentsTheSame(a: BudgetCategory, b: BudgetCategory) = a == b
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvBudgetName)
        val tvAmounts: TextView = view.findViewById(R.id.tvBudgetAmounts)
        val progress: ProgressBar = view.findViewById(R.id.progressBudget)
        val tvRemaining: TextView = view.findViewById(R.id.tvBudgetRemaining)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val bc = getItem(position)
        holder.tvName.text = "${bc.icon} ${bc.name}"
        holder.tvAmounts.text = "R ${"%.2f".format(bc.spent)} / R ${"%.2f".format(bc.limitAmount)}"
        val pct = ((bc.spent / bc.limitAmount) * 100).toInt().coerceIn(0, 100)
        holder.progress.progress = pct
        val over = bc.spent > bc.limitAmount
        holder.progress.progressDrawable = holder.itemView.context.getDrawable(
            if (over) R.drawable.progress_red else R.drawable.progress_green
        )
        val remaining = bc.limitAmount - bc.spent
        if (over) {
            holder.tvRemaining.text = "R ${"%.2f".format(-remaining)} over budget"
            holder.tvRemaining.setTextColor(holder.itemView.context.getColor(R.color.expense_red))
        } else {
            holder.tvRemaining.text = "R ${"%.2f".format(remaining)} remaining"
            holder.tvRemaining.setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
        }
    }
}
