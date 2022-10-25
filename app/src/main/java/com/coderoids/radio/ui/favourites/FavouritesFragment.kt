package com.coderoids.radio.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.MainViewModel
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentFavouritesBinding
import com.coderoids.radio.databinding.FragmentRadioBinding
import com.coderoids.radio.ui.favourites.adapters.FavouriteAdapter
import com.coderoids.radio.ui.podcast.adapter.PodcastFragmentAdapter
import com.coderoids.radio.ui.radio.RadioViewModel

class FavouritesFragment : BaseFragment<FragmentFavouritesBinding>(R.layout.fragment_favourites) {
    val favouritesViewModel : FavouritesViewModel by activityViewModels()
    private lateinit var mainActivityViewModel : MainViewModel

    override fun FragmentFavouritesBinding.initialize() {
        binding.lifecycleOwner =this@FavouritesFragment
        binding.favViewModel = favouritesViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!).get(MainViewModel::class.java)
        }
        binding.mainViewModel = mainActivityViewModel

        if(mainActivityViewModel.favouritesRadioArray.size >0){
            binding.emptyView.visibility = View.GONE
            binding.head.visibility = View.VISIBLE
            binding.favouritesAdapter = FavouriteAdapter(listOf(), mainActivityViewModel)

        }else {
            binding.emptyView.visibility = View.VISIBLE
            binding.head.visibility = View.GONE
        }
    }



}