package com.example.skyalert.view.screens.map.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentMapBinding
import com.example.skyalert.interfaces.BottomSheetCallbacks
import com.example.skyalert.interfaces.OnAlertDialogCallback
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.services.alarm.AndroidAlarmScheduler
import com.example.skyalert.services.alarm.model.AlarmItem
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.view.dialogs.AlertResultDialog
import com.example.skyalert.view.dialogs.DateAlertDialog
import com.example.skyalert.view.dialogs.MapBottomSheet
import com.example.skyalert.view.screens.map.ALERT_RESULT_CONSTANTS
import com.example.skyalert.view.screens.map.MAP_CONSTANTS.MAP_LAT
import com.example.skyalert.view.screens.map.MAP_CONSTANTS.MAP_LON
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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MapsFragment"

class MapsFragment : Fragment(), OnMapReadyCallback, OnMapLongClickListener, OnMarkerDragListener, GoogleMap.OnMapClickListener,
    BottomSheetCallbacks, OnAlertDialogCallback {
    private lateinit var mMap: GoogleMap
    private val binding: FragmentMapBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }
    private lateinit var marker: Marker
    private val viewModel: MapViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val dao = WeatherDatabase.getInstance(requireContext().applicationContext).weatherDao()
        val sharedPref = SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref
        )
        val repo = WeatherRepo.getInstance(
            remoteDataSource, localDatasource
        )
        val factory = WeatherViewModelFactory(repo)
        factory.create(MapViewModel::class.java)
    }

    private val coord by lazy {
        viewModel.getMapLocation()
    }

    private val alarm: AndroidAlarmScheduler by lazy {
        AndroidAlarmScheduler(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mapFragment = childFragmentManager.findFragmentById(com.example.skyalert.R.id.map) as SupportMapFragment
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
        coord.apply { this.lat = lat; this.lon = lon }
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
        coord.lat = p0.latitude
        coord.lon = p0.longitude
        val argument = Bundle().apply {
            putDouble(MAP_LAT, p0.latitude)
            putDouble(MAP_LON, p0.longitude)
        }
        viewModel.saveAlertLocation(coord)
        val bottomSheet = MapBottomSheet(this)
        bottomSheet.arguments = argument
        bottomSheet.show(requireActivity().supportFragmentManager, "MapBottomSheet")
    }

    override fun setDefaultLocation(coord: Coord) {
        viewModel.setMapLocation(coord)
    }

    override fun saveBookmark() {
//        viewModel.saveMapLocation(coord)
    }

    override fun setAlert() {
        val dateAlertDialog = DateAlertDialog(this)
        viewModel.saveAlertLocation(coord)
        val arguments = Bundle().apply {
            putDouble(MAP_LAT, coord.lat)
            putDouble(MAP_LON, coord.lon)
        }
        dateAlertDialog.apply {
            this.arguments = arguments
            view?.background = resources.getDrawable(R.drawable.dialog_background, requireActivity().theme)
        }
        dateAlertDialog.show(parentFragmentManager, "alert_dialog")
    }

    override fun onAddToFavorite(currentWeather: CurrentWeather) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addToFavorite(currentWeather)
        }
    }

    override fun createNotification(alarmItem: AlarmItem) {
        viewModel.saveAlertLocation(coord)
        alarm.scheduleAlarm(alarmItem)
    }

    override fun createDialog(request: OneTimeWorkRequest) {
        WorkManager.getInstance(requireContext().applicationContext).enqueue(request)
        WorkManager.getInstance(requireContext().applicationContext).getWorkInfoByIdLiveData(request.id).observe(
            requireActivity()
        ) { workInfo ->
            val result = workInfo.outputData.getString(ALERT_RESULT_CONSTANTS.CURRENT_WEATHER)
            val currentWeather = result?.let { json -> Gson().fromJson(json, CurrentWeather::class.java) }
            if (currentWeather != null) {
                val dialog = AlertResultDialog(currentWeather)
                dialog.show(parentFragmentManager, "AlertResultDialog")
            }
        }
    }


}