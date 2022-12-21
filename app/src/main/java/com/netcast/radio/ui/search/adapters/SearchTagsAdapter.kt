package com.netcast.radio.ui.search.adapters

import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.TagsRowBinding
import com.netcast.radio.ui.search.frequentsearch.Data

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
        if(data.size > 10)
            return 10
        else
            return data.size;
    }

}


interface OnSearchTagListener{
    fun onSearchTagClicked(data:Data)
}