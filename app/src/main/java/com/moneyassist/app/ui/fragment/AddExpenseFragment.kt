package com.moneyassist.app.ui.fragment

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.moneyassist.app.R
import com.moneyassist.app.data.db.AppDatabase
import com.moneyassist.app.data.entity.ExpenseEntry
import com.moneyassist.app.engine.PointsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddExpenseFragment : Fragment() {

    private val args: AddExpenseFragmentArgs by navArgs()
    private var editEntry: ExpenseEntry? = null
    private var selectedCategoryId: Int = 1
    private var selectedDate: String = LocalDate.now().toString()
    private var isIncomeMode = false
    private var photoUri: Uri? = null

    private val pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photoUri = it; view?.findViewById<ImageView>(R.id.ivPhoto)?.apply { setImageURI(it); visibility = View.VISIBLE } }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_add_expense, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getInstance(requireContext())

        val etAmount      = view.findViewById<TextInputEditText>(R.id.etAmount)
        val etDescription = view.findViewById<TextInputEditText>(R.id.etDescription)
        val etDate        = view.findViewById<TextInputEditText>(R.id.etDate)
        val spinnerCat    = view.findViewById<Spinner>(R.id.spinnerCategory)
        val btnTypeExp    = view.findViewById<MaterialButton>(R.id.btnTypeExpense)
        val btnTypeInc    = view.findViewById<MaterialButton>(R.id.btnTypeIncome)
        val btnSave       = view.findViewById<MaterialButton>(R.id.btnSave)
        val btnCamera     = view.findViewById<MaterialButton>(R.id.btnCamera)
        val btnGallery    = view.findViewById<MaterialButton>(R.id.btnGallery)

        etDate.setText(selectedDate)

        fun setMode(income: Boolean) {
            isIncomeMode = income
            btnTypeExp.alpha = if (income) 0.5f else 1f
            btnTypeInc.alpha = if (income) 1f else 0.5f
        }
        btnTypeExp.setOnClickListener { setMode(false) }
        btnTypeInc.setOnClickListener { setMode(true) }
        setMode(isIncomeMode)

        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedDate = "%04d-%02d-%02d".format(y, m + 1, d)
                etDate.setText(selectedDate)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnGallery.setOnClickListener { pickPhoto.launch("image/*") }
        btnCamera.setOnClickListener  { pickPhoto.launch("image/*") }

        db.categoryDao().getAllCategories().observe(viewLifecycleOwner) { categories ->
            if (categories.isEmpty()) return@observe
            val names = categories.map { it.name }
            spinnerCat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)

            spinnerCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                    selectedCategoryId = categories[pos].id
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            editEntry?.let { entry ->
                val idx = categories.indexOfFirst { it.id == entry.categoryId }
                if (idx >= 0) spinnerCat.setSelection(idx)
            }
        }

        if (args.entryId != -1) {
            lifecycleScope.launch {
                val entry = withContext(Dispatchers.IO) { db.expenseEntryDao().getById(args.entryId) }
                entry?.let {
                    editEntry = it
                    etAmount.setText(it.amount.toString())
                    etDescription.setText(it.description)
                    selectedDate = it.date
                    etDate.setText(it.date)
                    setMode(it.isIncome)

                    val adapter = spinnerCat.adapter as? ArrayAdapter<String>
                    if (adapter != null) {
                        // BUG FIX: original called `db.categoryDao().getCategoryById(it.categoryId)`
                        // directly on the Main dispatcher — Room suspend DAOs must run on an IO
                        // thread or Room throws "Cannot access database on the main thread".
                        // Wrapped in withContext(Dispatchers.IO) to fix this.
                        val cat = withContext(Dispatchers.IO) {
                            db.categoryDao().getCategoryById(it.categoryId)
                        }
                        cat?.let { c ->
                            for (i in 0 until adapter.count) {
                                if (adapter.getItem(i) == c.name) {
                                    spinnerCat.setSelection(i)
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val amountStr   = etAmount.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (amountStr.isBlank()) { etAmount.error = "Enter an amount"; return@setOnClickListener }
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) { etAmount.error = "Invalid amount"; return@setOnClickListener }
            if (description.isBlank()) { etDescription.error = "Enter a description"; return@setOnClickListener }

            val now   = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            val isNew = editEntry == null

            val entry = (editEntry ?: ExpenseEntry(
                date = selectedDate, startTime = now, endTime = now,
                description = description, amount = amount,
                categoryId = selectedCategoryId, isIncome = isIncomeMode,
                isSynced = false
            )).copy(
                date = selectedDate,
                description = description,
                amount = amount,
                categoryId = selectedCategoryId,
                isIncome = isIncomeMode,
                photoPath = photoUri?.toString() ?: editEntry?.photoPath,
                isSynced = false
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (isNew) db.expenseEntryDao().insertEntry(entry)
                    else db.expenseEntryDao().updateEntry(entry)
                }
                if (isNew) PointsManager.onTransactionLogged(requireContext())
                findNavController().popBackStack()
            }
        }
    }
}