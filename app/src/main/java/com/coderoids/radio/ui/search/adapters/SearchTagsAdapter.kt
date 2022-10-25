package com.coderoids.radio.ui.search.adapters

import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.databinding.TagsRowBinding
import com.coderoids.radio.ui.search.frequentsearch.Data

class SearchTagsAdapter(private val list:List<Data>,private val onSearcTagListener : OnSearchTagListener)
    : BaseAdapter<TagsRowBinding,Data>(list){
    override val layoutId: Int = R.layout.tags_row
    override fun bind(binding: TagsRowBinding, item: Data) {
        binding.apply {
            tagsdata = item
            listener = onSearcTagListener
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
        return data.size;
    }

}


interface OnSearchTagListener{
    fun onSearchTagClicked(data:Data)
}