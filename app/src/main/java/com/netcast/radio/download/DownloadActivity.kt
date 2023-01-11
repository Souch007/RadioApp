package com.netcast.radio.download

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import kotlinx.coroutines.launch

class DownloadActivity : BaseActivity<DownloadViewModel,ActivityDownloadBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSingelton.currentActivity = AppConstants.DownloadActivity
        observers()
        manageOfflineData()


        dataBinding.ivBack.setOnClickListener {
            finish()
        }
        Handler(Looper.getMainLooper()).postDelayed({
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
        }, 3000)

    }

    private fun manageOfflineData() {
        CoroutineScope(Dispatchers.IO).launch {
            val listOfEpisodes = getOfflineData()
            if(listOfEpisodes != null && listOfEpisodes.size > 0)
                viewModel._listDownloadedEpisodes.postValue(listOfEpisodes)
        }
    }

    private fun observers() {
        AppSingelton._progressPublish.observe(this@DownloadActivity){
            if(it != null) {
                dataBinding.tvDownlaodTag.text = "Downloading " + it.toString() + " %"
                dataBinding.progressBar.setProgress(it)
                if (it == 100) {
                    AppSingelton._progressPublish.value = null
                    dataBinding.tvDownlaodTag.setText("Downloaded")
                    dataBinding.cardDownload.visibility = View.GONE
                }
            }
        }

        AppSingelton._onDownloadCompletion.observe(this@DownloadActivity){
            insertOfflineData(data = it)
            Handler(Looper.getMainLooper()).postDelayed({
                manageOfflineData()
            }, 3000)
        }

        viewModel._listDownloadedEpisodes.observe(this@DownloadActivity){
            dataBinding.adapter = DownloadEpisodeAdapter(it, viewModel)
        }

        viewModel.onDownloadPlayListner.observe(this@DownloadActivity){
                val playingChannelData = PlayingChannelData(
                    it.fileURI,
                    it.feedImage,
                    it.title,
                    it.id,
                    it.guidFromRss,
                    it.description,
                    "Offline"
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