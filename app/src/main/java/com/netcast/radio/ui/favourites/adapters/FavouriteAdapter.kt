package com.netcast.radio.ui.favourites.adapters

import android.annotation.SuppressLint
import android.view.View
import com.netcast.radio.PlayingChannelData
import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.FavRowBinding

class FavouriteAdapter(
    private val list: List<PlayingChannelData>,
    private val onFavouriteClickListener: OnFavouriteClickListener,
    private val type: String
) : BaseAdapter<FavRowBinding, PlayingChannelData>(list) {
    override val layoutId: Int = R.layout.fav_row

    @SuppressLint("SuspiciousIndentation")
    override fun bind(binding: FavRowBinding, item: PlayingChannelData, position: Int) {
        binding.apply {
            playing = item
            tabtype = type
            listener = onFavouriteClickListener
            executePendingBindings()
        }
        if (type == "favourites") binding.layoutDelete.visibility = View.VISIBLE
    }

    override fun getItemsCount(data: List<PlayingChannelData>): Int {

        return if (type == "favourites") data.size
        else if (type == "recently_played") {
            if (data.size > 10) return 10
            else data.size
        } else {
            data.size
        }
    }
}

interface OnFavouriteClickListener {
    fun onFavChannelClicked(playingChannelData: PlayingChannelData, tabtype: String)
    fun onFavChannelDeleteClicked(playingChannelData: PlayingChannelData)
}