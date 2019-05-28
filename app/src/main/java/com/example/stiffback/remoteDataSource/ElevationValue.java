package com.example.stiffback.remoteDataSource;

import com.google.gson.annotations.SerializedName;

public class ElevationValue {
    @SerializedName("USGS_Elevation_Point_Query_Service")
    public PointQueryService mPointQueryService;

    public class PointQueryService{
        @SerializedName("Elevation_Query")
        public ElevationQuery mElevationQuery;

        public class ElevationQuery{
            @SerializedName("x")
            public Double mLng;
            @SerializedName("y")
            public Double mLat;
            @SerializedName("Data_Source")
            public String mDataSource;
            @SerializedName("Elevation")
            public Double mElevation;
            @SerializedName("Units")
            public String mUnits;
        }
    }
}