package com.netcast.baidutv.ui.search.adapters

import com.netcast.baidutv.R
import com.netcast.baidutv.base.BaseAdapter
import com.netcast.baidutv.databinding.TagsRowBinding
import com.netcast.baidutv.ui.search.frequentsearch.Data

class SearchTagsAdapter(private val list:List<Data>,private val onSearcTagListener : OnSearchTagListener)
    : BaseAdapter<TagsRowBinding,Data>(list){
    override val layoutId: Int = R.layout.tags_row
    override fun bind(binding: TagsRowBinding, item: Data, position: Int) {
        binding.apply {
            tagsdata = item
            listener = onSearcTagListener
            executePendingBindings()
        }
    }

    override fun getItemsCount(data: List<Data>): Int {
        return if(data.size > 10)
            10
        else
            data.size
    }

}

interface OnSearchTagListener{
    fun onSearchTagClicked(data:Data)
}