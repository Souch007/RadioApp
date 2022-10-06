package com.coderoids.radio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aemerse.slider.ImageCarousel
import com.aemerse.slider.model.CarouselItem
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
        binding.dragView.visibility = View.INVISIBLE
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
        val carousel: ImageCarousel = findViewById(R.id.carousel)
        carousel.registerLifecycle(lifecycle)
        val list = mutableListOf<CarouselItem>()
        list.add(
            CarouselItem(
                imageUrl = "https://images.unsplash.com/photo-1532581291347-9c39cf10a73c?w=1080",
                caption = "Photo by Aaron Wu on Unsplash"
            )
        )
        list.add(
            CarouselItem(
                imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080"
            )
        )
        val headers = mutableMapOf<String, String>()
        headers["header_key"] = "header_value"

        list.add(
            CarouselItem(
                imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1080",
                headers = headers
            )
        )

        list.add(
            CarouselItem(
                imageDrawable = R.drawable.ic_baseline_radio_24,
                caption = "Photo by Kimiya Oveisi on Unsplash"
            )
        )

        list.add(
            CarouselItem(
                imageDrawable = R.drawable.ic_baseline_favorite_border_24
            )
        )
        carousel.setData(list)

    }

    private fun Observers() {
        radioViewModel.radioClickEvent.observe(this){
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_radio_player);
            binding.settingsBarLayout.visibility = View.GONE
        }

        mainViewModel.isPlayerFragVisible.observe(this@MainActivity){
            if(!it) {
                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_radio);
                binding.settingsBarLayout.visibility = View.VISIBLE
            }
        }

    }

    private fun callApis() {
        mainViewModel.getRadioListing(radioViewModel)
        mainViewModel.getPodCastListing(podcastViewModel)
        mainViewModel.getLanguages(radioViewModel)
        mainViewModel.getCountires(radioViewModel)
        mainViewModel.getAllGenres(radioViewModel)
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
                    binding.tvRadio.text = "Search"}

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