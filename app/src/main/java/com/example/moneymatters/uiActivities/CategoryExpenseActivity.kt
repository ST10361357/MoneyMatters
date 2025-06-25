package com.example.moneymatters.uiActivities

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneymatters.data.Category
import com.example.moneymatters.data.Expense
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.database.CategoryDao
import com.example.moneymatters.database.ExpenseDao
import com.example.moneymatters.database.FrozenCategoryDao
import com.example.moneymatters.databinding.ActivityCategoryExpenseBinding
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Environment
import android.util.Log
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import com.example.moneymatters.data.FrozenCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CategoryExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryExpenseBinding

    //  image URI
    private var photoUri: Uri? = null
    private val REQUEST_CAMERA = 100
    private val REQUEST_GALLERY = 200
    private val REQUEST_PERMISSIONS = 101
    private lateinit var categoryDao: CategoryDao
    private lateinit var expenseDao: ExpenseDao

    //
    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (photoUri != null) {
                binding.expenseImage.visibility = View.VISIBLE
                binding.expenseImage.setImageURI(photoUri)
                binding.addPhotoButton.text = "Photo Selected"
            } else {
                showToast("Failed to capture image")
            }
        }

    //
    private val requestGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val imageUri = result.data?.data
            if (imageUri != null) {
                binding.expenseImage.visibility = View.VISIBLE
                binding.expenseImage.setImageURI(imageUri)
                binding.addPhotoButton.text = "Image Selected"
            } else {
                showToast("Failed to select image")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  database instance
        val db = AppDb.getDb(this)

        // initialize DAOs
        categoryDao = db.CategoryDao()
        expenseDao = db.ExpenseDao()

        // setup event listeners
        setupCategorySpinner()
        setupDatePicker()
        setupTimePickers()
        setupPhotoSelection()
        checkPermissions()
        setupSubmitButton()


    }

    //  add new categories
    private fun setupCategorySpinner() {
        categoryDao.getAllCategories().observe(this) { categories ->
            val categoryNames = categories.map { it.categoryName }.toMutableList()
            categoryNames.add("Add new category") // Special option to add a new category

            // spinner adapter
            val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categoryNames)
            binding.categorySpinner.adapter = adapter

            //  category selection
            binding.categorySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Show dialog when user selects "Add new category"
                        if (categoryNames[position] == "Add new category") {
                            showAddCategoryDialog()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
        }
    }
/*
    // new category
    private fun showAddCategoryDialog() {
        val userInput = EditText(this) // Create input field
        AlertDialog.Builder(this)
            .setTitle("Enter new category")
            .setView(userInput)
            .setPositiveButton("Add") { _, _ ->
                val newCategoryName = userInput.text.toString().trim()
                if (newCategoryName.isNotEmpty()) {
                    val newCategory = Category(categoryName = newCategoryName)

                    // Add category to RoomDB
                    lifecycleScope.launch {
                        categoryDao.addCategory(newCategory)
                        Toast.makeText(
                            this@CategoryExpenseActivity,
                            "Category added!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Category name cannot be empty!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }*/
    //udate to freeze spending on ccertain catergories
