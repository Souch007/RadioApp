package com.coderoids.radio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
import com.coderoids.radio.ui.search.SearchViewModel
import com.coderoids.radio.ui.seeall.SeeAllViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sothree.slidinguppanel.SlidingUpPanelLayout


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var radioViewModel: RadioViewModel
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var podcastViewModel: PodcastViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var seeAllViewModel: SeeAllViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPredEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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
                    if (newState!!.name == "EXPANDED") {
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
        hideProgressBar()
    }

    private fun hideProgressBar() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            binding.progressHolder.visibility = View.GONE
//        }, 5000)
    }

    private fun searchWatcherListener() {
        binding.searchEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.progressHolder.visibility = View.VISIBLE
                hideProgressBar()
                mainViewModel._state.value = true
                var searchedString = binding.searchEditText.text.toString()
                if (!searchedString.matches("".toRegex()) && !searchedString.matches("\\.".toRegex())) {
                    mainViewModel.getSearchQueryResult(searchedString, searchViewModel)
                    binding.navView.selectedItemId = R.id.navigation_search
                    binding.searchEditText.setText("")
                    val inputMethodManager =
                        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onBackPressed() {

    }

    private fun Observers() {
        if (mainViewModel.favouritesRadioArray.size == 0) {
            val gson = Gson()
            val json = sharedPreferences.getString("FavChannels", null)
            if (json != null) {
                val type = object : TypeToken<ArrayList<PlayingChannelData?>?>() {}.getType()
                mainViewModel.favouritesRadioArray = gson.fromJson(json, type)
            }
        }
        mainViewModel._queriedSearched.observe(this) {
            mainViewModel.getSearchQueryResult(it, searchViewModel)
            binding.navView.selectedItemId = R.id.navigation_search
        }

        mainViewModel._isFavUpdated.observe(this) {
            val gson = Gson();
            val json = gson.toJson(mainViewModel.favouritesRadioArray)
            sharedPredEditor.putString("FavChannels", json).apply()
        }

        mainViewModel.radioSelectedChannel.observe(this) {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_radio_player);
            binding.settingsBarLayout.visibility = View.GONE
            mainViewModel.valueTypeFrag = it.type
        }

        mainViewModel._radioSeeAllSelected.observe(this) {
           val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (it == "CLOSE") {
                navController.navigate(R.id.navigation_radio)
                binding.settingsBarLayout.visibility = View.VISIBLE
            } else if (it == "CLOSE_PODCAST") {
//                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_podcast)
                binding.settingsBarLayout.visibility = View.VISIBLE
            } else {
//                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_see_all)
                binding.settingsBarLayout.visibility = View.GONE
            }

        }

        mainViewModel.isPlayerFragVisible.observe(this@MainActivity) {
            if (!it) {
                var type = mainViewModel._radioSelectedChannel.value!!.type
                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                if (type.matches("PODCAST".toRegex())) {
                    navController.navigate(R.id.navigation_podcast)
                } else
                    navController.navigate(R.id.navigation_radio)
                binding.settingsBarLayout.visibility = View.VISIBLE
                binding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                binding.playButtonCarousel.player = mainViewModel.exoPlayer
                binding.playButtonCarousel.showTimeoutMs = -1
                binding.playBtn.player = mainViewModel.exoPlayer
            }
        }

        mainViewModel._isNewStationSelected.observe(this@MainActivity) {
            try {
                if (!it && binding.playButtonCarousel != null && mainViewModel.exoPlayer != null) {
                    if (binding.playButtonCarousel.player!!.isPlaying)
                        binding.playButtonCarousel.player!!.stop()
                    binding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }

        mainViewModel._suggesteStations.observe(this@MainActivity) {
//            var data =  it as List<RadioLists>
//            binding.mainViewModelAdapter = com.coderoids.radio.ui.radio.adapter.RadioFragmentAdapter(
//                data,
//                mainViewModel
//            )
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
        mainViewModel.getSearchQueryResult("", searchViewModel)

    }

    private fun initializeViewModel() {
        val factory = getViewModelFactory()
        radioViewModel =
            ViewModelProvider(this@MainActivity, factory).get(RadioViewModel::class.java)
        podcastViewModel =
            ViewModelProvider(this@MainActivity, factory).get(PodcastViewModel::class.java)
        searchViewModel =
            ViewModelProvider(this@MainActivity, factory).get(SearchViewModel::class.java)
        favouritesViewModel =
            ViewModelProvider(this@MainActivity, factory).get(FavouritesViewModel::class.java)
        seeAllViewModel =
            ViewModelProvider(this@MainActivity, factory).get(SeeAllViewModel::class.java)
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
//            val currentFragId = mainViewModel.currentFragmentId
//            currentFragId
//            if(mainViewModel.valueTypeFrag.matches("PODCAST".toRegex())){
//                mainViewModel.valueTypeFrag = ""
//                if (destination.id == R.id.navigation_radio) {
//                    navController.clearBackStack(R.id.navigation_radio_player)
//                    navController.navigate(R.id.navigation_radio)
//                }
//                else {
//                    navController.navigate(R.id.navigation_podcast)
//                }
//
//                return@addOnDestinationChangedListener
//            } else if(mainViewModel.valueTypeFrag.matches("RADIO".toRegex())){
//                mainViewModel.valueTypeFrag = ""
//                if (destination.id == R.id.navigation_podcast) {
//                    navController.clearBackStack(R.id.navigation_radio_player)
//                    navController.navigate(R.id.navigation_podcast)
//                }
//                else {
//                    navController.navigate(R.id.navigation_radio)
//                }
//                return@addOnDestinationChangedListener
//            }

            when (destination.id) {
                R.id.navigation_radio -> {
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
                    binding.tvRadio.text = "Radio"
                }
                R.id.navigation_podcast -> {
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
                    binding.tvRadio.text = "Podcast"
                }
                R.id.navigation_favourites -> {
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
                    binding.tvRadio.text = "Favourites"
                }
                R.id.navigation_search -> {
                    binding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
                    binding.tvRadio.text = "Search"
                }
                R.id.navigation_radio_player -> {
                    navView.visibility = View.GONE
                    binding.settingsBarLayout.visibility = View.GONE
                }
            }
        }

        binding.ivSettings.setOnClickListener {
            Intent(this@MainActivity, SettingsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }
}