package com.coderoids.radio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
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
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.search.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout


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
        Observers()
        binding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN

        binding.slidingLayout.addPanelSlideListener(
            object : SlidingUpPanelLayout.PanelSlideListener {
                override fun onPanelSlide(panel: View, slideOffset: Float) {
                }
                override fun onPanelStateChanged(
                    panel: View?,
                    previousState: SlidingUpPanelLayout.PanelState?,
                    newState: SlidingUpPanelLayout.PanelState?
                ) {
                    if(newState!!.name == "EXPANDED"){
                        binding.header.visibility = View.GONE
                    } else {
                        binding.header.visibility = View.VISIBLE
                    }
                }
            }
        )
        binding.slideUp.setOnClickListener {
            binding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        binding.crossSlider.setOnClickListener {
            binding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
            mainViewModel._isNewStationSelected.value = false
        }

        searchWatcherListener()

    }

    private fun searchWatcherListener() {
        binding.searchEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var searchedString = binding.searchEditText.text.toString()
                if (!searchedString.matches("".toRegex()) && !searchedString.matches("\\.".toRegex())) {
                    mainViewModel.getSearchQueryResult(searchedString, searchViewModel)
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onBackPressed() {

    }

    private fun Observers() {
        mainViewModel.radioSelectedChannel.observe(this){
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_radio_player);
            binding.settingsBarLayout.visibility = View.GONE
        }

        mainViewModel.isPlayerFragVisible.observe(this@MainActivity){
            if(!it) {
                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_radio);
                binding.settingsBarLayout.visibility = View.VISIBLE
                binding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                binding.playButtonCarousel.player = mainViewModel.exoPlayer
                binding.playButtonCarousel.showTimeoutMs = -1
                binding.playBtn.player = mainViewModel.exoPlayer
            }
        }

        mainViewModel._isNewStationSelected.observe(this@MainActivity){
            if (!it && binding.playButtonCarousel != null && mainViewModel.exoPlayer != null){
                binding.playButtonCarousel.player!!.stop()
                binding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
            }
        }

        mainViewModel._suggesteStations.observe(this@MainActivity){
            var data =  it as List<RadioLists>
            binding.mainViewModelAdapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
                data,
                mainViewModel
            )
            binding.currentRadioInfo.text = mainViewModel.radioSelectedChannel.value?.name

        }
    }

    private fun callApis() {
        mainViewModel.getRadioListing(radioViewModel)
        mainViewModel.getPodCastListing(podcastViewModel)
        mainViewModel.getLanguages(radioViewModel)
        mainViewModel.getCountires(radioViewModel)
        mainViewModel.getAllGenres(radioViewModel)
        mainViewModel.getFrequentSearchesTags(searchViewModel)
        mainViewModel.getSearchQueryResult("",searchViewModel)

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
                R.id.navigation_radio  -> {
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    binding.tvRadio.text = "Radio"
                }
                R.id.navigation_podcast -> {
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    binding.tvRadio.text = "Podcast"
                }
                R.id.navigation_favourites -> {
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    binding.tvRadio.text = "Favourites"}
                R.id.navigation_search ->{
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    binding.tvRadio.text = "Search"
                }
                R.id.navigation_radio_player -> {
                    binding.settingsBarLayout.visibility = View.GONE

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