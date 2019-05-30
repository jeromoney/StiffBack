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

import com.example.stiffback.databinding.ActivityMainBinding;
import com.example.stiffback.treelineDatabase.TreelineEntity;

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

        final Observer<LocationCell> compassObserver = new Observer<LocationCell>() {
            @Override
            public void onChanged(@Nullable final LocationCell compassCell) {

                Location location = compassCell.getmLocation();
                if (location != null) {
                    String latStr = Double.toString(location.getLatitude());
                    String lngStr = Double.toString(location.getLongitude());
                    String accuracyStr = Double.toString(location.getAccuracy());
                    binding.latValue.setText(latStr);
                    binding.lngValue.setText(lngStr);
                    binding.accuracyValue.setText(accuracyStr);
                }
                // Update nearest treeline
                TreelineEntity treelineEntity = compassCell.getmTreelineEntity();
                if (treelineEntity != null){
                    String mountainRange = treelineEntity.getMountainRange();
                    int elev = treelineEntity.getTreelineElevation();
                    binding.mountainRangeValue.setText(mountainRange);
                    binding.mountainRangeTreelineValue.setText(Integer.toString(elev));
                }


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

                // update slope and aspect
                double slope = compassCell.getmSlope();
                binding.slopeValue.setText(Double.toString(slope));
                double aspect = compassCell.getmAspect();
                binding.aspectValue.setText(Double.toString(aspect));
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

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.getmCompass().observe(this,compassObserver);

    }

    public Activity getActivity(){
        return this;
    }
}
