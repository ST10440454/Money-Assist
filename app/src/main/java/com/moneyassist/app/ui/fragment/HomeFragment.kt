package com.moneyassist.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moneyassist.app.R
import com.moneyassist.app.ui.adapter.BillAdapter
import com.moneyassist.app.ui.adapter.MissionAdapter
import com.moneyassist.app.ui.adapter.TransactionAdapter
import com.moneyassist.app.ui.viewmodel.BillsViewModel
import com.moneyassist.app.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private val homeVm: HomeViewModel by activityViewModels()
    private val billsVm: BillsViewModel by activityViewModels()

    private lateinit var txAdapter: TransactionAdapter
    private lateinit var billAdapter: BillAdapter
    private lateinit var missionAdapter: MissionAdapter

    private lateinit var tvEmptyTx: TextView
    private lateinit var tvEmptyBills: TextView
    private lateinit var tvEmptyMissions: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvEmptyTx       = view.findViewById(R.id.tvEmptyTx)
        tvEmptyBills    = view.findViewById(R.id.tvEmptyBills)
        tvEmptyMissions = view.findViewById(R.id.tvEmptyMissions)

        // Recent transactions
        txAdapter = TransactionAdapter()
        view.findViewById<RecyclerView>(R.id.rvRecentTx).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = txAdapter
        }

        // Upcoming bills
        billAdapter = BillAdapter { bill -> billsVm.markPaid(bill) }
        view.findViewById<RecyclerView>(R.id.rvUpcomingBills).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = billAdapter
        }

        // Active missions (compact)
        missionAdapter = MissionAdapter(showContrib = false)
        view.findViewById<RecyclerView>(R.id.rvMissions).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = missionAdapter
        }

        // Observe
        homeVm.recentTransactions.observe(viewLifecycleOwner) { list ->
            txAdapter.submitList(list)
            tvEmptyTx.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        homeVm.upcomingBills.observe(viewLifecycleOwner) { list ->
            billAdapter.submitList(list.take(3))
            tvEmptyBills.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        homeVm.missions.observe(viewLifecycleOwner) { list ->
            missionAdapter.submitList(list.take(2))
            tvEmptyMissions.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        homeVm.netWorth.observe(viewLifecycleOwner) { nw ->
            val amount = nw ?: 0.0
            view.findViewById<TextView>(R.id.tvNetWorth).text =
                "R ${"%,.2f".format(amount).replace(",", " ")}"
        }

        // Navigation
        view.findViewById<TextView>(R.id.tvSeeAllTx).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_transactions)
        }
        view.findViewById<TextView>(R.id.tvSeeAllBills).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_bills)
        }
        view.findViewById<TextView>(R.id.tvSeeAllMissions).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_missions)
        }
        view.findViewById<View>(R.id.fabAdd).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addExpense)
        }
        view.findViewById<View>(R.id.btnCloseTip).setOnClickListener {
            view.findViewById<View>(R.id.layoutTip).visibility = View.GONE
        }
    }
}