package com.example.stiffback.repository;

import android.location.Location;
import android.util.Log;

import com.example.stiffback.LocationViewModel;
import com.example.stiffback.SlopeUtils;
import com.example.stiffback.remoteDataSource.ElevationRetrofitClientInstance;
import com.example.stiffback.remoteDataSource.ElevationService;
import com.example.stiffback.remoteDataSource.ElevationValue;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.stiffback.SlopeUtils.THIRD_ARC_SECOND;

public class LocationRetriever {
    private static final String TAG = LocationRetriever.class.getSimpleName();

    public static void getLastLocation(final LocationViewModel model, FusedLocationProviderClient fusedLocationProviderClient){
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        model.getmLocation().postValue(location);
                    }
                });
    }

    /**
     * Creates 9 cells and calls USGS elevation service to find elevation value
     * @param model
     */
    public static void getElevation(final LocationViewModel model){
        // This method needs to wait until the location is found and not null to activate.
        if (model.getmLocation().getValue() == null) {
            Log.i(TAG,"Location is null");
            return;
        }


        Double lat = model.getmLocation().getValue().getLatitude();
        Double lng = model.getmLocation().getValue().getLongitude();
        for (int i = -1; i<2; i++){
            for (int j = -1; j<2; j++){
                Double newLat = lat + i * THIRD_ARC_SECOND;
                Double newLng = lng + j * THIRD_ARC_SECOND;

                Map<String, String> options = new HashMap<>();
                options.put("x",newLng.toString());
                options.put("y",newLat.toString());
                options.put("output","json");
                options.put("units","Feet");

                // Used to pass integers to the onReponse. Is there a better way?
                final int finalI = i;
                final int finalJ = j;
                ElevationService service = ElevationRetrofitClientInstance.getRetrofitInstance().create(ElevationService.class);
                Call<ElevationValue> call = service.getMyLocation(options);
                call.enqueue(new Callback<ElevationValue>() {
                    @Override
                    public void onResponse(Call<ElevationValue> call, Response<ElevationValue> response) {
                        // Get the response object of our query
                        ElevationValue.PointQueryService.ElevationQuery elevationQuery = response.body().mPointQueryService.mElevationQuery;
                        // Pass it to ViewModel who decides where to place it
                        model.updateElevationValue(elevationQuery, finalI, finalJ);

                    }

                    @Override
                    public void onFailure(Call<ElevationValue> call, Throwable t) {
                        Log.i(TAG, t.getMessage());
                    }
                });



            }
        }
    }


}
