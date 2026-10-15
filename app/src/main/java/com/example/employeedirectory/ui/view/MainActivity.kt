package com.example.employeedirectory.ui.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.employeedirectory.data.api.RetrofitClient
import com.example.employeedirectory.data.model.Employee
import com.example.employeedirectory.data.model.EmployeeResponse
import com.example.employeedirectory.databinding.ActivityMainBinding
import com.example.employeedirectory.ui.adapter.EmployeeAdapter
import com.example.employeedirectory.utils.NetworkUtils
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 * Main Activity that fetches employee list from API (https://aamras.com/dummy/EmployeeDetails.json),
 * displays in RecyclerView, supports live search filtering, pull-to-refresh,
 * and passes the selected employee to EmployeeDetailActivity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "employee_directory_prefs"
        private const val KEY_CACHED_EMPLOYEES = "cached_employees_json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setupRecyclerView()
        setupSearch()
        setupListeners()

        // Initial fetch
        fetchEmployees()
    }

    private fun setupRecyclerView() {
        employeeAdapter = EmployeeAdapter { selectedEmployee ->
            navigateToDetail(selectedEmployee)
        }

        binding.rvEmployees.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = employeeAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupListeners() {
        // Pull-to-refresh
        binding.swipeRefresh.setOnRefreshListener {
            fetchEmployees(isRefreshing = true)
        }

        // Error retry button
        binding.btnRetry.setOnClickListener {
            fetchEmployees()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty()
                binding.ivClearSearch.isVisible = query.isNotEmpty()
                employeeAdapter.filter(query)

                // Show empty search state if query matches nothing
                val hasResults = !employeeAdapter.isEmpty()
                binding.layoutEmpty.isVisible = !hasResults && query.isNotEmpty()
                binding.rvEmployees.isVisible = hasResults
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }
    }

    private fun fetchEmployees(isRefreshing: Boolean = false) {
        if (!isRefreshing) {
            showLoadingState()
        }

        lifecycleScope.launch {
            try {
                // Call Retrofit API endpoint
                val response: EmployeeResponse = RetrofitClient.apiService.getEmployees()
                val list = response.employees

                // Cache to SharedPreferences for offline resilience
                saveToCache(list)

                showSuccessState(list)
            } catch (e: Exception) {
                e.printStackTrace()

                // Check if we have offline cached employees
                val cached = loadFromCache()
                if (cached.isNotEmpty()) {
                    showSuccessState(cached)
                    Toast.makeText(
                        this@MainActivity,
                        "Showing cached data (Offline mode)",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val errorMsg = if (!NetworkUtils.isNetworkAvailable(this@MainActivity)) {
                        "No internet connection detected. Please connect and tap Retry."
                    } else {
                        "Failed to connect to server: ${e.localizedMessage ?: "Unknown error"}"
                    }
                    showErrorState(errorMsg)
                }
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun showLoadingState() {
        binding.layoutLoading.isVisible = true
        binding.layoutError.isVisible = false
        binding.layoutEmpty.isVisible = false
        binding.rvEmployees.isVisible = false
    }

    private fun showSuccessState(employees: List<Employee>) {
        binding.layoutLoading.isVisible = false
        binding.layoutError.isVisible = false

        employeeAdapter.submitList(employees)

        // Reset search bar if any filter was active
        val currentQuery = binding.etSearch.text?.toString().orEmpty()
        if (currentQuery.isNotEmpty()) {
            employeeAdapter.filter(currentQuery)
            binding.layoutEmpty.isVisible = employeeAdapter.isEmpty()
            binding.rvEmployees.isVisible = !employeeAdapter.isEmpty()
        } else {
            binding.layoutEmpty.isVisible = employees.isEmpty()
            binding.rvEmployees.isVisible = employees.isNotEmpty()
        }
    }

    private fun showErrorState(message: String) {
        binding.layoutLoading.isVisible = false
        binding.rvEmployees.isVisible = false
        binding.layoutEmpty.isVisible = false
        binding.layoutError.isVisible = true
        binding.tvErrorMessage.text = message
    }

    private fun navigateToDetail(employee: Employee) {
        val intent = Intent(this, EmployeeDetailActivity::class.java).apply {
            putExtra(EmployeeDetailActivity.EXTRA_EMPLOYEE, employee)
        }
        startActivity(intent)
    }

    private fun saveToCache(employees: List<Employee>) {
        val json = gson.toJson(employees)
        sharedPrefs.edit().putString(KEY_CACHED_EMPLOYEES, json).apply()
    }

    private fun loadFromCache(): List<Employee> {
        val json = sharedPrefs.getString(KEY_CACHED_EMPLOYEES, null) ?: return emptyList()
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<Employee>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
