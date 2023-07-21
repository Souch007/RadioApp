package com.netcast.radio.ui.radio.adapter

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.GenresRowBinding
import com.netcast.radio.ui.radio.genres.Data

class GenresAdapter(
    private val list: List<Data>, private val onClickGeneresListener: OnClickGeneresListener
) : BaseAdapter<GenresRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.genres_row
    var drawable: Int = 0

    override fun bind(binding: GenresRowBinding, item: Data) {

        binding.apply {
            genresList = item
            listener = onClickGeneresListener
            executePendingBindings()
        }

        if (item.name.equals("Rock", true)) {
            drawable = R.drawable.rock
        } else if (item.name.equals("Pop", true)) {
            drawable = R.drawable.pop
        } else if (item.name.equals("Classical", true)) {
            drawable = R.drawable.classical
        } else if (item.name.equals("México", true)) {
            drawable = R.drawable.mexico
        } else if (item.name.equals("Talk", true)) {
            drawable = R.drawable.talk
        } else if (item.name.equals("Music", true)) {
            drawable = R.drawable.music
        } else if (item.name.equals("News", true)) {
            drawable = R.drawable.news
        } else if (item.name.equals("Estación", true)) {
            drawable = R.drawable.estacion
        }
        else if (item.name.equals("Radio", true)) {
            drawable = R.drawable.logo
        }

        binding.img.setImageResource(drawable)
    }

    override fun getItemsCount(data: List<Data>): Int {
        if (data.size > 10) return 9
        else return data.size;
    }

}

interface OnClickGeneresListener {
    fun OnClickGenres(data: Data)
}