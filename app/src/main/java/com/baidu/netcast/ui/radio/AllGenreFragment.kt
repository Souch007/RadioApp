package com.baidu.netcast.ui.radio

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.baidu.netcast.MainViewModel
import com.baidu.netcast.R
import com.baidu.netcast.base.BaseFragment
import com.baidu.netcast.databinding.FragmentAllgenreBinding
import com.baidu.netcast.request.Resource

class AllGenreFragment() :
    BaseFragment<FragmentAllgenreBinding>(R.layout.fragment_allgenre) {

    private lateinit var mainActivityViewModel: MainViewModel
    private val TAG = "FilterRadioFragment"
    val radioViewModel: RadioViewModel by activityViewModels()

    override fun FragmentAllgenreBinding.initialize() {
        binding.viewmodel=radioViewModel
        activity.let {
            mainActivityViewModel = ViewModelProvider(it!!)[MainViewModel::class.java]
        }
        binding.ivBack.setOnClickListener {
            mainActivityViewModel._radioSeeAllSelected.value = "CLOSE"
        }


        radioViewModel._genresListinLive.observe(viewLifecycleOwner) {
            try {
                val data = (it as Resource.Success).value.data
                binding.genresAdapter = com.baidu.netcast.ui.radio.adapter.GenresAdapter(
                    listOf(),
                    mainActivityViewModel,
                   "all"
                )
                radioViewModel._genresListArray.value = data
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
 /*       mainActivityViewModel._queriedSearched.observe(viewLifecycleOwner) {
            it?.let {
                val args = Bundle()
                args.putString("filter_tag", it)
                findNavController().navigate(R.id.navigation_filterstaions,args)

            }
        }*/

    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getAllGenres(radioViewModel)
        binding.tvFilter.text = "All Genre"
    }

}