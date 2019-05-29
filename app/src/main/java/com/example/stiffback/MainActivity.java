package com.example.stiffback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.stiffback.databinding.ActivityMainBinding;
import com.example.stiffback.treelineDatabase.AppDatabase;
import com.example.stiffback.treelineDatabase.TreelineDao;
import com.example.stiffback.treelineDatabase.TreelineEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.stiffback.treelineDatabase.AppDatabase.getInstance;


public class MainActivity extends AppCompatActivity {

    private LocationViewModel model;

    private FusedLocationProviderClient mFusedLocationClient;
    private Task<Location> mLocationTask;
    private int PERMISSIONS_REQUEST_FINE_LOCATION = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        // Request permission
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                PERMISSIONS_REQUEST_FINE_LOCATION);


        // Create the location API
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create location Requests
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(5));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                changeLiveData();
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);


        // Get the ViewModel.
        model = ViewModelProviders.of(this).get(LocationViewModel.class);
        // set location request

        // Create the observers which updates the UI.
        // Treeline observer
        final Observer<List<TreelineEntity>> treelineObserver = new Observer<List<TreelineEntity>>() {
            @Override
            public void onChanged(List<TreelineEntity> treelineEntities) {
                Log.i("dick","butt"); // TODO -- Remove this breakpoint
            }
        };


        final Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onChanged(@Nullable final Location location) {
                // Update the UI, in this case, a TextView.
                String latStr = Double.toString(location.getLatitude());
                String lngStr = Double.toString(location.getLongitude());
                String accuracyStr = Double.toString(location.getAccuracy());
                binding.latValue.setText(latStr);
                binding.lngValue.setText(lngStr);
                binding.accuracyValue.setText(accuracyStr);

                // New location so let's query USGS servers again.
                model.updateElevation();
            }
        };

        final Observer<CompassCell> compassObserver = new Observer<CompassCell>() {
            @Override
            public void onChanged(@Nullable final CompassCell compassCell) {
                // Update the UI, in this case, a TextView.
                binding.center.setText(String.format("%.0f", compassCell.cellArr[1][1]));
                binding.north.setText(String.format("%.0f", compassCell.cellArr[2][0]));
                binding.northEast.setText(String.format("%.0f", compassCell.cellArr[2][2]));
                binding.northWest.setText(String.format("%.0f", compassCell.cellArr[2][0]));
                binding.east.setText(String.format("%.0f", compassCell.cellArr[1][2]));
                binding.west.setText(String.format("%.0f", compassCell.cellArr[1][0]));
                binding.southEast.setText(String.format("%.0f", compassCell.cellArr[0][2]));
                binding.south.setText(String.format("%.0f", compassCell.cellArr[0][1]));
                binding.southWest.setText(String.format("%.0f", compassCell.cellArr[0][0]));
            }
        };

        // Create the observer which updates the UI.
        final Observer<Double> aspectObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable final Double aspect) {
                // Update the UI, in this case, a TextView.
                binding.aspectValue.setText(aspect.toString());
            }
        };

        // Create the observer which updates the UI.
        final Observer<Double> slopeObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable final Double slope) {
                // Update the UI, in this case, a TextView.
                binding.slopeValue.setText(slope.toString());
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.getmTreelineEntities().observe(this,treelineObserver);
        model.getmLocation().observe(this, locationObserver);
        model.getmCompass().observe(this,compassObserver);
        model.getmSlope().observe(this,slopeObserver);
        model.getmAspect().observe(this,aspectObserver);

    }

    public void changeLiveData(){



        model.update(mFusedLocationClient);
    }

    public void changeElevation(View view){
        model.updateElevation();
    }
}
