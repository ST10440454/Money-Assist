package com.moneyassist.app.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.Mission
import com.moneyassist.app.ui.viewmodel.MissionsViewModel

/**
 * Screen 4 — Missions & Budgeting.
 * Tab 1: Active missions with progress, monthly contribution, add-progress button.
 * Tab 2: Completed missions.
 * FAB → New Mission dialog with live formula preview.
 */
class MissionsFragment : Fragment() {

    private val vm: MissionsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_missions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tabs      = view.findViewById<TabLayout>(R.id.tab_layout)
        val rvActive  = view.findViewById<RecyclerView>(R.id.rv_active_missions)
        val rvDone    = view.findViewById<RecyclerView>(R.id.rv_completed_missions)
        val fab       = view.findViewById<FloatingActionButton>(R.id.fab_new_mission)
        val tvEmpty   = view.findViewById<TextView>(R.id.tv_empty_missions)

        rvActive.layoutManager  = LinearLayoutManager(context)
        rvDone.layoutManager    = LinearLayoutManager(context)

        val activeAdapter = MissionAdapter(mutableListOf(),
            onAddProgress = { mission -> showAddProgressDialog(mission) },
            onDelete = { vm.deleteMission(it) }
        )
        val doneAdapter = MissionAdapter(mutableListOf(),
            onAddProgress = {}, onDelete = { vm.deleteMission(it) }, readOnly = true
        )

        rvActive.adapter = activeAdapter
        rvDone.adapter = doneAdapter

        // ── Tabs ──────────────────────────────────────────────────
        tabs.addTab(tabs.newTab().setText("Active"))
        tabs.addTab(tabs.newTab().setText("Completed"))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                rvActive.visibility = if (tab.position == 0) View.VISIBLE else View.GONE
                rvDone.visibility   = if (tab.position == 1) View.VISIBLE else View.GONE
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // ── Active missions ───────────────────────────────────────
        vm.activeMissions.observe(viewLifecycleOwner) { missions ->
            tvEmpty.visibility = if (missions.isNullOrEmpty()) View.VISIBLE else View.GONE
            activeAdapter.updateData(missions ?: emptyList())
        }

        // ── Completed missions ────────────────────────────────────
        vm.completedMissions.observe(viewLifecycleOwner) { missions ->
            doneAdapter.updateData(missions ?: emptyList())
        }

        // ── Result feedback ───────────────────────────────────────
        vm.saveResult.observe(viewLifecycleOwner) { msg ->
            msg?.let { Snackbar.make(view, it, Snackbar.LENGTH_LONG).show(); vm.clearResult() }
        }

        fab.setOnClickListener { showNewMissionDialog() }
    }

    // ── New Mission dialog with live contribution formula ─────────
    private fun showNewMissionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_mission, null)
        val etName     = dialogView.findViewById<EditText>(R.id.et_mission_name)
        val etTarget   = dialogView.findViewById<EditText>(R.id.et_mission_target)
        val etCurrent  = dialogView.findViewById<EditText>(R.id.et_mission_current)
        val etDeadline = dialogView.findViewById<EditText>(R.id.et_mission_deadline)
        val tvContrib  = dialogView.findViewById<TextView>(R.id.tv_mission_contrib_preview)
        val rgMode     = dialogView.findViewById<RadioGroup>(R.id.rg_budget_mode)

        fun updatePreview() {
            val target  = etTarget.text.toString().toDoubleOrNull() ?: return
            val current = etCurrent.text.toString().toDoubleOrNull() ?: 0.0
            val dl      = etDeadline.text.toString().trim()
            tvContrib.text = "💡 " + vm.calcContribution(target, current, dl)
        }

        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { updatePreview() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        }
        etTarget.addTextChangedListener(watcher)
        etCurrent.addTextChangedListener(watcher)
        etDeadline.addTextChangedListener(watcher)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🎯 New Mission")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name    = etName.text.toString().trim()
                val target  = etTarget.text.toString().toDoubleOrNull() ?: 0.0
                val current = etCurrent.text.toString().toDoubleOrNull() ?: 0.0
                val dl      = etDeadline.text.toString().trim()
                val mode    = if (rgMode.checkedRadioButtonId == R.id.rb_strict) "strict" else "flexible"
                vm.createMission(name, target, current, dl, mode)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddProgressDialog(mission: Mission) {
        val et = EditText(requireContext()).apply {
            hint = "Amount to add (R)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add progress to ${mission.name}")
            .setView(et)
            .setPositiveButton("Add") { _, _ ->
                val amount = et.text.toString().toDoubleOrNull() ?: 0.0
                if (amount > 0) vm.addProgress(mission, amount)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ── Adapter ───────────────────────────────────────────────────
    inner class MissionAdapter(
        private var items: List<Mission>,
        private val onAddProgress: (Mission) -> Unit,
        private val onDelete: (Mission) -> Unit,
        private val readOnly: Boolean = false
    ) : RecyclerView.Adapter<MissionAdapter.VH>() {

        fun updateData(newItems: List<Mission>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvName:    TextView    = v.findViewById(R.id.tv_mission_name)
            val tvTarget:  TextView    = v.findViewById(R.id.tv_mission_target)
            val tvContrib: TextView    = v.findViewById(R.id.tv_monthly_contrib)
            val tvMode:    TextView    = v.findViewById(R.id.tv_budget_mode)
            val progress:  ProgressBar = v.findViewById(R.id.progress_mission)
            val tvPct:     TextView    = v.findViewById(R.id.tv_mission_pct)
            val btnAdd:    Button      = v.findViewById(R.id.btn_add_progress)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_mission, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val m = items[pos]
            val pct = if (m.targetAmount > 0) ((m.currentAmount / m.targetAmount) * 100).toInt().coerceAtMost(100) else 0
            holder.tvName.text    = "${m.icon} ${m.name}"
            holder.tvTarget.text  = "R${"%.2f".format(m.currentAmount)} / R${"%.2f".format(m.targetAmount)}"
            holder.tvContrib.text = "R${"%.2f".format(m.monthlyContrib)}/mo recommended"
            holder.tvMode.text    = if (m.budgetMode == "strict") "🔒 Strict" else "🔓 Flexible"
            holder.progress.progress = pct
            holder.tvPct.text     = "$pct%"
            holder.btnAdd.visibility = if (readOnly) View.GONE else View.VISIBLE
            holder.btnAdd.setOnClickListener { onAddProgress(m) }
            holder.itemView.setOnLongClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete '${m.name}'?")
                    .setPositiveButton("Delete") { _, _ -> onDelete(m) }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }
}
