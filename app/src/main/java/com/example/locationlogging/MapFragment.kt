package com.example.locationlogging

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var currentLatLng: LatLng
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapView = view.findViewById(R.id.mapView) as MapView

        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        // Register the map callback, Will get notified when Map ready, then call onMapReady()
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation // update lastLocation

                currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)

                Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show()

                map.addMarker(MarkerOptions().position(currentLatLng).title("Here you are"))


            }
        }
        createLocationRequest()
        return view
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        Toast.makeText(context, "map ready", Toast.LENGTH_SHORT).show()
        checkPermission()
    }

    private fun checkPermission() {
        // 23 was first time Android introduced the permissions (Udemy Section 16.88)
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // show permission request dialog (user response handled by onRequestPermissionsResult())
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        permissionGranted()
    }

    // Handling user response to the permission request dialog
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()

                    permissionGranted()

                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun permissionGranted() {

        Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            // Got last known location. In some rare situations this can be null.

            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)

                // map.moveCamera(CameraUpdateFactory.newLatLng(newYork))
                // zoom level: 0 (default, fully zoom out) ~ 20
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                map.addMarker(MarkerOptions().position(currentLatLng).title("Here you are"))

                createLocationRequest()

            } else {
                Toast.makeText(context, "Location null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createLocationRequest() {

        /* https://developer.android.com/training/location/change-location-settings */
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // 1. Get current location settings
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())

        // 2. Check whether the current location settings are satisfied
        //  task success means all is well and you can go ahead and initiate a location request
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        // Task failure means the location settings have some issues which can be fixed
        // e.g. user’s location settings turned off.
        task.addOnFailureListener { e ->

            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    // Handling user response to the GPS request dialog
    // (override AppCompatActivity.onActivityResult())
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()

                Toast.makeText(activity, "GPS got", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
