package com.moneyassist.app.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.moneyassist.app.R
import com.moneyassist.app.data.db.AppDatabase
import com.moneyassist.app.data.entity.ExpenseEntry

/** Full transaction list screen with edit on tap and FAB to add new. */
class TransactionListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_transaction_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_transactions)
        val fab      = view.findViewById<FloatingActionButton>(R.id.fab_add)
        val db       = AppDatabase.getInstance(requireContext())

        recycler.layoutManager = LinearLayoutManager(context)
        val adapter = TxAdapter(mutableListOf()) { entry ->
            val action = TransactionListFragmentDirections
                .actionTransactionsToAddExpense(entryId = entry.id)
            findNavController().navigate(action)
        }
        recycler.adapter = adapter

        db.expenseEntryDao().getAllEntries().observe(viewLifecycleOwner) { entries ->
            adapter.updateData(entries ?: emptyList())
        }

        fab.setOnClickListener {
            val action = TransactionListFragmentDirections
                .actionTransactionsToAddExpense(entryId = -1)
            findNavController().navigate(action)
        }
    }

    inner class TxAdapter(
        private var items: List<ExpenseEntry>,
        private val onClick: (ExpenseEntry) -> Unit
    ) : RecyclerView.Adapter<TxAdapter.VH>() {

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
            holder.tvDate.text   = "${tx.date}${if (!tx.isSynced) "  ⏳" else ""}"
            val sign   = if (tx.isIncome) "+" else "-"
            val colour = if (tx.isIncome) R.color.green_primary else R.color.red_danger
            holder.tvAmount.text = "${sign}R${"%.2f".format(tx.amount)}"
            holder.tvAmount.setTextColor(resources.getColor(colour, null))
            holder.itemView.setOnClickListener { onClick(tx) }
        }
    }
}
