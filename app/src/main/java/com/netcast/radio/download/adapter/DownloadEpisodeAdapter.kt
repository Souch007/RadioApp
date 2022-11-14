package com.netcast.radio.download.adapter

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.DownloadRowBinding
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

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