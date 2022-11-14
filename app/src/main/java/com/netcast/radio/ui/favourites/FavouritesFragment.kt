package com.netcast.radio.ui.favourites

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentFavouritesBinding
import com.netcast.radio.ui.favourites.adapters.FavouriteAdapter

class FavouritesFragment : BaseFragment<FragmentFavouritesBinding>(R.layout.fragment_favourites) {
    val favouritesViewModel : FavouritesViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentFavouritesBinding.initialize() {
        binding.lifecycleOwner =this@FavouritesFragment
        binding.favViewModel = favouritesViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        mainActivityViewModel.favouritesRadioArray = AppSingelton.favouritesRadioArray
        binding.mainViewModel = mainActivityViewModel

        if(mainActivityViewModel.favouritesRadioArray.size >0){
            binding.emptyView.visibility = View.GONE
            binding.head.visibility = View.VISIBLE
            binding.favouritesAdapter = FavouriteAdapter(listOf(), mainActivityViewModel)

        } else {
            binding.emptyView.visibility = View.VISIBLE
            binding.head.visibility = View.GONE
        }
    }



}