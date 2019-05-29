package com.example.stiffback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.stiffback.databinding.ActivityMainBinding;
import com.example.stiffback.treelineDatabase.TreelineEntity;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.tasks.Task;

import java.util.List;



public class MainActivity extends AppCompatActivity {
    private int PERMISSIONS_REQUEST_FINE_LOCATION = 1234;

    private LocationViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        // Request permission
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                PERMISSIONS_REQUEST_FINE_LOCATION);



        // Get the ViewModel.
        model = ViewModelProviders.of(this).get(LocationViewModel.class);

        // Create the observers which updates the UI.
        // Treeline observer
        final Observer<TreelineEntity> treelineObserver = new Observer<TreelineEntity>() {
            @Override
            public void onChanged(TreelineEntity treelineEntity) {
                String mountainRange = treelineEntity.getMountainRange();
                int elev = treelineEntity.getTreelineElevation();
                binding.mountainRangeValue.setText(mountainRange);
                binding.mountainRangeTreelineValue.setText(Integer.toString(elev));
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
        model.getmNearestTreeline().observe(this, treelineObserver);
        model.getmLocation().observe(this, locationObserver);
        model.getmCompass().observe(this,compassObserver);
        model.getmSlope().observe(this,slopeObserver);
        model.getmAspect().observe(this,aspectObserver);

    }

    public Activity getActivity(){
        return this;
    }
}
