package com.example.employeedirectory.ui.view

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.employeedirectory.R
import com.example.employeedirectory.data.model.Employee
import com.example.employeedirectory.databinding.ActivityEmployeeDetailBinding

/**
 * Detail screen displaying individual employee profile, personal details,
 * compensation, and quick contact actions.
 */
class EmployeeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeDetailBinding
    private var employee: Employee? = null

    companion object {
        const val EXTRA_EMPLOYEE = "EXTRA_EMPLOYEE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve Employee Parcelable from Intent
        employee = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_EMPLOYEE, Employee::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EMPLOYEE)
        }

        if (employee == null) {
            Toast.makeText(this, "Employee details unavailable", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        bindEmployeeData(employee!!)
        setupActionButtons(employee!!)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun bindEmployeeData(emp: Employee) {
        // Hero Header
        binding.tvDetailAvatarInitial.text = emp.getInitial()
        binding.tvDetailNameHeader.text = emp.name

        // Set avatar background color based on name hash
        val avatarColors = intArrayOf(
            R.color.avatar_1, R.color.avatar_2, R.color.avatar_3,
            R.color.avatar_4, R.color.avatar_5, R.color.avatar_6, R.color.avatar_7
        )
        val colorIndex = kotlin.math.abs(emp.name.hashCode()) % avatarColors.size
        val colorInt = ContextCompat.getColor(this, avatarColors[colorIndex])
        binding.avatarContainer.backgroundTintList = ColorStateList.valueOf(colorInt)

        // Personal Information
        binding.tvDetailFullName.text = emp.name
        binding.tvDetailAge.text = "${emp.age} years old"
        binding.tvDetailEmployeeId.text = emp.getEmployeeId()

        // Compensation
        binding.tvDetailMonthlySalary.text = "${emp.getFormattedSalary()} / mo"
        binding.tvDetailAnnualSalary.text = "${emp.getFormattedAnnualSalary()} / yr"
    }

    private fun setupActionButtons(emp: Employee) {
        // Email action button
        binding.btnSendEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${emp.getEmail()}")
                putExtra(Intent.EXTRA_SUBJECT, "Regarding Employee: ${emp.name} (${emp.getEmployeeId()})")
            }
            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            } catch (e: Exception) {
                Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show()
            }
        }

        // Share action button
        binding.btnShare.setOnClickListener {
            val shareText = """
                Employee Details:
                Name: ${emp.name}
                Age: ${emp.age}
                Monthly Salary: ${emp.getFormattedSalary()}
                Annual Salary: ${emp.getFormattedAnnualSalary()}
                ID: ${emp.getEmployeeId()}
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Employee Profile - ${emp.name}")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Employee Profile"))
        }
    }
}