private fun showAddCategoryDialog() {
    val userInput = EditText(this).apply {
        hint = "e.g., Takeout, Subscriptions"
    }

    val freezeNowCheckBox = CheckBox(this).apply {
        text = "Freeze this category now"
    }

    val startDateEdit = EditText(this).apply {
        hint = "Start Date (dd/MM/yyyy)"
        isEnabled = false
    }

    val endDateEdit = EditText(this).apply {
        hint = "End Date (dd/MM/yyyy)"
        isEnabled = false
    }

    // Enable date pickers only if checkbox is checked
    freezeNowCheckBox.setOnCheckedChangeListener { _, isChecked ->
        startDateEdit.isEnabled = isChecked
        endDateEdit.isEnabled = isChecked
    }

    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(50, 20, 50, 0)
        addView(userInput)
        addView(freezeNowCheckBox)
        addView(startDateEdit)
        addView(endDateEdit)
    }

    // Date picker dialogs
    fun pickDate(target: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            target.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    startDateEdit.setOnClickListener { pickDate(startDateEdit) }
    endDateEdit.setOnClickListener { pickDate(endDateEdit) }

    AlertDialog.Builder(this)
        .setTitle("Enter new category")
        .setView(layout)
        .setPositiveButton("Add") { _, _ ->
            val newCategoryName = userInput.text.toString().trim()

            if (newCategoryName.isNotEmpty()) {
                val newCategory = Category(categoryName = newCategoryName)

                lifecycleScope.launch {
                    categoryDao.addCategory(newCategory)

                    if (freezeNowCheckBox.isChecked &&
                        startDateEdit.text.isNotEmpty() &&
                        endDateEdit.text.isNotEmpty()) {

                        val freeze = FrozenCategory(
                            categoryName = newCategoryName,
                            startDate = startDateEdit.text.toString(),
                            endDate = endDateEdit.text.toString()
                        )

                        val db = AppDb.getDb(this@CategoryExpenseActivity)
                        db.FrozenCategoryDao().freezeCategory(freeze)
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CategoryExpenseActivity,
                            "Category added${if (freezeNowCheckBox.isChecked) " and frozen" else ""}!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Category name cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton("Cancel", null)
        .show()
}



    // date picker
    private fun setupDatePicker() {
        binding.expenseDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    // Format and display selected date
                    binding.expenseDateButton.text = "$day/${month + 1}/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    // time picker -start
    private fun setupTimePickers() {
        val calendar = Calendar.getInstance()

        // Time Picker for Start Time
        binding.expenseStartTimeButton.setOnClickListener {
            showTimePicker { hour, minute ->
                binding.expenseStartTimeButton.text = String.format("%02d:%02d", hour, minute)
            }
        }

        // Time Picker for End Time
        binding.expenseEndTimeButton.setOnClickListener {
            showTimePicker { hour, minute ->
                binding.expenseEndTimeButton.text = String.format("%02d:%02d", hour, minute)
            }
        }
    }

    // Generic Time Picker Function
    private fun showTimePicker(onTimeSelected: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _, hour, minute -> onTimeSelected(hour, minute) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Use 24-hour format
        )
        timePicker.show()
    }


    //  take a pic or choose from gallery
    private fun setupPhotoSelection() {
        binding.addPhotoButton.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery")
            AlertDialog.Builder(this)
                .setTitle("Select an option")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> openCamera() // Capture a new photo
                        1 -> openGallery() // Choose an existing photo
                    }
                }
                .show()
        }
    }

    //  capture pic
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        requestCamera.launch(intent)
    }

    // Function to create an image file
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("expense_photo", ".jpg", storageDir)
    }

    // select an pic
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        requestGallery.launch(intent)
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE // Needed for saving images
        )

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            showToast("Permissions granted!")
        } else {
            showToast("Permissions denied! Please enable them in settings.")
        }
    }


    // image selection from camera or gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    if (photoUri != null) {
                        binding.expenseImage.setImageURI(photoUri) // Display the captured image
                        binding.addPhotoButton.text = "Photo Selected"
                    } else {
                        Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                    }
                }

                REQUEST_GALLERY -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        binding.expenseImage.setImageURI(imageUri) // Display selected image
                        binding.addPhotoButton.text = "Image Selected"
                    } else {
                        Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //epense to db
   /* private fun insertExpense() {
        val expenseName = binding.expenseName.text.toString()
        val description = binding.expenseDescription.text.toString()
        val amount = binding.expenseAmount.text.toString().toDoubleOrNull() ?: 0.0
        val selectedDate = binding.expenseDateButton.text.toString()
        val startTime = binding.expenseStartTimeButton.text.toString()
        val endTime = binding.expenseEndTimeButton.text.toString()
        val selectedCategory = binding.categorySpinner.selectedItem.toString()

        // Validate required fields
        if (expenseName.isEmpty() || description.isEmpty() || selectedCategory.isEmpty()) {
            showToast("Please fill in all required fields!")
            return
        }

        lifecycleScope.launch (Dispatchers.IO){
            try {
                var categoryName = categoryDao.getCategoryId(selectedCategory)

                if (categoryName == null) {
                    Log.d("ExpenseDebug", "Adding new category: $selectedCategory")
                    categoryDao.addCategory(Category(categoryName = selectedCategory))

                    categoryName = categoryDao.getCategoryId(selectedCategory)
                    if (categoryName == null) {
                        showToast("Error: Failed to retrieve category ID after insertion!")
                        Log.e("ExpenseDebug", "Category insertion failed for: $selectedCategory")
                        return@launch
                    }
                }

                // Create the Expense object
                val expense = Expense(
                    expenseName = expenseName,
                    description = description,
                    amount = amount,
                    date = selectedDate,
                    startTime = startTime,
                    endTime = endTime,
                    photo = photoUri?.toString(), // Store the image URI in RoomDB
                    categoryId = categoryName
                )

                Log.d("ExpenseDebug", "Attempting to insert expense: $expense")
                expenseDao.addExpense(expense)

                // Confirm insertion
                val allExpenses = expenseDao.getAllExpenses()
                Log.d("ExpenseDebug", "Total expenses after insertion: ${allExpenses}")

                withContext(Dispatchers.Main) {
                    showToast("Expense added successfully!")

                    // Navigate to View Nav screen
                    val intent = Intent(this@CategoryExpenseActivity, NavMenuActivity::class.java)
                    startActivity(intent)
                    //finish() // Close the activity
                }
            } catch (e: Exception) {
                Log.e("ExpenseDebug", "Error inserting expense", e)
                showToast("Failed to add expense. Please try again.")
            }
        }
    }

        private fun setupSubmitButton() {
        binding.buttonSubmitExpense.setOnClickListener {
            Log.d("ExpenseDebug", "Submit button clicked!")
            Toast.makeText(this, "Submit button clicked!", Toast.LENGTH_SHORT).show()
            insertExpense()
        }
    }*/


    //insert expense with frozen category considered

    private fun insertExpense() {
        val expenseName = binding.expenseName.text.toString()
        val description = binding.expenseDescription.text.toString()
        val amount = binding.expenseAmount.text.toString().toDoubleOrNull() ?: 0.0
        val selectedDate = binding.expenseDateButton.text.toString()
        val startTime = binding.expenseStartTimeButton.text.toString()
        val endTime = binding.expenseEndTimeButton.text.toString()
        val selectedCategory = binding.categorySpinner.selectedItem.toString()

        // Validate required fields
        if (expenseName.isEmpty() || description.isEmpty() || selectedCategory.isEmpty()) {
            showToast("Please fill in all required fields!")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // ❄️ Check if selected category is frozen for today
                val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val db = AppDb.getDb(this@CategoryExpenseActivity)
                val frozen = db.FrozenCategoryDao().isFrozen(selectedCategory, today)

                if (frozen != null) {
                    withContext(Dispatchers.Main) {
                        showToast("🚫 '$selectedCategory' is currently frozen. Expense not allowed.")
                    }
                    return@launch
                }

                // ✅ Proceed with category lookup
                var categoryId = categoryDao.getCategoryId(selectedCategory)
                if (categoryId == null) {
                    Log.d("ExpenseDebug", "Adding new category: $selectedCategory")
                    categoryDao.addCategory(Category(categoryName = selectedCategory))

                    categoryId = categoryDao.getCategoryId(selectedCategory)
                    if (categoryId == null) {
                        withContext(Dispatchers.Main) {
                            showToast("Error: Failed to retrieve category ID after insertion!")
                        }
                        Log.e("ExpenseDebug", "Category insertion failed for: $selectedCategory")
                        return@launch
                    }
                }

                // Create the Expense object
                val expense = Expense(
                    expenseName = expenseName,
                    description = description,
                    amount = amount,
                    date = selectedDate,
                    startTime = startTime,
                    endTime = endTime,
                    photo = photoUri?.toString(),
                    categoryId = categoryId
                )

                Log.d("ExpenseDebug", "Attempting to insert expense: $expense")
                expenseDao.addExpense(expense)

                val allExpenses = expenseDao.getAllExpenses()
                Log.d("ExpenseDebug", "Total expenses after insertion: $allExpenses")

                withContext(Dispatchers.Main) {
                    showToast("Expense added successfully!")
                    val intent = Intent(this@CategoryExpenseActivity, NavMenuActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e("ExpenseDebug", "Error inserting expense", e)
                withContext(Dispatchers.Main) {
                    showToast("Failed to add expense. Please try again.")
                }
            }
        }
    }

    private fun setupSubmitButton() {
        binding.buttonSubmitExpense.setOnClickListener {
            Log.d("ExpenseDebug", "Submit button clicked!")

            val selectedCategory = binding.categorySpinner.selectedItem.toString()
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            lifecycleScope.launch {
                val db = AppDb.getDb(this@CategoryExpenseActivity)
                val frozen = db.FrozenCategoryDao().isFrozen(selectedCategory, today)

                if (frozen != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CategoryExpenseActivity,
                            "🚫 '$selectedCategory' is currently frozen. Expense not allowed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@launch
                }

                // Not frozen → proceed
                insertExpense()
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}




