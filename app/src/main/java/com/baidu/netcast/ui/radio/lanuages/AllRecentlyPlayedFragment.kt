package com.baidu.netcast.ui.radio.lanuages

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.R
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.BaseFragment
import com.baidu.netcast.databinding.FragmentAllrecentplayedBinding
import com.baidu.netcast.ui.radio.adapter.AllRecentPlayedAdapter
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
//                recentlyPlayedAdapter.enbaleCheckboxes(false)
                binding.titleDelete.text = "Edit"
//                dataBinding.downloadRv.adapter?.notifyDataSetChanged()
                /* Handler(Looper.myLooper()!!).postDelayed({
                    dataBinding.adapter?.notifyDataSetChanged()
                 }, 2000)*/


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