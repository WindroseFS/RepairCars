package com.thorapps.repaircars.repository

import com.thorapps.repaircars.network.ApiService
import com.thorapps.repaircars.network.models.ApiLocation
import com.thorapps.repaircars.network.models.CreateLocationRequest
import retrofit2.Response
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getLocations(): Response<List<ApiLocation>> {
        return apiService.getLocations()
    }

    suspend fun createLocation(location: CreateLocationRequest): Response<ApiLocation> {
        return apiService.createLocation(location)
    }

    suspend fun getLocationById(id: String): Response<ApiLocation> {
        return apiService.getLocationById(id)
    }
}