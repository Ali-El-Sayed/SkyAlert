package com.example.skyalert.view.screens.alerts.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.dataSource.local.localStorage.LocalStorage
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentAlertBinding
import com.example.skyalert.interfaces.OnAlertClickListener
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.services.alarm.AndroidAlarmScheduler
import com.example.skyalert.services.alarm.model.ALERT_TYPE
import com.example.skyalert.services.alarm.model.Alert
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.view.screens.alerts.adapter.RvAlertsAdapter
import com.example.skyalert.view.screens.alerts.viewModel.AlertViewmodel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlertFragment : Fragment(), OnAlertClickListener {

    private val binding by lazy { FragmentAlertBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val dao = WeatherDatabase.getInstance(requireContext().applicationContext).weatherDao()
        val sharedPref = SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        val localStorage = LocalStorage.getInstance(requireActivity().applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref, localStorage
        )
        val repo = WeatherRepo.getInstance(
            remoteDataSource, localDatasource
        )
        val factory = WeatherViewModelFactory(repo)
        ViewModelProvider(this, factory)[AlertViewmodel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { viewModel.getAlerts() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).setSupportActionBar(binding.settingsToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.alert)
        binding.settingsToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAlerts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            viewModel.alerts.collect {
                when (it) {
                    is AlertsState.Loading -> binding.progressBar.visibility = View.VISIBLE

                    is AlertsState.Success -> {
                        binding.progressBar.visibility = View.GONE
//                        for (alert in it.alerts) if (System.currentTimeMillis() - alert.time > 0) viewModel.deleteAlert(alert)
                        binding.rvAlerts.adapter = RvAlertsAdapter(this@AlertFragment).apply { submitList(it.alerts) }
                    }

                    is AlertsState.Error -> binding.progressBar.visibility = View.GONE

                }
            }
        }
    }

    override fun onAlertClick(alert: Alert) {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(getString(R.string.delete_bookmark))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_bookmark))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    when (alert.alertType) {
                        ALERT_TYPE.DIALOG -> WorkManager.getInstance(requireContext()).cancelWorkById(alert.uuid)
                        ALERT_TYPE.NOTIFICATION -> AndroidAlarmScheduler(requireContext()).cancelAlarm(alert)
                    }
                    viewModel.deleteAlert(alert)
                    viewModel.getAlerts()
                }
            }.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setBackground(resources.getDrawable(R.drawable.dialog_background, requireActivity().theme)).create().show()
    }

}