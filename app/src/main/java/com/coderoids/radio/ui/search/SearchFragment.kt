package com.coderoids.radio.ui.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseFragment
import com.coderoids.radio.databinding.FragmentSearchBinding

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search){
    val _fragmentSearchViewModel : SearchViewModel by activityViewModels()
    override fun FragmentSearchBinding.initialize() {
        binding.fragmentSearchViewModel = _fragmentSearchViewModel
    }
}