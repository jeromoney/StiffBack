package com.example.stiffback.remoteDataSource

import com.google.gson.annotations.SerializedName

class ElevationValue {
    @SerializedName("USGS_Elevation_Point_Query_Service")
    lateinit var mPointQueryService: PointQueryService

    inner class PointQueryService {
        @SerializedName("Elevation_Query")
        lateinit var mElevationQuery: ElevationQuery

        inner class ElevationQuery {
            @SerializedName("x")
            var mLng: Double? = null
            @SerializedName("y")
            var mLat: Double? = null
            @SerializedName("Data_Source")
            var mDataSource: String? = null
            @SerializedName("Elevation")
            var mElevation: Double? = null
            @SerializedName("Units")
            var mUnits: String? = null
        }
    }
}