package com.moneyassist.app.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.moneyassist.app.R
import com.moneyassist.app.data.db.AppDatabase
import com.moneyassist.app.data.entity.Bill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar

/** Screen 5 — Bills & Subscriptions (list view). */
class BillsFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: BillsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_bills, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = AppDatabase.getInstance(requireContext())

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_bills)
        val fab      = view.findViewById<FloatingActionButton>(R.id.fab_add_bill)
        val tvEmpty  = view.findViewById<TextView>(R.id.tv_empty_bills)

        adapter = BillsAdapter(mutableListOf(),
            onMarkPaid = { bill ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { db.billDao().markPaid(bill.id, LocalDate.now().toString()) }
                }
            },
            onDelete = { bill ->
                lifecycleScope.launch(Dispatchers.IO) { db.billDao().delete(bill) }
            }
        )
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        db.billDao().getAll().observe(viewLifecycleOwner) { list ->
            adapter.updateData(list ?: emptyList())
            tvEmpty.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        fab.setOnClickListener { showAddBillDialog() }
    }

    private fun showAddBillDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_bill, null)
        val etName   = dialogView.findViewById<EditText>(R.id.etBillName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etBillAmount)
        val etDate   = dialogView.findViewById<EditText>(R.id.etBillDue)

        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                etDate.setText("%04d-%02d-%02d".format(y, m + 1, d))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Bill")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val dueDate = etDate.text.toString().trim()
                if (name.isNotBlank() && dueDate.isNotBlank()) {
                    val isUrgent = try {
                        LocalDate.parse(dueDate).isBefore(LocalDate.now().plusDays(4))
                    } catch (e: Exception) { false }

                    lifecycleScope.launch(Dispatchers.IO) {
                        db.billDao().insertBill(Bill(name = name, amount = amount, dueDate = dueDate, isUrgent = isUrgent))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Inline adapter ────────────────────────────────────────────
    inner class BillsAdapter(
        private var items: List<Bill>,
        private val onMarkPaid: (Bill) -> Unit,
        private val onDelete: (Bill) -> Unit
    ) : RecyclerView.Adapter<BillsAdapter.VH>() {

        fun updateData(newItems: List<Bill>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView     = view.findViewById(R.id.tv_bill_name)
            val tvAmount: TextView   = view.findViewById(R.id.tv_bill_amount)
            val tvDue: TextView      = view.findViewById(R.id.tv_bill_due)
            val cbPaid: CheckBox     = view.findViewById(R.id.cb_mark_paid)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val bill = items[position]
            holder.tvName.text   = "${if (bill.isUrgent) "🔴 " else ""}${bill.name}"
            holder.tvAmount.text = "R${"%.2f".format(bill.amount)}"
            holder.tvDue.text    = "Due: ${bill.dueDate}"
            
            // Fix: avoid triggering listener when recycler rebinds
            holder.cbPaid.setOnCheckedChangeListener(null)
            holder.cbPaid.isChecked = bill.isPaid
            holder.cbPaid.setOnCheckedChangeListener { _, checked ->
                if (checked && !bill.isPaid) onMarkPaid(bill)
            }
            holder.itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete ${bill.name}?")
                    .setPositiveButton("Delete") { _, _ -> onDelete(bill) }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }
}
