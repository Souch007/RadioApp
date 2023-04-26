package com.netcast.radio.ui.favourites.adapters

import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.FavRowBinding

class FavouriteAdapter(private val list: List<PlayingChannelData>,
private val onFavouriteClickListener : OnFavouriteClickListener) :  BaseAdapter<FavRowBinding, PlayingChannelData>(list)  {
    override val layoutId: Int = R.layout.fav_row

    override fun bind(binding: FavRowBinding, item: PlayingChannelData) {
        binding.apply {
            playing = item
            listener = onFavouriteClickListener
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<PlayingChannelData>): Int {
       /* if(data.size >5)
            return 5;
        else*/
            return data.size
    }
}

interface OnFavouriteClickListener {
    fun onFavChannelClicked(playingChannelData: PlayingChannelData)
    fun onFavChannelDeleteClicked(playingChannelData: PlayingChannelData)
}