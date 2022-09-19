package com.coderoids.radio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.coderoids.radio.base.ViewModelFactory
import com.coderoids.radio.databinding.ActivityMainBinding
import com.coderoids.radio.request.AppApis
import com.coderoids.radio.request.RemoteDataSource
import com.coderoids.radio.request.repository.AppRepository
import com.coderoids.radio.ui.SettingsActivity
import com.coderoids.radio.ui.favourites.FavouritesViewModel
import com.coderoids.radio.ui.podcast.PodcastViewModel
import com.coderoids.radio.ui.radio.RadioViewModel
import com.coderoids.radio.ui.search.SearchViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var radioViewModel: RadioViewModel
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var podcastViewModel: PodcastViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        initializeViewModel()
    }

    private fun callApis() {
        mainViewModel.getRadioListing(radioViewModel)
        mainViewModel.getPodCastListing(podcastViewModel)
    }

    private fun initializeViewModel() {
        val factory = getViewModelFactory()
        radioViewModel = ViewModelProvider(this@MainActivity, factory).get(RadioViewModel::class.java)
        podcastViewModel = ViewModelProvider(this@MainActivity, factory).get(PodcastViewModel::class.java)
        searchViewModel = ViewModelProvider(this@MainActivity, factory).get(SearchViewModel::class.java)
        favouritesViewModel = ViewModelProvider(this@MainActivity, factory).get(FavouritesViewModel::class.java)
        mainViewModel = ViewModelProvider(this@MainActivity, factory).get(MainViewModel::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            callApis()
            setUpUI()
        }, 200)
    }

    private fun setUpUI() {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_radio -> {
                    binding.tvRadio.text = "Radio"
                }
                R.id.navigation_podcast -> {
                    binding.tvRadio.text = "Podcast"

                }
                R.id.navigation_favourites -> {
                    binding.tvRadio.text = "Favourites"
                }
                R.id.navigation_search -> {
                    binding.tvRadio.text = "Search"

                }
            }
        }

        binding.ivSettings.setOnClickListener {
            Intent(this@MainActivity,SettingsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }
}