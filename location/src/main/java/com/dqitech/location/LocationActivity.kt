package com.dqitech.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dqitech.location.databinding.ActivityLocationBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationAdapter: LocationAdapter
    private val locations = mutableListOf<LocationData>()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Realm.init(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupLocationRequest()
        setupLocationCallback()
        setupRecyclerView()

        binding.btnGetLocation.setOnClickListener {
            requestLocationUpdates()
        }

        binding.btnPlayback.setOnClickListener {
            playbackLocations()
        }

        loadSavedLocations()

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Request location permissions when the activity starts
        requestLocationPermission()
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 900000 // 15 minutes
            fastestInterval = 900000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    saveLocation(location)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter(locations)
        binding.recyclerView.apply {
            adapter = locationAdapter
            layoutManager = LinearLayoutManager(this@LocationActivity)
        }

        locationAdapter.setOnItemClickListener { locationData ->
            val latLng = LatLng(locationData.latitude, locationData.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            val marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Location ${locations.indexOf(locationData) + 1}"))
            marker?.showInfoWindow()
        }
    }

    private fun requestLocationUpdates() {
        if (checkLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestLocationPermission()
        }
    }

    private fun saveLocation(location: Location) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync { realm ->
            val locationData = realm.createObject(LocationData::class.java, UUID.randomUUID().toString())
            locationData.latitude = location.latitude
            locationData.longitude = location.longitude
            locationData.timestamp = Date()
            locations.add(realm.copyFromRealm(locationData))
            runOnUiThread {
                locationAdapter.notifyItemInserted(locations.size - 1)
            }
        }
        realm.close()
    }

    private fun loadSavedLocations() {
        val realm = Realm.getDefaultInstance()
        val results: RealmResults<LocationData> = realm.where(LocationData::class.java).findAll()
        locations.clear()
        locations.addAll(realm.copyFromRealm(results))
        locationAdapter.notifyDataSetChanged()
        realm.close()
    }

    private fun checkLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun playbackLocations() {
        if (locations.isEmpty()) {
            Toast.makeText(this, "No locations recorded", Toast.LENGTH_SHORT).show()
            return
        }

        val handler = Handler(Looper.getMainLooper())
        var index = 0

        val playbackRunnable = object : Runnable {
            override fun run() {
                if (index < locations.size) {
                    val locationData = locations[index]
                    val latLng = LatLng(locationData.latitude, locationData.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    val marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Location ${index + 1}"))
                    marker?.showInfoWindow()
                    index++
                    handler.postDelayed(this, 2000) // 2 seconds delay between markers
                }
            }
        }
        handler.post(playbackRunnable)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
