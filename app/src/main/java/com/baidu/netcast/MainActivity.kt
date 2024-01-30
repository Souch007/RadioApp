package com.baidu.netcast

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.BaseActivity
import com.baidu.netcast.base.ViewModelFactory
import com.baidu.netcast.databinding.ActivityMainBinding
import com.baidu.netcast.download.DownloadActivity
import com.baidu.netcast.request.AppApis
import com.baidu.netcast.request.AppConstants
import com.baidu.netcast.request.RemoteDataSource
import com.baidu.netcast.request.repository.AppRepository
import com.baidu.netcast.ui.SettingsActivity
import com.baidu.netcast.ui.favourites.FavouritesViewModel
import com.baidu.netcast.ui.podcast.PodcastViewModel
import com.baidu.netcast.ui.radio.RadioViewModel
import com.baidu.netcast.ui.radioplayermanager.RadioPlayerActivity
import com.baidu.netcast.ui.search.SearchViewModel
import com.baidu.netcast.ui.seeall.SeeAllViewModel
import com.baidu.netcast.ui.ui.settings.AlarmFragment
import com.baidu.netcast.ui.ui.settings.SleepTimerFragment
import com.baidu.netcast.util.BottomSheetOptionsFragment
import com.baidu.netcast.util.OptionsClickListner
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), OptionsClickListner {
    private lateinit var mainActivityViewModel: MainViewModel
    private lateinit var radioViewModel: RadioViewModel
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var podcastViewModel: PodcastViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var seeAllViewModel: SeeAllViewModel
    private var DEVICE_ID = ""
    private var selectedDestination = ""
    var backPressedTime: Long = 0
    private lateinit var sharedPredEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        sharedPredEditor = sharedPreferences.edit()
        val appmode = sharedPreferences.getInt("App_Mode", -1)
        if (appmode == 0)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        val tv_VersionInfo=dataBinding.splashview.appCompatTextView2
        var versionCode = BuildConfig.VERSION_NAME
        tv_VersionInfo.text="Version Info ${versionCode}\n© 2016-2024"


        DEVICE_ID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        initializeViewModel()
        Observers()
        dataBinding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN

//        dataBinding.slidingLayout.setDragView(dataBinding.slidedown)
//        dataBinding.slidingLayout.setDragView(dataBinding.playerOptions)
        AppSingelton.currentActivity = AppConstants.MAIN_ACTIVITY
        dataBinding.playerOptions.setOnClickListener {
            showBottomSheetDialog()
        }

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
        getIntentData()
        if (sharedPreferences.getBoolean("delete_completed_episode", true))
            deleteCompletedEpisodes()

        handleIncomingDeepLinks()

    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkOfflineChannels() {
        CoroutineScope(Dispatchers.IO).launch {
            val listOffline = getOfflineData()
            runOnUiThread {
                if (listOffline.isNotEmpty()) {
//                    dataBinding.primeLayout.visibility = View.VISIBLE
                } else dataBinding.primeLayout.visibility = View.GONE
            }

            dataBinding.primeLayout.setOnClickListener {
                startActivity(Intent(this@MainActivity, DownloadActivity::class.java))
            }
        }
    }

    private fun hideProgressBar() {

        // Assuming you have a layout with the ID 'myLayout' in your XML file

        // Set up the fade-in animation
        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 1000 // You can adjust the duration as needed

        // Set up the fade-out animation
        val fadeOut = AlphaAnimation(1.0f, 0.0f)
        fadeOut.duration = 5000 // You can adjust the duration as needed

        // Combine the animations into a sequence
        val fadeSequence = AnimationSet(true)
        fadeSequence.addAnimation(fadeIn)
        fadeSequence.addAnimation(fadeOut)

        // Start the animation when the activity is created or when you want to trigger it
        dataBinding.llShimmerLayout.startAnimation(fadeSequence)

        // Optional: You can set up a listener to perform actions when the animation ends
        fadeSequence.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Do something when the animation ends
                dataBinding.llShimmerLayout.visibility = View.GONE
                dataBinding.llShimmerLayoutmain.visibility = View.GONE
//                dataBinding.navView.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        /*Handler(Looper.getMainLooper()).postDelayed({
            dataBinding.llShimmerLayout.visibility = View.GONE
            dataBinding.navView.visibility = View.VISIBLE
        }, 5000)*/
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
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
//                    dataBinding.searchEditText.setText("")
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


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val currentDestination = navController.currentDestination

        // Check if it's the home destination

        if (getCurrenDestination(navController, currentDestination)) {
            if (backPressedTime + 1500 > System.currentTimeMillis()) {
                super.onBackPressed()
                finish()
            } else {
                Toast.makeText(this, "Press back again to exit the app.", Toast.LENGTH_LONG).show()
            }
            backPressedTime = System.currentTimeMillis()
        } else {
            val id = navController.currentDestination!!.id
            if (id == R.id.navigation_see_all) {
                if (mainViewModel._radioSeeAllSelected.value == "PODCAST")
                    mainViewModel._radioSeeAllSelected.value = "CLOSE_PODCAST"
                else
                    mainViewModel._radioSeeAllSelected.value = "CLOSE"
            }
        }
    }

    private fun getCurrenDestination(
        navController: NavController,
        currentDestination: NavDestination?
    ): Boolean {
        when (currentDestination?.id) {
            R.id.navigation_radio -> {
                return true
            }

            R.id.navigation_podcast -> {
                return true
            }

            R.id.navigation_search -> {
                return true
            }

            R.id.navigation_favourites -> {
                return true
            }

        }


        return false
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
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (navController.currentDestination?.id == R.id.navigation_radio) {
                val args = Bundle()
                args.putString("filter_tag", it)
                navController.navigate(
                    R.id.action_navigation_radio_to_navigation_filterstaions, args
                )
            } else if (navController.currentDestination?.id == R.id.allcountriesFragment) {
                val args = Bundle()
                args.putString("filter_tag", it)
                navController.navigate(
                    R.id.action_allcountriesFragment_to_navigation_filterstaions, args
                )
            } else if (navController.currentDestination?.id == R.id.allGenreFragment) {
                val args = Bundle()
                args.putString("filter_tag", it)
                navController.navigate(
                    R.id.navigation_filterstaions, args
                )
            } else if (navController.currentDestination?.id == R.id.allLanguagesFragment) {
                val args = Bundle()
                args.putString("filter_tag", it)
                navController.navigate(
                    R.id.navigation_filterstaions, args
                )
            } else {
                dataBinding.searchEditText.setText(it)
                dataBinding.llShimmerLayout.visibility = View.VISIBLE
                mainViewModel.getSearchQueryResult(DEVICE_ID, it, searchViewModel)
                dataBinding.navView.selectedItemId = R.id.navigation_search
                hideProgressBar()
//            startActivity(Intent(this, FilterRadioActivity::class.java).putExtra("filter_tag", it))
//            finish()
            }
        }

        AppSingelton.radioSelectedChannel.observe(this) {
            it?.let {
                if (AppSingelton.isAlramSet) storeObjectInSharedPref(
                    it, AppConstants.SELECTED_ALARM_RADIO
                )

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
                dataBinding.navView.visibility = View.VISIBLE
            } else if (it == "CLOSE_PODCAST") {
                dataBinding.navView.visibility = View.VISIBLE
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
                    closePlayerandPanel()

                }

            }


        }, 1000)
    }

    private fun closePlayerandPanel() {
        if (dataBinding.playButtonCarousel != null && AppSingelton.exoPlayer != null) {
            if (dataBinding.playButtonCarousel.player!!.isPlaying) {
                dataBinding.playButtonCarousel.player!!.stop()
            }
        }
        dataBinding.slidingLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        AppSingelton._radioSelectedChannel.value = null
    }

    private fun callApis() {
//        mainViewModel.getRadioListing(radioViewModel, getUserCountry(this))
        mainViewModel.getRadioListing(radioViewModel, AppSingelton.country)
        mainViewModel.getLanguages(radioViewModel)
        mainViewModel.getCountires(radioViewModel)
        mainViewModel.getAllGenres(radioViewModel)
        mainViewModel.getPodCastListing(podcastViewModel, "")
//        mainViewModel.getPodCastListing(podcastViewModel, getUserCountry(this))
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
//                    navView.visibility = View.VISIBLE
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
                    if (dataBinding.settingsBarLayout.visibility == View.VISIBLE) dataBinding.settingsBarLayout.visibility =
                        View.GONE
                    navView.visibility = View.GONE
                }

                else -> {
                    selectedDestination = getString(R.string.see_all)
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
        if (selectedDestination != getString(R.string.see_all)) dataBinding.settingsBarLayout.visibility =
            View.VISIBLE

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

    private fun getIntentData() {
        val url = intent.getStringExtra("alarm_radio_url")
        if (!url.isNullOrEmpty()) {
            val renderersFactory = DefaultRenderersFactory(this)
            AppSingelton.exoPlayer?.let {
                it.release()
                it.stop()
            }

            AppSingelton.exoPlayer =
                ExoPlayer.Builder(this, renderersFactory).setHandleAudioBecomingNoisy(true).build()
                    .also { exoPlayer ->
                        val mediaItem = MediaItem.fromUri(url ?: "")
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.addAnalyticsListener(object : AnalyticsListener {})
                        exoPlayer.addListener(this)
                        exoPlayer.prepare()
                        exoPlayer.play()
                    }
        }
    }

    private fun showBottomSheetDialog() {


        val isEpisode = AppSingelton._radioSelectedChannel.value?.type != "RADIO"
        val bottomSheetFragment = BottomSheetOptionsFragment(this, true, isEpisode)
        bottomSheetFragment.show(supportFragmentManager, "BSDialogFragment")


        /*  val bottomSheetDialog = BottomSheetDialog(this)
          val optionLayoutBinding = OptionLayoutBinding.inflate(layoutInflater, null, false)
          bottomSheetDialog.setContentView(optionLayoutBinding!!.root)

          optionLayoutBinding.tvSetalarm.setOnClickListener {
              startActivity(Intent(this, AlarmFragment::class.java))
              bottomSheetDia//Logismiss()
              closePlayerandPanel()
  //            dataBinding.slidingLayout.panelState=SlidingUpPanelLayout.PanelState.COLLAPSED
          }
          optionLayoutBinding.tvSetsleeptime.setOnClickListener {
  //            navController.navigate(R.id.sleepTimerFragment)
              startActivity(Intent(this, SleepTimerFragment::class.java))

              bottomSheetDia//Logismiss()
              closePlayerandPanel()
  //            dataBinding.slidingLayout.panelState=SlidingUpPanelLayout.PanelState.COLLAPSED
          }
          optionLayoutBinding.tvShare.setOnClickListener {
              bottomSheetDia//Logismiss()
              share(
                  "Checkout this link its amazing. ",
                  AppSingelton._radioSelectedChannel.value
              )

          }
          optionLayoutBinding.tvFavourite.setOnClickListener {
              bottomSheetDia//Logismiss()
              AppSingelton._radioSelectedChannel.value?.let { it1 ->
                  viewModel.addChannelToFavourites(
                      it1
                  )
              }

          }
          bottomSheetDialog.show()*/
    }

//    private fun share(messageToShare: String, appUrl: PlayingChannelData?) {
//        AppConstants.generateSharingLink(
////            deepLink = AppConstants.PREFIX.toUri(),
//            deepLink = Uri.parse("https://netcast.com/"),
//            Gson().toJson(appUrl)
//        ) { generatedLink ->
//            shareDeepLink(generatedLink)
//        }
//
//    }
//
//    private fun shareDeepLink(deepLink: String) {
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "text/plain"
//        intent.putExtra(
//            Intent.EXTRA_SUBJECT, "You have been shared an amazing meme, check it out ->"
//        )
//        intent.putExtra(Intent.EXTRA_TEXT, deepLink)
//        startActivity(intent)
//        closePlayerandPanel()
//
//    }

    override fun onSetAlarm() {
        startActivity(Intent(this, AlarmFragment::class.java))
        closePlayerandPanel()
    }

    override fun onShare() {
        AppConstants.share(
            "Checkout this link its amazing. ", AppSingelton._radioSelectedChannel.value, this
        )
        closePlayerandPanel()
    }

    override fun onSleepTimer() {
        startActivity(Intent(this, SleepTimerFragment::class.java))
        closePlayerandPanel()
    }

    override fun onFavourite() {
        AppSingelton._radioSelectedChannel.value?.let { it1 ->
            viewModel.addChannelToFavourites(
                it1
            )
        }
    }

    private fun handleIncomingDeepLinks() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                deepLink?.let { uri ->
                    val channeldataJson = deepLink.getQueryParameter("channeldata")

//                    val postId = uri.toString().substring(deepLink.toString().lastIndexOf("/") + 1)
                    when {
                        uri.toString().contains("channels") -> {
//                            navigateToNewsFeed(Gs)
                            AppSingelton._radioSelectedChannel.value =
                                Gson().fromJson(channeldataJson, PlayingChannelData::class.java)

                        }
                    }
                }
            }.addOnFailureListener {
                //Log(TAG, "handleIncomingDeepLinks: ${it.message}")
            }
    }
}
