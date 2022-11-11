package com.coderoids.radio.download

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.coderoids.radio.BR
import com.coderoids.radio.PlayingChannelData
import com.coderoids.radio.R
import com.coderoids.radio.base.AppSingelton
import com.coderoids.radio.base.BaseActivity
import com.coderoids.radio.databinding.ActivityDownloadBinding
import com.coderoids.radio.download.adapter.DownloadEpisodeAdapter
import com.coderoids.radio.request.AppConstants
import com.coderoids.radio.ui.podcast.adapter.PodcastFragmentAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class DownloadActivity : BaseActivity<DownloadViewModel,ActivityDownloadBinding>() {
    private var objectAnimator : ObjectAnimator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSingelton.currentActivity = AppConstants.DownloadActivity
        if(AppSingelton.downloadingEpisodeData != null){
            Glide.with(this)
                .load(AppSingelton.downloadingEpisodeData!!.image)
                .error(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(dataBinding.ivChannelPod)
            dataBinding.channelName.setText(AppSingelton.downloadingEpisodeData!!.title)
        } else {
            dataBinding.cardDownload.visibility = View.GONE
        }
        observers()
        manageOfflineData()
        dataBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun manageOfflineData() {
        CoroutineScope(Dispatchers.IO).launch {
            var listOfEpisodes = getOfflineData()
            viewModel._listDownloadedEpisodes.postValue(listOfEpisodes)
        }
    }

    private fun observers() {
        AppSingelton._progressPublish.observe(this@DownloadActivity){
            dataBinding.tvDownlaodTag.setText("Downloading " + it.toString() +" %")
            dataBinding.progressBar.setProgress(it)
            if(it== 100){
                dataBinding.tvDownlaodTag.setText("Downloaded")
                dataBinding.cardDownload.visibility = View.GONE
                dataBinding.adapter!!.notifyDataSetChanged()
            }
        }

        AppSingelton._onDownloadCompletion.observe(this@DownloadActivity){
            insertOfflineData(data = it)
            manageOfflineData()

        }

        viewModel._listDownloadedEpisodes.observe(this@DownloadActivity){
            dataBinding.adapter = DownloadEpisodeAdapter(it, viewModel)
        }

        viewModel.onDownloadPlayListner.observe(this@DownloadActivity){
                val playingChannelData = PlayingChannelData(
                    it.fileURI,
                    it.feedImage,
                    it.title,
                    it._id.toString(),
                    it.feedId.toString(),
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