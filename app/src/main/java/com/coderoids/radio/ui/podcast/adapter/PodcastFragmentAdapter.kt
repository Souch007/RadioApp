package com.coderoids.radio.ui.podcast.adapter


import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.PodcastRowBinding
import com.coderoids.radio.databinding.RadioRowBinding
import com.coderoids.radio.ui.podcast.data.Feed
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FileExistsException

class PodcastFragmentAdapter(
    private val list: List<Feed>,
    private val _onClickListenerPodcast: OnClickListenerPodcast
) : BaseAdapter<PodcastRowBinding, Feed>(list) {
    override val layoutId: Int = R.layout.podcast_row

    override fun bind(binding: PodcastRowBinding , item: Feed) {
        binding.apply {
            feeds = item
            listener = _onClickListenerPodcast
            executePendingBindings()
        }
    }
}
interface OnClickListenerPodcast {
    fun onPodCastClicked(data: Feed)
}