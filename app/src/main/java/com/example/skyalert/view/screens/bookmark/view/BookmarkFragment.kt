package com.example.skyalert.view.screens.bookmark.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.db.model.BookmarkedWeatherState
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentBookmarkBinding
import com.example.skyalert.interfaces.OnBookmarkCardClickListener
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.view.screens.bookmark.adapter.RvBookmarkAdapter
import com.example.skyalert.view.screens.bookmark.viewModel.BookmarkViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment(), OnBookmarkCardClickListener {
    private val binding: FragmentBookmarkBinding by lazy {
        FragmentBookmarkBinding.inflate(layoutInflater)
    }
    private val viewModel: BookmarkViewModel by lazy {
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
        ViewModelProvider(this, factory)[BookmarkViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch { viewModel.getBookmarkList() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).setSupportActionBar(binding.settingsToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.bookmarks)
        binding.settingsToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBookmarks.layoutManager

        lifecycleScope.launch {
            viewModel.bookmarkState.collect {
                when (it) {
                    is BookmarkedWeatherState.Loading -> binding.progressBar.visibility = View.VISIBLE

                    is BookmarkedWeatherState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvBookmarks.adapter =
                            RvBookmarkAdapter(this@BookmarkFragment).apply { submitList(it.currentWeather) }
                    }

                    is BookmarkedWeatherState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, it.message, Snackbar.LENGTH_SHORT).setAction(getString(R.string.retry)) {
                            lifecycleScope.launch { viewModel.getBookmarkList() }
                        }.show()
                    }

                }
            }
        }

    }

    override fun onCardClick(currentWeather: CurrentWeather): Int {
        var result = 0
        lifecycleScope.launch(Dispatchers.IO) {
            result = viewModel.deleteFavoriteWeather(currentWeather)
        }
        return result
    }
}