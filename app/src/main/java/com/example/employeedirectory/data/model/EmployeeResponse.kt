package com.example.employeedirectory.data.model

import com.google.gson.annotations.SerializedName

/**
 * Root wrapper response from https://aamras.com/dummy/EmployeeDetails.json
 */
data class EmployeeResponse(
    @SerializedName("employees")
    val employees: List<Employee> = emptyList()
)
