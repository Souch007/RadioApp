package com.baidu.netcast.ui.radio.adapter

import android.annotation.SuppressLint
import android.view.View
import com.google.gson.Gson
import com.baidu.netcast.PlayingChannelData
import com.baidu.netcast.R
import com.baidu.netcast.base.AppSingelton
import com.baidu.netcast.base.BaseAdapter
import com.baidu.netcast.databinding.RecentplayerRowBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllRecentPlayedAdapter(
    private val list: List<PlayingChannelData>,
    private val onFavouriteClickListener: OnAllRecentPlayerClickListener,
    private val type: String,
    private var isCheckBoxesEnable: Boolean
) : BaseAdapter<RecentplayerRowBinding, PlayingChannelData>(list) {
    override val layoutId: Int = R.layout.recentplayer_row
    private var selectedItems = mutableListOf<PlayingChannelData>()

    @SuppressLint("SuspiciousIndentation")
    override fun bind(binding: RecentplayerRowBinding, item: PlayingChannelData, position: Int) {
        val currentItem = item
        binding.apply {
            playing = item
            tabtype = type
            listener = onFavouriteClickListener
            executePendingBindings()
        }
        binding.checkboxChecked.setOnClickListener {
            currentItem.isSelected = !currentItem.isSelected
            toggleSelection(currentItem)
        }
        // Highlight selected items
        binding.checkboxChecked.isChecked = currentItem.isSelected

        if (isCheckBoxesEnable) {
            binding.checkboxChecked.visibility = View.VISIBLE
        } else
            binding.checkboxChecked.visibility = View.GONE
//        if (type == "favourites") binding.layoutDelete.visibility = View.VISIBLE
    }

    override fun getItemsCount(data: List<PlayingChannelData>): Int {
        return data.size
    }

    fun enbaleCheckboxes(enable: Boolean) {
        isCheckBoxesEnable = enable
        notifyDataSetChanged()
    }

    private fun toggleSelection(item: PlayingChannelData) {
        if (item.isSelected) {
            selectedItems.add(item)
        } else {
            selectedItems.remove(item)
        }
    }

    fun deleteSelectedItems() {
        for (i in selectedItems) {
            AppSingelton.recentlyPlayedArray.remove(i)
        }

        val gson = Gson()
        val json = gson.toJson(AppSingelton.recentlyPlayedArray)
        if (json != null) {
            CoroutineScope(Dispatchers.Main).launch {
                AppSingelton.isNewItemAdded.value = true
                notifyDataSetChanged()
            }

        }

    }
}

interface OnAllRecentPlayerClickListener {
    fun onChannelClicked(playingChannelData: PlayingChannelData, tabtype: String)

}
