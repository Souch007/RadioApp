package com.coderoids.radio.ui.favourites.adapters

import com.coderoids.radio.PlayingChannelData
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.FavRowBinding
import com.coderoids.radio.databinding.RadioRowBinding
import com.coderoids.radio.ui.radio.data.temp.RadioLists

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
        if(data.size >5)
            return 5;
        else
            return data.size;
    }
}

interface OnFavouriteClickListener {
    fun onFavChannelClicked(playingChannelData: PlayingChannelData)
}