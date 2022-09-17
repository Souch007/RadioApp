package com.coderoids.radio.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentFavouritesBinding
import com.coderoids.radio.databinding.FragmentRadioBinding
import com.coderoids.radio.ui.radio.RadioViewModel

class FavouritesFragment : BaseFragment<FragmentFavouritesBinding>(R.layout.fragment_favourites) {
    val favouritesViewModel : FavouritesViewModel by activityViewModels()

    override fun FragmentFavouritesBinding.initialize() {
        binding.lifecycleOwner =this@FavouritesFragment
        binding.favViewModel = favouritesViewModel
    }

}