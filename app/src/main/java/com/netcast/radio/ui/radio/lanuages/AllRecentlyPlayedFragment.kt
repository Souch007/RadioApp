package com.netcast.radio.ui.radio.lanuages

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentAllrecentplayedBinding
import com.netcast.radio.ui.radio.adapter.AllRecentPlayedAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AllRecentlyPlayedFragment() :

    BaseFragment<FragmentAllrecentplayedBinding>(R.layout.fragment_allrecentplayed) {

    private lateinit var mainActivityViewModel: MainViewModel
    private val TAG = "FilterRadioFragment"
    private lateinit var recentlyPlayedAdapter: AllRecentPlayedAdapter

    override fun FragmentAllrecentplayedBinding.initialize() {

        activity?.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }

        binding.titleDelete.setOnClickListener {
            if (binding.titleDelete.text.equals("Edit")) {
                binding.titleDelete.text = "Delete"
                recentlyPlayedAdapter.enbaleCheckboxes(true)

            } else {
                GlobalScope.launch {
                    recentlyPlayedAdapter.deleteSelectedItems()
                }
                binding.titleDelete.text = "Edit"
            }
        }


    }

    override fun onResume() {
        super.onResume()
        if (AppSingelton.recentlyPlayedArray.isNullOrEmpty()) {
            binding.titleDelete.visibility = View.GONE
        }
        recentlyPlayedAdapter = AllRecentPlayedAdapter(
            AppSingelton.recentlyPlayedArray.asReversed(),
            mainActivityViewModel,
            "recently_played-all",
            false
        )
        binding.recentlyPlayed.adapter = recentlyPlayedAdapter
    }

}