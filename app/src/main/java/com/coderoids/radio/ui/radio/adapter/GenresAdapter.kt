package com.coderoids.radio.ui.radio.adapter

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.GenresRowBinding
import com.coderoids.radio.ui.radio.genres.Data

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