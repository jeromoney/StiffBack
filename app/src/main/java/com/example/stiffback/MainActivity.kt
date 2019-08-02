package com.example.stiffback

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.databinding.ViewDataBinding

import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_FINE_LOCATION = 1234

    private var model: LocationViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_main)


        // Request permission
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET),
                PERMISSIONS_REQUEST_FINE_LOCATION)


        // Get the ViewModel.
        model = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        // Create the observers which updates the UI.
        val locationObserver = Observer<Location> { location ->
            if (location != null) {
                val latStr = java.lang.Double.toString(location.latitude)
                val lngStr = java.lang.Double.toString(location.longitude)
                val accuracyStr = java.lang.Double.toString(location.accuracy.toDouble())
                binding.root.latValue.setText(latStr)
                binding.root.lngValue.setText(lngStr)
                binding.root.accuracyValue.setText(accuracyStr)
            }
        }

        val compassObserver = Observer<LocationCell> { compassCell ->
            // Update nearest treeline
            val treelineEntity = compassCell!!.mTreelineEntity
            if (treelineEntity != null) {
                val mountainRange = treelineEntity.mountainRange
                val elev = treelineEntity.treelineElevation
                binding.root.mountainRangeValue.setText(mountainRange)
                binding.root.mountainRangeTreelineValue.setText(Integer.toString(elev))
            }


            // Update the UI, in this case, a TextView.
            binding.root.center.setText(String.format("%.0f", compassCell.cellArr[1][1]))
            binding.root.north.setText(String.format("%.0f", compassCell.cellArr[2][0]))
            binding.root.northEast.setText(String.format("%.0f", compassCell.cellArr[2][2]))
            binding.root.northWest.setText(String.format("%.0f", compassCell.cellArr[2][0]))
            binding.root.east.setText(String.format("%.0f", compassCell.cellArr[1][2]))
            binding.root.west.setText(String.format("%.0f", compassCell.cellArr[1][0]))
            binding.root.southEast.setText(String.format("%.0f", compassCell.cellArr[0][2]))
            binding.root.south.setText(String.format("%.0f", compassCell.cellArr[0][1]))
            binding.root.southWest.setText(String.format("%.0f", compassCell.cellArr[0][0]))

            // update slope and aspect
            val slope = compassCell.mSlope
            binding.root.slopeValue.setText(java.lang.Double.toString(slope))
            val aspect = compassCell.mAspect
            binding.root.aspectValue.setText(java.lang.Double.toString(aspect))
        }


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model!!.getmLocationCell().observe(this, compassObserver)
        model!!.getmLocation().observe(this, locationObserver)

    }

    override fun onPause() {
        super.onPause()
        // Stop receiving location updates when app is not visible
        model!!.pauseLocationUpdate()
    }

    override fun onResume() {
        super.onResume()
        // Start receiving location updates
        model!!.resumeLocationUpdate()
    }

}
