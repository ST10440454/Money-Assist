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
import com.moneyassist.app.data.entity.Mission

class MissionAdapter(
    private val showContrib: Boolean = true
) : ListAdapter<Mission, MissionAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Mission>() {
            override fun areItemsTheSame(a: Mission, b: Mission) = a.id == b.id
            override fun areContentsTheSame(a: Mission, b: Mission) = a == b
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvMissionName)
        val tvDeadline: TextView = view.findViewById(R.id.tvMissionDeadline)
        val tvTag: TextView = view.findViewById(R.id.tvMissionTag)
        val tvPct: TextView = view.findViewById(R.id.tvMissionPct)
        val progress: ProgressBar = view.findViewById(R.id.progressMission)
        val tvAmounts: TextView = view.findViewById(R.id.tvMissionAmounts)
        val tvContrib: TextView? = view.findViewById(R.id.tvMissionContrib)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layout = if (showContrib) R.layout.item_mission else R.layout.item_mission_home
        val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = getItem(position)
        holder.tvName.text = "${m.icon} ${m.name}"
        holder.tvDeadline.text = "📅 Target: ${m.deadline}"

        val isSavings = m.type == "savings"
        holder.tvTag.text = if (isSavings) "Savings" else "Debt"
        holder.tvTag.setTextColor(
            holder.itemView.context.getColor(
                if (isSavings) R.color.income_green else R.color.expense_red
            )
        )
        holder.tvTag.setBackgroundResource(
            if (isSavings) R.drawable.bg_tag_savings else R.drawable.bg_tag_debt
        )

        val pct = ((m.current / m.target) * 100).toInt().coerceIn(0, 100)
        holder.tvPct.text = "$pct%"
        holder.progress.progress = pct
        holder.progress.progressDrawable = holder.itemView.context.getDrawable(
            if (isSavings) R.drawable.progress_green else R.drawable.progress_red
        )
        holder.tvAmounts.text = "R ${formatAmount(m.current)} of R ${formatAmount(m.target)}"
        holder.tvContrib?.text = "R ${"%.2f".format(m.monthlyContrib)}"
    }

    private fun formatAmount(v: Double): String {
        return if (v >= 1000) {
            val k = v.toInt()
            val thousands = k / 1000
            val remainder = k % 1000
            if (remainder == 0) "$thousands 000" else "$thousands ${remainder.toString().padStart(3, '0')}"
        } else {
            "%.0f".format(v)
        }
    }
}
