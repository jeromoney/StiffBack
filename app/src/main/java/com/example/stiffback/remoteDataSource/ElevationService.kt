package com.example.stiffback.remoteDataSource

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 *
 * REMOTE DATA SOURCE
 *
 * Connects to USGS Elevation Point Query Service and returns the elevation of a location
 *
 * Query: https://nationalmap.gov/epqs/pqs.php?y=36.1251958&x=-115.3150863&output=json&units=Meters
 *
 * JSON Result: {"USGS_Elevation_Point_Query_Service":{"Elevation_Query":
 * {"x":-115.3150863,"y":36.1251958,"Data_Source":"3DEP 1\/3 arc-second",
 * "Elevation":853.74,"Units":"Meters"}}}
 *
 */
interface ElevationService {
    @GET("epqs/pqs.php")
    fun getMyLocation(@QueryMap options: Map<String, String>): Call<ElevationValue>
}