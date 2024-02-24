package com.netcast.radio.download

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.netcast.radio.BR
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.AppSingelton
import com.netcast.radio.base.BaseActivity
import com.netcast.radio.databinding.ActivityDownloadBinding
import com.netcast.radio.download.adapter.DownloadEpisodeAdapter
import com.netcast.radio.request.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DownloadActivity : BaseActivity<DownloadViewModel, ActivityDownloadBinding>() {
    private lateinit var downlaodEpisodeAdapter: DownloadEpisodeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppSingelton.currentActivity = AppConstants.DownloadActivity
        observers()
        manageOfflineData()

        dataBinding.titleDelete.setOnClickListener {
            if (dataBinding.titleDelete.text.equals("Edit")) {
                dataBinding.titleDelete.text = "Delete"
                downlaodEpisodeAdapter.enbaleCheckboxes(true)

            } else {
                GlobalScope.launch {
                    downlaodEpisodeAdapter.deleteSelectedItems(appDatabase)
                }
                downlaodEpisodeAdapter.enbaleCheckboxes(false)
                dataBinding.titleDelete.text = "Edit"
//                dataBinding.downloadRv.adapter?.notifyDataSetChanged()
                /* Handler(Looper.myLooper()!!).postDelayed({
                    dataBinding.adapter?.notifyDataSetChanged()
                 }, 2000)*/


            }
        }

        dataBinding.ivBack.setOnClickListener {
            finish()
        }

        /*   Handler(Looper.getMainLooper()).postDelayed({
               if(AppSingelton.downloadingEpisodeData != null){
                   Glide.with(applicationContext)
                       .load(AppSingelton.downloadingEpisodeData!!.feedImage)
                       .error(R.drawable.logo)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)
                       .priority(Priority.HIGH)
                       .into(dataBinding.ivChannelPod)
                   dataBinding.channelName.text = AppSingelton.downloadingEpisodeData!!.title
                   dataBinding.location.text = Html.fromHtml((AppSingelton.downloadingEpisodeData!!.description))
                   dataBinding.cardDownload.visibility = View.VISIBLE
               } else {
                   dataBinding.cardDownload.visibility = View.GONE
               }
           }, 3000)*/

    }

    private fun manageOfflineData() {
        CoroutineScope(Dispatchers.IO).launch {
            val listOfEpisodes = getOfflineData()
            if (listOfEpisodes != null && listOfEpisodes.isNotEmpty()) {
                viewModel._listDownloadedEpisodes.postValue(listOfEpisodes)
                dataBinding.titleDelete.visibility = View.VISIBLE
            } else {
                dataBinding.titleDownload.visibility=View.VISIBLE
                dataBinding.titleDownload.text = "No Data Found"
            }
        }
    }

    private fun observers() {
        AppSingelton._progressPublish.observe(this@DownloadActivity) {
            if (it != null) {
                dataBinding.tvDownlaodTag.text = "Downloading " + it.toString() + " %"
                dataBinding.progressBar.progress = it
                if (it == 100) {
                    AppSingelton._progressPublish.value = null
                    dataBinding.tvDownlaodTag.setText("Downloaded")
                    dataBinding.cardDownload.visibility = View.GONE
                }
            }
        }

        AppSingelton._onDownloadCompletion.observe(this@DownloadActivity) {
            insertOfflineData(data = it)
            Handler(Looper.getMainLooper()).postDelayed({
                manageOfflineData()
            }, 3000)
        }
        viewModel._listDownloadedEpisodes.observe(this@DownloadActivity) {
            it?.let {
                downlaodEpisodeAdapter =
                    DownloadEpisodeAdapter(it.toMutableList(), viewModel, false)
                dataBinding.downloadRv.adapter = downlaodEpisodeAdapter
            }
        }

        viewModel.onDownloadPlayListner.observe(this@DownloadActivity) {
            val playingChannelData = PlayingChannelData(
                it.fileURI,
                it.feedImage,
                it.title,
                it.id,
                it.guidFromRss,
                it.description,
                "Offline",
                secondaryUrl = ""
            )
            AppSingelton._radioSelectedChannel.value = playingChannelData
            finish()
        }
    }

    override fun onBackPressed() {

    }


    override val layoutRes: Int
        get() = R.layout.activity_download
    override val bindingVariable: Int
        get() = BR.downloadVM
    override val viewModelClass: Class<DownloadViewModel>
        get() = DownloadViewModel::class.java
}