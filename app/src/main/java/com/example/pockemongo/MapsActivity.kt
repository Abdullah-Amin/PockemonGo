package com.example.pockemongo

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pockemongo.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val TAG = "MapsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap = googleMap

        // Add a marker in Sydney and move the camera


        checkPermissions()
        MyThread().start()
    }

    private fun checkPermissions(){

        if(Build.VERSION.SDK_INT >= 23){
            if ((ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
            }else{
                getCurrentLocation()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED){

            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {

        val myLocationListener = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 5f, myLocationListener)
    }

    var mLocation: Location? = null

    inner class MyLocationListener: LocationListener {

        init {
            mLocation = Location("me")
            mLocation!!.latitude = 0.0
            mLocation!!.longitude = 0.0
        }
        override fun onLocationChanged(location: Location) {
            mLocation = location
        }
    }

    inner class MyThread() : Thread() {


        override fun run() {
            super.run()

            try{
                runOnUiThread {
                    mMap.clear()
                    val sydney = LatLng(mLocation!!.latitude, mLocation!!.longitude)
                    mMap.addMarker(MarkerOptions()
                        .position(sydney)
                        .title("Abdullah")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.apparel_image))
                        .snippet("Hi! it's me"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 4f))
                }
            }catch (ex: Exception){
                Log.i(TAG, "run: ${ex.localizedMessage}")
            }

        }
    }
}
