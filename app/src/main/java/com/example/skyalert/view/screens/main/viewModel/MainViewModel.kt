package com.example.skyalert.view.screens.main.viewModel

import androidx.lifecycle.ViewModel
import com.example.skyalert.repository.IWeatherRepo

class MainViewModel(private val repo: IWeatherRepo) : ViewModel() {

    fun getLocationSource() = repo.getLocationSource()
}