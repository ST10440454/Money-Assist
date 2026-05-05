package com.moneyassist.app.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.moneyassist.app.R
import com.moneyassist.app.data.entity.ExpenseEntry
import com.moneyassist.app.data.entity.Transaction
import com.moneyassist.app.ui.viewmodel.CategoryViewModel
import com.moneyassist.app.ui.viewmodel.ExpenseViewModel
import com.moneyassist.app.ui.viewmodel.HomeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Fragment for adding a new transaction (income or expense).
 * Supports picking dates, times, categories, and capturing/selecting a photo of a receipt.
 */
class AddExpenseFragment : Fragment() {

    private val expenseVm: ExpenseViewModel by activityViewModels()
    private val categoryVm: CategoryViewModel by activityViewModels()
    private val homeVm: HomeViewModel by activityViewModels()

    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var ivPhoto: ImageView
    private lateinit var btnTypeExpense: MaterialButton
    private lateinit var btnTypeIncome: MaterialButton

    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var currentType = "expense"

    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Launcher for the camera to take a photo of a receipt
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            photoUri?.let {
                ivPhoto.setImageURI(it)
                ivPhoto.visibility = View.VISIBLE
            }
        }
    }

    // Launcher for selecting a photo from the gallery
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
            ivPhoto.setImageURI(it)
            ivPhoto.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_expense, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        etDate = view.findViewById(R.id.etDate)
        etStartTime = view.findViewById(R.id.etStartTime)
        etEndTime = view.findViewById(R.id.etEndTime)
        etDescription = view.findViewById(R.id.etDescription)
        etAmount = view.findViewById(R.id.etAmount)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        ivPhoto = view.findViewById(R.id.ivPhoto)
        btnTypeExpense = view.findViewById(R.id.btnTypeExpense)
        btnTypeIncome = view.findViewById(R.id.btnTypeIncome)

        // Default the date to today
        etDate.setText(LocalDate.now().format(fmt))

        // Set up type selection (Expense vs. Income)
        btnTypeExpense.setOnClickListener { setType("expense") }
        btnTypeIncome.setOnClickListener { setType("income") }

        // Date picker for the transaction date
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Time pickers for start and end times
        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }

        // Populate the category spinner with data from the database
        categoryVm.categories.observe(viewLifecycleOwner) { cats ->
            val names = cats.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        // Image attachment options
        view.findViewById<MaterialButton>(R.id.btnCamera).setOnClickListener { launchCamera() }
        view.findViewById<MaterialButton>(R.id.btnGallery).setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Save action
        view.findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { saveEntry() }
    }

    /** Updates the UI to reflect whether Expense or Income is selected. */
    private fun setType(type: String) {
        currentType = type
        if (type == "expense") {
            btnTypeExpense.setBackgroundColor(requireContext().getColor(R.color.expense_red_bg))
            btnTypeExpense.setTextColor(requireContext().getColor(R.color.expense_red))
            btnTypeIncome.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
            btnTypeIncome.setTextColor(requireContext().getColor(R.color.text_secondary))
        } else {
            btnTypeIncome.setBackgroundColor(requireContext().getColor(R.color.income_green_bg))
            btnTypeIncome.setTextColor(requireContext().getColor(R.color.income_green))
            btnTypeExpense.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
            btnTypeExpense.setTextColor(requireContext().getColor(R.color.text_secondary))
        }
    }

    /** Displays a time picker dialog. */
    private fun showTimePicker(target: EditText) {
        val c = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, h, m ->
            target.setText(String.format("%02d:%02d", h, m))
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    /** Launches the camera intent. */
    private fun launchCamera() {
        val photoFile = createImageFile()
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        photoUri = uri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        cameraLauncher.launch(intent)
    }

    /** Creates a temporary file for the captured image. */
    private fun createImageFile(): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${ts}_", ".jpg", dir).also {
            currentPhotoPath = it.absolutePath
        }
    }

    /** Validates and saves the transaction to the database. */
    private fun saveEntry() {
        val description = etDescription.text.toString().trim()
        val amountStr = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()
        val startTime = etStartTime.text.toString().trim().ifEmpty { "00:00" }
        val endTime = etEndTime.text.toString().trim().ifEmpty { "00:00" }

        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val catName = spinnerCategory.selectedItem?.toString() ?: "Other"
        val catIcon = when (catName.lowercase()) {
            "food" -> "🛒"
            "transport" -> "🚗"
            "entertainment" -> "🎬"
            "health" -> "💪"
            "utilities" -> "💡"
            "salary" -> "💼"
            else -> "📦"
        }

        // Save as a Transaction entity for general tracking
        val tx = Transaction(
            name = description,
            date = date,
            amount = amount,
            type = currentType,
            category = catName,
            icon = catIcon
        )
        homeVm.addTransaction(tx)

        // Also save as an ExpenseEntry for detailed reporting
        val catId = 1 // Simplified; ideally resolved from selection
        val entry = ExpenseEntry(
            date = date,
            startTime = startTime,
            endTime = endTime,
            description = description,
            amount = amount,
            categoryId = catId,
            photoPath = currentPhotoPath
        )
        expenseVm.addEntry(entry)

        Toast.makeText(requireContext(), "✅ Transaction saved!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }
}
