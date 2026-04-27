package com.moneyassist.app.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.ui.adapter.TransactionAdapter
import com.moneyassist.app.ui.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class TransactionListFragment : Fragment() {

    private val vm: TransactionViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_transaction_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyTransactions)

        adapter = TransactionAdapter { tx -> vm.deleteTransaction(tx) }
        view.findViewById<RecyclerView>(R.id.rvTransactions).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TransactionListFragment.adapter
        }

        vm.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        vm.dateRange.observe(viewLifecycleOwner) { (s, e) ->
            view.findViewById<TextView>(R.id.tvDateRange).text = "$s → $e"
        }

        view.findViewById<Button>(R.id.btnStartDate).setOnClickListener {
            showDatePicker { date ->
                val end = vm.dateRange.value?.second ?: LocalDate.now().format(fmt)
                vm.setDateRange(date, end)
            }
        }
        view.findViewById<Button>(R.id.btnEndDate).setOnClickListener {
            showDatePicker { date ->
                val start = vm.dateRange.value?.first ?: LocalDate.now().withDayOfMonth(1).format(fmt)
                vm.setDateRange(start, date)
            }
        }

        view.findViewById<View>(R.id.fabAdd).setOnClickListener {
            findNavController().navigate(R.id.action_transactions_to_addExpense)
        }
    }

    private fun showDatePicker(onDate: (String) -> Unit) {
        val c = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            onDate(LocalDate.of(y, m + 1, d).format(fmt))
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }
}