package com.example.skyalert.view.screens.bookmark.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.db.model.BookmarkedWeatherState
import com.example.skyalert.dataSource.local.localStorage.LocalStorage
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        val localStorage = LocalStorage.getInstance(requireActivity().applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref, localStorage
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

        binding.rvBookmarks.layoutManager = GridLayoutManager(requireContext(), 2)

        lifecycleScope.launch {
            viewModel.bookmarkState.collect {
                when (it) {
                    is BookmarkedWeatherState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is BookmarkedWeatherState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvBookmarks.adapter =
                            RvBookmarkAdapter(this@BookmarkFragment).apply { submitList(it.currentWeather) }
                    }

                    is BookmarkedWeatherState.Error -> binding.progressBar.visibility = View.GONE
                }
            }
        }

    }

    override fun onCardClick(currentWeather: CurrentWeather) {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(getString(R.string.delete_bookmark))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_bookmark))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteBookmark(currentWeather)
                }
            }.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setBackground(ResourcesCompat.getDrawable(resources, R.drawable.dialog_background, requireActivity().theme)).create()
            .show()
    }
}