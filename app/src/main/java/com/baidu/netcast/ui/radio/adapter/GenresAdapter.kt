package com.baidu.netcast.ui.radio.adapter

import com.baidu.netcast.R
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.GenresRowBinding
import com.baidu.netcast.ui.radio.genres.Data

class GenresAdapter(
    private val list: List<Data>, private val onClickGeneresListener: OnClickGeneresListener,private val type:String
) : BaseAdapter<GenresRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.genres_row
    var drawable: Int = 0

    override fun bind(binding: GenresRowBinding, item: Data, position: Int) {

        binding.apply {
            genresList = item
            listener = onClickGeneresListener
            executePendingBindings()
        }

       /* if (item.name.equals("Rock", true)) {
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

        binding.img.setImageResource(drawable)*/
    }

    override fun getItemsCount(data: List<Data>): Int {
        return if (type=="all")
            data.size
        else if (data.size > 10) 9
        else data.size
    }

}

interface OnClickGeneresListener {
    fun OnClickGenres(data: Data)
}