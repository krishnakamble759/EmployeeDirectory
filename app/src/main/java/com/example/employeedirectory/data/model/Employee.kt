package com.example.employeedirectory.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data model representing an individual Employee.
 * Implements Parcelable so it can be passed smoothly between Activities via Intent.
 */
@Parcelize
data class Employee(
    @SerializedName("name")
    val name: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("salary")
    val salary: Int
) : Parcelable {

    /**
     * Returns the first character of the name capitalized for avatar badges (e.g. "J" for "James").
     */
    fun getInitial(): String {
        return name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }

    /**
     * Formats the monthly salary with comma grouping (e.g. "$17,000").
     */
    fun getFormattedSalary(): String {
        return "$%,d".format(salary)
    }

    /**
     * Calculates and formats estimated annual salary (salary * 12).
     */
    fun getFormattedAnnualSalary(): String {
        return "$%,d".format(salary * 12)
    }

    /**
     * Generates a realistic mock Employee ID (e.g. "EMP-4102").
     */
    fun getEmployeeId(): String {
        val id = kotlin.math.abs(name.hashCode() % 9000) + 1000
        return "EMP-$id"
    }

    /**
     * Generates an employee email address.
     */
    fun getEmail(): String {
        val cleanName = name.lowercase().trim().replace("\\s+".toRegex(), ".")
        return "$cleanName@company.com"
    }
}
