package com.netcast.radio.ui.seeall

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.netcast.radio.MainViewModel
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseFragment
import com.netcast.radio.databinding.FragmentSeeAllBinding
import com.netcast.radio.request.Resource
import com.netcast.radio.ui.radio.RadioViewModel
import com.netcast.radio.ui.radio.data.temp.RadioLists
import com.netcast.radio.ui.seeall.adapter.SeeAllAdapter
import com.netcast.radio.ui.seeall.adapter.SeeAllPodAdapter
import com.netcast.radio.util.EndLessLoading


class SeeAllFragment : BaseFragment<FragmentSeeAllBinding>(R.layout.fragment_see_all) {
    val seeAllViewModel: SeeAllViewModel by activityViewModels()
    private lateinit var mainActivityViewModel: MainViewModel
    private var seeAllAdapter: SeeAllAdapter? = null
    private var seeAllPodAdapter: SeeAllPodAdapter? = null
    private var page = 1
    val radioViewModel: RadioViewModel by activityViewModels()
    private var myradioList: MutableList<RadioLists> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun FragmentSeeAllBinding.initialize() {
        binding.lifecycleOwner = this@SeeAllFragment
        binding.seeallviewmodel = seeAllViewModel

        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.mainViewModel = mainActivityViewModel
//        setRVscrolllistener()
        mainActivityViewModel._selectedSeeAllListRadio.observe(this@SeeAllFragment) {
            it?.let { radioList ->
                myradioList.addAll(radioList)
                binding.radioRv.visibility = View.VISIBLE
                binding.podcastRv.visibility = View.GONE
                seeAllAdapter = SeeAllAdapter(
                    myradioList,
                    mainActivityViewModel,
                    mainActivityViewModel._radioSelectedTitle.value
                )
                binding.seeallaadapter = seeAllAdapter
            }

        }
 /*       radioViewModel.radioListing.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {}
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    myradioList.addAll(it.value.data.classical)
                    Toast.makeText(requireContext(), myradioList.size.toString(), Toast.LENGTH_SHORT).show()
                    seeAllAdapter?.notifyDataSetChanged()
                }
            }
        }*/

        mainActivityViewModel._selectedSeeAllPodcasts.observe(this@SeeAllFragment) {
            binding.podcastRv.visibility = View.VISIBLE
            binding.radioRv.visibility = View.GONE
            seeAllPodAdapter = SeeAllPodAdapter(
                it,
                mainActivityViewModel,
                mainActivityViewModel._radioSelectedTitle.value,
            )
            binding.seeallpodadapter = seeAllPodAdapter
        }

        AppSingelton._isFavUpdated.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    seeAllAdapter?.notifyDataSetChanged()
                    seeAllPodAdapter?.notifyDataSetChanged()
                }

            }
        }

        binding.ivBack.setOnClickListener {
            if (mainActivityViewModel._radioSeeAllSelected.value == "PODCAST")
                mainActivityViewModel._radioSeeAllSelected.value = "CLOSE_PODCAST"
            else
                mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }
        binding.tvChannelName.text = mainActivityViewModel._radioSelectedTitle.value
//        setRVscrolllistener()

    }

 /*   private fun setRVscrolllistener() {
        binding.radioRv.apply {
            addOnScrollListener(object : EndLessLoading() {
                override fun onLoadMore() {
                    page += 1
                    mainActivityViewModel.getRadioListing(radioViewModel = radioViewModel, "")

                }
            })
        }

    }*/
}