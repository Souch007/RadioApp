package com.netcast.radio.ui.radio.adapter

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.GenresRowBinding
import com.netcast.radio.ui.radio.genres.Data

class GenresAdapter (
    private val list: List<Data>,
    private val onClickGeneresListener: OnClickGeneresListener
) : BaseAdapter<GenresRowBinding, Data>(list) {
    override val layoutId: Int = R.layout.genres_row

    override fun bind(binding: GenresRowBinding, item: Data) {
        binding.apply {
            genresList = item
            listener = onClickGeneresListener
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
        if(data.size > 10)
            return 9;
        else
            return data.size;
    }

}
interface OnClickGeneresListener {
    fun OnClickGenres(data: Data)
}