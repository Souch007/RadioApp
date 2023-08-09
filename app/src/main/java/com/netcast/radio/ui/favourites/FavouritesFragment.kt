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

    override fun FragmentFavouritesBinding.initialize() {
        binding.lifecycleOwner = this@FavouritesFragment
        binding.favViewModel = favouritesViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        mainActivityViewModel.favouritesRadioArray = AppSingelton?.favouritesRadioArray?.asReversed() ?: mutableListOf()

        var radioFiterList = AppSingelton.favouritesRadioArray?.filter { it.idPodcast!="PODCAST"}
        var radioPodcasrList = AppSingelton.favouritesRadioArray?.filter { it.idPodcast=="PODCAST"}

        binding.mainViewModel = mainActivityViewModel

        if (mainActivityViewModel.favouritesRadioArray.size > 0) {
            binding.emptyView.visibility = View.GONE
            binding.head.visibility = View.VISIBLE
            binding.favouritesAdapter = FavouriteAdapter(listOf(), mainActivityViewModel,"favourites")

        } else {
            binding.emptyView.visibility = View.VISIBLE
            binding.head.visibility = View.GONE
        }
        binding.btnDownload.setOnClickListener {
            startActivity(Intent(requireContext(), DownloadActivity::class.java))
        }
        AppSingelton._isFavUpdated.observe(viewLifecycleOwner) {
            it?.let {
                if (it)
                    favouritesAdapter?.notifyDataSetChanged()

            }
        }
        binding.tvRadio.setOnClickListener {
            radioFiterList?.let { it1 -> setSelectedTab(true, it1,radioPodcasrList) }

        }
        binding.tvPodcast.setOnClickListener {
            radioFiterList?.let { it1 -> setSelectedTab(false, it1, radioPodcasrList) }
        }
    }
    private fun setSelectedTab(
        isStation: Boolean,
        radioFiterList: List<PlayingChannelData>,
        radioPodcasrList: List<PlayingChannelData>?
    ) {
        if (isStation) {
            binding.tvPodcast.setTextColor(ContextCompat.getColor(requireContext(), R.color.White))
            binding.tvRadio.setTextColor(ContextCompat.getColor(requireContext(), R.color.OrangeRed))
            mainActivityViewModel.favouritesRadioArray = radioFiterList?.asReversed()?.toMutableList()
                ?: mutableListOf<PlayingChannelData>()
            binding.favouritesAdapter = FavouriteAdapter(mainActivityViewModel.favouritesRadioArray, mainActivityViewModel,"favourites")

        }
        else{
            binding.tvPodcast.setTextColor(ContextCompat.getColor(requireContext(), R.color.OrangeRed))
            binding.tvRadio.setTextColor(ContextCompat.getColor(requireContext(), R.color.White))
            mainActivityViewModel.favouritesRadioArray = radioPodcasrList?.asReversed()?.toMutableList() ?: mutableListOf<PlayingChannelData>()

            binding.favouritesAdapter = FavouriteAdapter(mainActivityViewModel.favouritesRadioArray, mainActivityViewModel,"favourites")

        }
    }


}