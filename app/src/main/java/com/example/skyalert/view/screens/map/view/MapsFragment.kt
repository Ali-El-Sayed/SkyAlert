package com.example.skyalert.view.screens.map.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentMapBinding
import com.example.skyalert.model.Coord
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.services.alarm.AndroidAlarmScheduler
import com.example.skyalert.services.alarm.model.AlarmItem
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.view.dialogs.MapBottomSheet
import com.example.skyalert.view.screens.map.viewModel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.time.LocalDateTime

private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), OnMapReadyCallback, OnMapLongClickListener, OnMarkerDragListener,
    GoogleMap.OnMapClickListener {
    private lateinit var mMap: GoogleMap
    private val binding: FragmentMapBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }
    private lateinit var marker: Marker
    private val viewModel: MapViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(
            remoteDataSource, SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        )
        val factory = WeatherViewModelFactory(repo)
        factory.create(MapViewModel::class.java)
    }
    private val alarm: AndroidAlarmScheduler by lazy {
        AndroidAlarmScheduler(requireContext())
    }
    private val alarmItem by lazy {
        AlarmItem(
            LocalDateTime.now().plusSeconds(10L), "This is a test alarm"
        )
    }

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mapFragment =
            childFragmentManager.findFragmentById(com.example.skyalert.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        val loc = LatLng(viewModel.getMapLocation().lat, viewModel.getMapLocation().lon)
        mMap = googleMap
        marker = mMap.addMarker(
            MarkerOptions().position(loc).title("Here I am!").draggable(true)
        )!!

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
        mMap.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isMapToolbarEnabled = true
            isMyLocationEnabled = true
            uiSettings.isScrollGesturesEnabled = true
            uiSettings.isZoomGesturesEnabled = true
        }
        mMap.setOnMarkerDragListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnMapClickListener(this)
    }

    override fun onMarkerDragStart(p0: Marker) {
    }

    override fun onMarkerDrag(p0: Marker) {
        val lat = p0.position.latitude
        val lon = p0.position.longitude
        val loc = LatLng(lat, lon)
        marker.position = loc
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 8f))
        mMap.setOnMapLongClickListener(this)
    }

    override fun onMarkerDragEnd(p0: Marker) {
    }


    override fun onMapLongClick(p0: LatLng) {
        handlePress(p0)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p0, 8f))
    }


    override fun onMapClick(p0: LatLng) {
        handlePress(p0)
    }

    private fun handlePress(p0: LatLng) {
        marker.position = p0

        val bottomSheet = MapBottomSheet(Coord(p0.latitude, p0.longitude))
        bottomSheet.show(requireActivity().supportFragmentManager, "MapBottomSheet")
    }


}