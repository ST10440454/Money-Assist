package com.moneyassist.app.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.Bill
import com.moneyassist.app.ui.adapter.BillAdapter
import com.moneyassist.app.ui.viewmodel.BillsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Fragment for managing and tracking upcoming and paid bills.
 */
class BillsFragment : Fragment() {

    private val vm: BillsViewModel by activityViewModels()
    private lateinit var upcomingAdapter: BillAdapter
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_bills, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyBills)

        // Initialize the adapter with a callback to mark bills as paid
        upcomingAdapter = BillAdapter { bill -> vm.markPaid(bill) }
        view.findViewById<RecyclerView>(R.id.rvBills).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = upcomingAdapter
        }

        // Observe the list of upcoming bills
        vm.upcomingBills.observe(viewLifecycleOwner) { list ->
            upcomingAdapter.submitList(list)
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe the total amount due for upcoming bills
        vm.totalUpcoming.observe(viewLifecycleOwner) { total ->
            view.findViewById<TextView>(R.id.tvBillsTotal).text =
                "R ${"%.2f".format(total ?: 0.0)}"
        }

        view.findViewById<View>(R.id.btnCloseTipBills).setOnClickListener {
            view.findViewById<View>(R.id.layoutTipBills).visibility = View.GONE
        }

        view.findViewById<View>(R.id.fabAddBill).setOnClickListener {
            showAddBillDialog()
        }
    }

    /**
     * Displays a dialog to input and add a new bill.
     */
    private fun showAddBillDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_bill, null)

        val etBillDue = dialogView.findViewById<EditText>(R.id.etBillDue)
        etBillDue.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                etBillDue.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Bill")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name      = dialogView.findViewById<EditText>(R.id.etBillName).text.toString().trim()
                val amountStr = dialogView.findViewById<EditText>(R.id.etBillAmount).text.toString().trim()
                val due       = etBillDue.text.toString().trim()
                val recurring = dialogView.findViewById<EditText>(R.id.etBillRecurring).text.toString()
                    .trim().ifEmpty { "Monthly" }

                val amount = amountStr.toDoubleOrNull()

                // Validate input fields
                when {
                    name.isEmpty()  -> Toast.makeText(requireContext(), "Please enter a bill name", Toast.LENGTH_SHORT).show()
                    amount == null  -> Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                    due.isEmpty()   -> Toast.makeText(requireContext(), "Please enter a due date (yyyy-MM-dd)", Toast.LENGTH_SHORT).show()
                    else -> {
                        // Mark as urgent if the due date is within the next 3 days
                        val isUrgent = try {
                            val dueDate = LocalDate.parse(due, fmt)
                            !dueDate.isAfter(LocalDate.now().plusDays(3))
                        } catch (e: Exception) { false }

                        vm.addBill(
                            Bill(
                                name      = name,
                                dueDate   = due,
                                amount    = amount,
                                recurring = recurring,
                                icon      = "📅",
                                isUrgent  = isUrgent
                            )
                        )
                        Toast.makeText(requireContext(), "✅ Bill added!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
