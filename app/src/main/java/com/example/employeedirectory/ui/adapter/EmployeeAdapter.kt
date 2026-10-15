package com.example.employeedirectory.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.employeedirectory.R
import com.example.employeedirectory.data.model.Employee
import com.example.employeedirectory.databinding.ItemEmployeeBinding

/**
 * RecyclerView Adapter for displaying the list of Employees.
 */
class EmployeeAdapter(
    private val onItemClick: (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    private val allEmployees = mutableListOf<Employee>()
    private val displayedEmployees = mutableListOf<Employee>()

    // Array of vibrant avatar colors to give each employee card a distinctive look
    private val avatarColors = intArrayOf(
        R.color.avatar_1,
        R.color.avatar_2,
        R.color.avatar_3,
        R.color.avatar_4,
        R.color.avatar_5,
        R.color.avatar_6,
        R.color.avatar_7
    )

    fun submitList(list: List<Employee>) {
        allEmployees.clear()
        allEmployees.addAll(list)

        displayedEmployees.clear()
        displayedEmployees.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Filters list based on user search query.
     */
    fun filter(query: String) {
        val trimmed = query.trim().lowercase()
        displayedEmployees.clear()

        if (trimmed.isEmpty()) {
            displayedEmployees.addAll(allEmployees)
        } else {
            val filtered = allEmployees.filter { employee ->
                employee.name.lowercase().contains(trimmed) ||
                employee.age.toString().contains(trimmed) ||
                employee.salary.toString().contains(trimmed)
            }
            displayedEmployees.addAll(filtered)
        }
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean = displayedEmployees.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = displayedEmployees[position]
        holder.bind(employee, position)
    }

    override fun getItemCount(): Int = displayedEmployees.size

    inner class EmployeeViewHolder(
        private val binding: ItemEmployeeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(employee: Employee, position: Int) {
            val context = itemView.context

            binding.tvEmployeeName.text = employee.name
            binding.tvEmployeeAge.text = context.getString(R.string.label_age) + ": " + employee.age
            binding.tvEmployeeSalary.text = employee.getFormattedSalary() + " / mo"
            binding.tvAvatarInitial.text = employee.getInitial()

            // Distinctive avatar badge color based on position
            val colorRes = avatarColors[position % avatarColors.size]
            val colorInt = ContextCompat.getColor(context, colorRes)
            binding.avatarContainer.backgroundTintList = ColorStateList.valueOf(colorInt)

            // Click listener opens detail activity
            binding.root.setOnClickListener {
                onItemClick(employee)
            }
        }
    }
}
