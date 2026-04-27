package com.moneyassist.app.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.Mission
import com.moneyassist.app.ui.adapter.BudgetAdapter
import com.moneyassist.app.ui.adapter.MissionAdapter
import com.moneyassist.app.ui.viewmodel.MissionsViewModel
import java.util.*

class MissionsFragment : Fragment() {

    private val vm: MissionsViewModel by activityViewModels()
    private lateinit var missionAdapter: MissionAdapter
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_missions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvEmptyMissions = view.findViewById<TextView>(R.id.tvEmptyMissions)
        val tvEmptyBudget   = view.findViewById<TextView>(R.id.tvEmptyBudget)

        missionAdapter = MissionAdapter(showContrib = true)
        budgetAdapter  = BudgetAdapter()

        view.findViewById<RecyclerView>(R.id.rvMissions).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = missionAdapter
        }
        view.findViewById<RecyclerView>(R.id.rvBudget).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = budgetAdapter
        }

        vm.missions.observe(viewLifecycleOwner) { list ->
            missionAdapter.submitList(list)
            tvEmptyMissions.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        vm.budgetCategories.observe(viewLifecycleOwner) { list ->
            budgetAdapter.submitList(list)
            tvEmptyBudget.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // Tab switching
        val btnMissions = view.findViewById<MaterialButton>(R.id.btnTabMissions)
        val btnBudget   = view.findViewById<MaterialButton>(R.id.btnTabBudget)
        val rvMissions  = view.findViewById<RecyclerView>(R.id.rvMissions)
        val rvBudget    = view.findViewById<RecyclerView>(R.id.rvBudget)
        val headerRow   = view.findViewById<View>(R.id.rowMissionsHeader)

        fun selectMissionsTab() {
            rvMissions.visibility          = View.VISIBLE
            rvBudget.visibility            = View.GONE
            tvEmptyMissions.visibility     = if (missionAdapter.itemCount == 0) View.VISIBLE else View.GONE
            tvEmptyBudget.visibility       = View.GONE
            headerRow.visibility           = View.VISIBLE
            btnMissions.setBackgroundColor(requireContext().getColor(R.color.purple_primary))
            btnMissions.setTextColor(requireContext().getColor(R.color.white))
            btnBudget.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
            btnBudget.setTextColor(requireContext().getColor(R.color.text_secondary))
        }

        fun selectBudgetTab() {
            rvMissions.visibility          = View.GONE
            rvBudget.visibility            = View.VISIBLE
            tvEmptyMissions.visibility     = View.GONE
            tvEmptyBudget.visibility       = if (budgetAdapter.itemCount == 0) View.VISIBLE else View.GONE
            headerRow.visibility           = View.GONE
            btnBudget.setBackgroundColor(requireContext().getColor(R.color.purple_primary))
            btnBudget.setTextColor(requireContext().getColor(R.color.white))
            btnMissions.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
            btnMissions.setTextColor(requireContext().getColor(R.color.text_secondary))
        }

        btnMissions.setOnClickListener { selectMissionsTab() }
        btnBudget.setOnClickListener   { selectBudgetTab() }

        view.findViewById<View>(R.id.btnCloseTipMissions).setOnClickListener {
            view.findViewById<View>(R.id.layoutTipMissions).visibility = View.GONE
        }

        view.findViewById<View>(R.id.btnNewMission).setOnClickListener {
            showNewMissionDialog()
        }
    }

    private fun showNewMissionDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_mission, null)

        val etMissionDeadline = dialogView.findViewById<EditText>(R.id.etMissionDeadline)
        etMissionDeadline.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                etMissionDeadline.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("New Mission")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name     = dialogView.findViewById<EditText>(R.id.etMissionName).text.toString().trim()
                val target   = dialogView.findViewById<EditText>(R.id.etMissionTarget).text.toString().toDoubleOrNull()
                val deadline = etMissionDeadline.text.toString().trim()
                val isSavings = dialogView.findViewById<RadioButton>(R.id.rbSavings).isChecked

                when {
                    name.isEmpty()    -> Toast.makeText(requireContext(), "Enter a mission name", Toast.LENGTH_SHORT).show()
                    target == null    -> Toast.makeText(requireContext(), "Enter a valid target amount", Toast.LENGTH_SHORT).show()
                    deadline.isEmpty() -> Toast.makeText(requireContext(), "Enter a deadline (yyyy-MM-dd)", Toast.LENGTH_SHORT).show()
                    else -> {
                        val icon = if (isSavings) "🎯" else "💳"
                        val type = if (isSavings) "savings" else "debt"
                        vm.addMission(
                            Mission(
                                name          = name,
                                target        = target,
                                current       = 0.0,
                                deadline      = deadline,
                                type          = type,
                                icon          = icon,
                                monthlyContrib = 0.0
                            )
                        )
                        Toast.makeText(requireContext(), "✅ Mission created!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
