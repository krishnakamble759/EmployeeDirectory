package com.example.employeedirectory.data.api

import com.example.employeedirectory.data.model.EmployeeResponse
import retrofit2.http.GET

/**
 * Retrofit interface defining endpoints for Employee API.
 */
interface ApiService {

    /**
     * Fetches employee directory from the given endpoint:
     * https://aamras.com/dummy/EmployeeDetails.json
     */
    @GET("dummy/EmployeeDetails.json")
    suspend fun getEmployees(): EmployeeResponse
}
