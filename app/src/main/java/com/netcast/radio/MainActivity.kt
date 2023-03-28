package com.netcast.radio

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseActivity
import com.netcast.radio.base.ViewModelFactory
import com.netcast.radio.databinding.ActivityMainBinding
import com.netcast.radio.download.DownloadActivity
import com.netcast.radio.request.AppApis
import com.netcast.radio.request.AppConstants
import com.netcast.radio.request.RemoteDataSource
import com.netcast.radio.request.repository.AppRepository
import com.netcast.radio.ui.SettingsActivity
import com.netcast.radio.ui.favourites.FavouritesViewModel
import com.netcast.radio.ui.podcast.PodcastViewModel
import com.netcast.radio.ui.radio.RadioViewModel
import com.netcast.radio.ui.radioplayermanager.RadioPlayerActivity
import com.netcast.radio.ui.search.SearchViewModel
import com.netcast.radio.ui.seeall.SeeAllViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    private lateinit var radioViewModel: RadioViewModel
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var podcastViewModel: PodcastViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var seeAllViewModel: SeeAllViewModel
    private var DEVICE_ID = ""
    private var selectedDestination = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DEVICE_ID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        initializeViewModel()
        Observers()
        dataBinding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        AppSingelton.currentActivity = AppConstants.MAIN_ACTIVITY

        dataBinding.slidingLayout.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState!!.name == "EXPANDED") {
                    dataBinding.header.visibility = View.GONE
                } else {
                    dataBinding.header.visibility = View.VISIBLE
                }
            }
        })



        searchWatcherListener()
        hideProgressBar()
        checkOfflineChannels()

    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkOfflineChannels() {
        CoroutineScope(Dispatchers.IO).launch {
            val listOffline = getOfflineData()
            runOnUiThread {
                if (listOffline.isNotEmpty()) {
                    dataBinding.primeLayout.visibility = View.VISIBLE
                } else dataBinding.primeLayout.visibility = View.GONE
            }

            dataBinding.primeLayout.setOnClickListener {
                startActivity(Intent(this@MainActivity, DownloadActivity::class.java))
            }
        }
    }

    private fun hideProgressBar() {
        Handler(Looper.getMainLooper()).postDelayed({
            dataBinding.llShimmerLayout.visibility = View.GONE
        }, 5000)
    }

    private fun searchWatcherListener() {
        dataBinding.searchEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                dataBinding.llShimmerLayout.visibility = View.VISIBLE
                hideProgressBar()
                mainViewModel._state.value = true
                var searchedString = dataBinding.searchEditText.text.toString()
                if (!searchedString.matches("".toRegex()) && !searchedString.matches("\\.".toRegex())) {
                    mainViewModel.getSearchQueryResult(DEVICE_ID, searchedString, searchViewModel)
                    dataBinding.navView.selectedItemId = R.id.navigation_search
                    dataBinding.searchEditText.setText("")
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
        if (AppSingelton.favouritesRadioArray.size == 0) {
            val gson = Gson()
            val json = sharedPreferences.getString("FavChannels", null)
            if (json != null) {
                val type = object : TypeToken<ArrayList<PlayingChannelData?>?>() {}.getType()
                AppSingelton.favouritesRadioArray = gson.fromJson(json, type)
            }
        }

        manageRecentlyPlayed()

        mainViewModel._queriedSearched.observe(this) {
            dataBinding.searchEditText.setText(it)
            dataBinding.llShimmerLayout.visibility = View.VISIBLE
            mainViewModel.getSearchQueryResult(DEVICE_ID, it, searchViewModel)
            dataBinding.navView.selectedItemId = R.id.navigation_search
            hideProgressBar()
        }

        AppSingelton.radioSelectedChannel.observe(this) {
            it?.let {
                if (!AppSingelton.currentActivity.matches(AppConstants.RADIO_PLAYER_ACTIVITY.toRegex())) {
                    if (AppSingelton.exoPlayer != null) {
                        AppSingelton.exoPlayer!!.stop()
                        AppSingelton.exoPlayer!!.release()
                        AppSingelton.exoPlayer = null
                    }
                    Intent(this@MainActivity, RadioPlayerActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }
        }

        mainViewModel._radioSeeAllSelected.observe(this) {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (it == "CLOSE") {
                navController.navigate(R.id.navigation_radio)
            } else if (it == "CLOSE_PODCAST") {
                navController.navigate(R.id.navigation_podcast)
            } else {
                navController.navigate(R.id.navigation_see_all)
            }

        }

        AppSingelton._playingStarted.observe(this@MainActivity) {
            if (it) {
                showSlideUpPanel()
            }
        }
        mainViewModel.navigateToPodcast.observe(this@MainActivity) {
            if (it) dataBinding.navView.selectedItemId = R.id.navigation_podcast
        }

        AppSingelton.isNewStationSelected.observe(this@MainActivity) {
            try {
                if (it && dataBinding.playButtonCarousel != null && AppSingelton.exoPlayer != null) {
                    if (dataBinding.playButtonCarousel.player!!.isPlaying) {
                        dataBinding.playButtonCarousel.player!!.stop()
                    }
                }
                dataBinding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN

            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun showSlideUpPanel() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (AppSingelton._currentPlayingChannel.value != null && AppSingelton.currentActivity.matches(
                    AppConstants.MAIN_ACTIVITY.toRegex()
                )
            ) {
                dataBinding.playingChannelName.text =
                    AppSingelton._currentPlayingChannel.value!!.name
                Glide.with(this).load(AppSingelton._currentPlayingChannel.value!!.favicon)
                    .error(R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH).into(dataBinding.slideUp)

                Glide.with(this).load(AppSingelton._currentPlayingChannel.value!!.favicon)
                    .error(R.drawable.logo).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH).into(dataBinding.slideUpIv)
                dataBinding.currentRadioInfo.text = AppSingelton._currentPlayingChannel.value!!.name

                dataBinding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                dataBinding.playButtonCarousel.player = AppSingelton.exoPlayer
                dataBinding.playButtonCarousel.showTimeoutMs = -1
                dataBinding.playBtn.player = AppSingelton.exoPlayer
                dataBinding.playBtn.showController()
                dataBinding.playBtn.setShowPreviousButton(false)
                dataBinding.playBtn.setShowNextButton(false)
                AppSingelton.isNewItemAdded.value = true

                // Added Close Sliding Panel Button
                dataBinding.closeButton.setOnClickListener {
                    if (dataBinding.playButtonCarousel != null && AppSingelton.exoPlayer != null) {
                        if (dataBinding.playButtonCarousel.player!!.isPlaying) {
                            dataBinding.playButtonCarousel.player!!.stop()
                        }
                    }
                    dataBinding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
//

                }

            }


        }, 1000)
    }

    private fun callApis() {
        mainViewModel.getRadioListing(radioViewModel, getUserCountry(this))
        mainViewModel.getLanguages(radioViewModel)
        mainViewModel.getCountires(radioViewModel)
        mainViewModel.getAllGenres(radioViewModel)
        mainViewModel.getPodCastListing(podcastViewModel, getUserCountry(this))
        mainViewModel.getSearchQueryResult(DEVICE_ID, "", searchViewModel)
        mainViewModel.getFrequentSearchesTags(DEVICE_ID, searchViewModel)
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
        val navView: BottomNavigationView = dataBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_radio -> {
                    dataBinding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
//                    dataBinding.tvRadio.text = "Radio"
                }
                R.id.navigation_podcast -> {
                    selectedDestination = destination.label.toString()
                    dataBinding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
//                    dataBinding.tvRadio.text = "Podcast"
                }
                R.id.navigation_favourites -> {
                    selectedDestination = destination.label.toString()
                    dataBinding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
//                    dataBinding.tvRadio.text = "Favourites"
                }
                R.id.navigation_search -> {
                    selectedDestination = destination.label.toString()
                    dataBinding.settingsBarLayout.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
//                    dataBinding.tvRadio.text = "Search"
                }
                R.id.navigation_see_all -> {
                    selectedDestination = destination.label.toString()
                    if (dataBinding.settingsBarLayout.visibility == View.VISIBLE)
                        dataBinding.settingsBarLayout.visibility = View.GONE
                    navView.visibility = View.GONE
                }
                else -> {
                    dataBinding.settingsBarLayout.visibility = View.GONE
                }
            }
        }

        dataBinding.ivSettings.setOnClickListener {
            Intent(this@MainActivity, SettingsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun getViewModelFactory(): ViewModelFactory {
        val remoteDataSource = RemoteDataSource()
        return ViewModelFactory(AppRepository(remoteDataSource.buildApi(AppApis::class.java)))
    }

    override val layoutRes: Int
        get() = R.layout.activity_main
    override val bindingVariable: Int
        get() = BR.mainViewModel
    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java

    override fun onResume() {
        super.onResume()
        if (selectedDestination != getString(R.string.see_all))
            dataBinding.settingsBarLayout.visibility = View.VISIBLE

        AppSingelton.currentActivity = AppConstants.MAIN_ACTIVITY
        showSlideUpPanel()
        checkOfflineChannels()

    }

    override fun onStart() {
        super.onStart()
    }

    private fun detectNetworkCountry(context: Context): String? {
        try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            Log.d(TAG, "detectNetworkCountry: ${telephonyManager.networkCountryIso}")
            return telephonyManager.networkCountryIso
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getUserCountry(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                val locale = Locale("", simCountry)
                return locale.displayCountry
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // Device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    val locale = Locale("", networkCountry)
                    return locale.displayCountry
                }
            }
        } catch (e: Exception) {
        }
        return null
    }

    override fun onPause() {
        super.onPause()
    }


}