package com.moneyassist.app.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.Bill
import com.moneyassist.app.data.entity.ExpenseEntry
import com.moneyassist.app.data.entity.Mission
import com.moneyassist.app.ui.viewmodel.HomeViewModel

/**
 * Screen 2 — Dashboard.
 * Displays:
 *  • Net balance summary
 *  • Assist Points balance
 *  • Recent 5 transactions
 *  • Upcoming 3 bills
 *  • Active missions with progress bars
 *  • Coach Cash contextual tip
 */
class HomeFragment : Fragment() {

    private val vm: HomeViewModel by viewModels()
    private val avatars = listOf("🦁", "🐯", "🦊", "🐻", "🐼", "🐨")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvAvatar       = view.findViewById<TextView>(R.id.tv_avatar)
        val tvBalance      = view.findViewById<TextView>(R.id.tv_balance)
        val tvPoints       = view.findViewById<TextView>(R.id.tv_points_home)
        val tvCoach        = view.findViewById<TextView>(R.id.tv_coach_tip)
        val rvTransactions = view.findViewById<RecyclerView>(R.id.rv_recent_transactions)
        val rvBills        = view.findViewById<RecyclerView>(R.id.rv_upcoming_bills)
        val rvMissions     = view.findViewById<RecyclerView>(R.id.rv_active_missions)
        val fab            = view.findViewById<FloatingActionButton>(R.id.fab_add_transaction)
        val tvEmptyTx      = view.findViewById<TextView>(R.id.tv_empty_transactions)
        val tvEmptyBills   = view.findViewById<TextView>(R.id.tv_empty_bills_home)
        val tvSeeAllTx     = view.findViewById<TextView>(R.id.tv_see_all_transactions)
        val tvSeeAllBills  = view.findViewById<TextView>(R.id.tv_see_all_bills)

        tvAvatar.text = avatars.getOrElse(vm.avatarIndex) { "🦁" }
        tvCoach.text  = getString(R.string.coach_tip_dashboard)

        rvTransactions.layoutManager = LinearLayoutManager(context)
        rvBills.layoutManager        = LinearLayoutManager(context)
        rvMissions.layoutManager     = LinearLayoutManager(context)

        // Reuse adapters instead of re-creating them for better scrolling smoothness
        val txAdapter = TransactionMiniAdapter(mutableListOf())
        val billAdapter = BillMiniAdapter(mutableListOf())
        val missionAdapter = MissionMiniAdapter(mutableListOf())

        rvTransactions.adapter = txAdapter
        rvBills.adapter = billAdapter
        rvMissions.adapter = missionAdapter

        // ── Net balance ───────────────────────────────────────────
        vm.netBalance.observe(viewLifecycleOwner) { balance ->
            val sign   = if (balance >= 0) "+" else ""
            val colour = if (balance >= 0) R.color.green_primary else R.color.red_danger
            tvBalance.text      = "${sign}R${"%.2f".format(balance)}"
            tvBalance.setTextColor(resources.getColor(colour, null))
        }

        // ── Assist Points ─────────────────────────────────────────
        vm.totalPoints.observe(viewLifecycleOwner) { pts ->
            tvPoints.text = "⭐ ${pts ?: 0} pts"
        }

        // ── Recent transactions (top 5) ───────────────────────────
        vm.recentEntries.observe(viewLifecycleOwner) { entries ->
            tvEmptyTx.visibility = if (entries.isNullOrEmpty()) View.VISIBLE else View.GONE
            txAdapter.updateData(entries ?: emptyList())
        }

        // ── Upcoming bills (top 3) ────────────────────────────────
        vm.upcomingBills.observe(viewLifecycleOwner) { bills ->
            tvEmptyBills.visibility = if (bills.isNullOrEmpty()) View.VISIBLE else View.GONE
            billAdapter.updateData(bills ?: emptyList())
        }

        // ── Active missions ───────────────────────────────────────
        vm.activeMissions.observe(viewLifecycleOwner) { missions ->
            missionAdapter.updateData(missions ?: emptyList())
        }

        tvSeeAllTx.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_transactions)
        }

        tvSeeAllBills.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_bills)
        }

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addExpense)
        }
    }

    // ── Mini adapters ─────────────────────────────────────────────

    inner class TransactionMiniAdapter(private var items: List<ExpenseEntry>) :
        RecyclerView.Adapter<TransactionMiniAdapter.VH>() {

        fun updateData(newItems: List<ExpenseEntry>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvDesc:   TextView = v.findViewById(R.id.tv_tx_description)
            val tvAmount: TextView = v.findViewById(R.id.tv_tx_amount)
            val tvDate:   TextView = v.findViewById(R.id.tv_tx_date)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val tx = items[pos]
            holder.tvDesc.text   = tx.description
            holder.tvDate.text   = tx.date
            val sign   = if (tx.isIncome) "+" else "-"
            val colour = if (tx.isIncome) R.color.green_primary else R.color.red_danger
            holder.tvAmount.text = "${sign}R${"%.2f".format(tx.amount)}"
            holder.tvAmount.setTextColor(resources.getColor(colour, null))
        }
    }

    inner class BillMiniAdapter(private var items: List<Bill>) :
        RecyclerView.Adapter<BillMiniAdapter.VH>() {

        fun updateData(newItems: List<Bill>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvName:   TextView = v.findViewById(R.id.tv_bill_name)
            val tvAmount: TextView = v.findViewById(R.id.tv_bill_amount)
            val tvDue:    TextView = v.findViewById(R.id.tv_bill_due)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val bill = items[pos]
            holder.tvName.text   = "${if (bill.isUrgent) "🔴 " else "📅 "}${bill.name}"
            holder.tvAmount.text = "R${"%.2f".format(bill.amount)}"
            holder.tvDue.text    = "Due: ${bill.dueDate}"
        }
    }

    inner class MissionMiniAdapter(private var items: List<Mission>) :
        RecyclerView.Adapter<MissionMiniAdapter.VH>() {

        fun updateData(newItems: List<Mission>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvName:    TextView    = v.findViewById(R.id.tv_mission_name)
            val tvContrib: TextView    = v.findViewById(R.id.tv_monthly_contrib)
            val progress:  ProgressBar = v.findViewById(R.id.progress_mission)
            val tvPct:     TextView    = v.findViewById(R.id.tv_mission_pct)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_mission_home, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val m = items[pos]
            val pct = if (m.targetAmount > 0) ((m.currentAmount / m.targetAmount) * 100).toInt() else 0
            holder.tvName.text    = "${m.icon} ${m.name}"
            holder.tvContrib.text = "R${"%.0f".format(m.monthlyContrib)}/mo"
            holder.progress.progress = pct
            holder.tvPct.text     = "$pct%"
        }
    }
}
