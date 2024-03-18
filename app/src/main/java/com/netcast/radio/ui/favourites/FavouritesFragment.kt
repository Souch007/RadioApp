package com.netcast.radio.ui.favourites

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.base.BaseViewModel
import com.netcast.radio.databinding.FragmentFavouritesBinding
import com.netcast.radio.download.DownloadActivity
import com.netcast.radio.ui.favourites.adapters.FavouriteAdapter

class FavouritesFragment : BaseFragment<FragmentFavouritesBinding>(R.layout.fragment_favourites) {
    val favouritesViewModel: FavouritesViewModel by activityViewModels()
    val baseViewModel: BaseViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel
    private lateinit var favouriteAdapter: FavouriteAdapter
    private var isStation=true

    override fun FragmentFavouritesBinding.initialize() {
        binding.lifecycleOwner = this@FavouritesFragment
        binding.favViewModel = favouritesViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        favouriteAdapter = FavouriteAdapter(listOf(), mainActivityViewModel, "favourites")

        binding.mainViewModel = mainActivityViewModel
        mainActivityViewModel.favouritesRadioArray = AppSingelton?.favouritesRadioArray ?: mutableListOf()

        if (mainActivityViewModel.favouritesRadioArray.size > 0) {
            binding.emptyView.visibility = View.GONE
//            binding.head.visibility = View.VISIBLE
            binding.favouritesAdapter = favouriteAdapter
        } else {
            binding.emptyView.visibility = View.VISIBLE
//            binding.head.visibility = View.GONE
        }

        setSelectedTab(AppSingelton.favouritesRadioArray ,isStation)
        binding.btnDownload.setOnClickListener {
            startActivity(Intent(requireContext(), DownloadActivity::class.java))
        }
        AppSingelton._isFavUpdated.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    mainActivityViewModel.favouritesRadioArray = AppSingelton?.favouritesRadioArray ?: mutableListOf()
                    setSelectedTab(
                        AppSingelton.favouritesRadioArray,
                        isStation
                    )
//                    favouritesAdapter?.notifyDataSetChanged()
                }

            }
        }
        binding.tvRadio.setOnClickListener {
            isStation=true
            setSelectedTab(AppSingelton.favouritesRadioArray, isStation)
        }
        binding.tvPodcast.setOnClickListener {
            isStation=false
           setSelectedTab(AppSingelton.favouritesRadioArray, isStation)
        }
    }

    private fun setSelectedTab(
        favouriteList: MutableList<PlayingChannelData>,
        isStation: Boolean,
    ) {

        var radioFiterList =
            favouriteList?.filter { it.type.equals("RADIO", true) }
        var podCastList =
            favouriteList?.filter { it.type.equals("Episodes", true) }

        if (isStation) {
            binding.btnDownload.visibility=View.GONE
            binding.tvPodcast.setTextColor(ContextCompat.getColor(requireContext(), R.color.White))
            binding.tvRadio.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.OrangeRed
                )
            )

//            mainActivityViewModel.favouritesRadioArray = radioFiterList?.toMutableList()?.asReversed()!!
            mainActivityViewModel.favouritesRadioArray = radioFiterList?.toMutableList()!!
//            AppSingelton._isFavUpdated.value = true
             binding.favouritesAdapter = FavouriteAdapter(
                 mainActivityViewModel.favouritesRadioArray,
                 mainActivityViewModel,
                 "favourites")

//            favouriteAdapter.updateData(mainActivityViewModel.favouritesRadioArray)
//            favouriteAdapter.notifyDataSetChanged()

        } else {
            binding.btnDownload.visibility=View.VISIBLE
            binding.tvPodcast.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.OrangeRed
                )
            )
            binding.tvRadio.setTextColor(ContextCompat.getColor(requireContext(), R.color.White))
            mainActivityViewModel.favouritesRadioArray = podCastList?.toMutableList()!!
//            mainActivityViewModel.favouritesRadioArray = podCastList?.toMutableList()?.asReversed()!!
            binding.favouritesAdapter = FavouriteAdapter(
                 mainActivityViewModel.favouritesRadioArray,
                 mainActivityViewModel,
                 "favourites"
             )

//            favouriteAdapter.notifyDataSetChanged()
//            AppSingelton._isFavUpdated.value = true

        }
    }


}