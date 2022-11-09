package com.coderoids.radio.download.adapter

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.DownloadRowBinding
import com.coderoids.radio.databinding.PodcastRowBinding
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data

class DownloadEpisodeAdapter(
    private val list: List<Data>,
    private val _onClickListenerPodcast: OnClickEpisodeDownload
) : BaseAdapter<DownloadRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.download_row

    override fun bind(binding: DownloadRowBinding, item: Data) {
        binding.apply {
            episode = item
            listener = _onClickListenerPodcast
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
        return data.size;
    }

}
interface OnClickEpisodeDownload {
    fun onDownloadedEpisodeClicked(data: Data)
}