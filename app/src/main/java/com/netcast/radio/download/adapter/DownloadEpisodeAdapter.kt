package com.netcast.radio.download.adapter

import android.view.View
import com.downloader.Progress
import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.databinding.DownloadRowBinding
import com.netcast.radio.db.AppDatabase
import com.netcast.radio.ui.radioplayermanager.episodedata.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File



class DownloadEpisodeAdapter(
    private var list: MutableList<Data>,
    private val _onClickListenerPodcast: OnClickEpisodeDownload,
    private var isCheckBoxesEnable: Boolean
) : BaseAdapter<DownloadRowBinding, Data>(list) {
    private var selectedItems = mutableListOf<Data>()



    override val layoutId: Int = R.layout.download_row

    override fun bind(binding: DownloadRowBinding, item: Data, position: Int) {

        binding.apply {
            episode = item
            listener = _onClickListenerPodcast
            executePendingBindings()
        }
        /*      binding.checkboxChecked.setOnCheckedChangeListener { buttonView, isChecked ->
                  currentItem.isSelected = !currentItem.isSelected
                  toggleSelection(currentItem)
              }*/

        binding.checkboxChecked.setOnClickListener {
            item.isSelected = !item.isSelected
            toggleSelection(item)
        }
        // Highlight selected items
        binding.checkboxChecked.isChecked = item.isSelected

        if (isCheckBoxesEnable) {
            binding.checkboxChecked.visibility = View.VISIBLE
        } else
            binding.checkboxChecked.visibility = View.GONE

    }





    override fun getItemsCount(data: List<Data>): Int {
        return data.size
    }

    private fun toggleSelection(item: Data) {
        if (item.isSelected) {
            selectedItems.add(item)
        } else {
            selectedItems.remove(item)
        }
    }

    fun contentList(datalist: MutableList<Data>) {
        list = datalist
        notifyDataSetChanged()
    }

    // Delete selected items
    suspend fun deleteSelectedItems(appDatabase: AppDatabase?) {
        try {
            val job = CoroutineScope(Dispatchers.IO).async {
                for (i in selectedItems) {
                    var data = appDatabase!!.appDap().getOfflineEpisodeById(i.id)
                    val fileUr = data.fileURI
                    val fdelete: File = File(fileUr)
                    if (fdelete.exists()) {
                        appDatabase?.appDap()?.deleteOfflineEpisodeById(i.id)
                        fdelete.delete()
                    } else {
                        appDatabase?.appDap()?.deleteOfflineEpisodeById(i.id)
                    }

                }
            }
            job.await()
            list.removeAll(selectedItems)
            selectedItems.clear()
            CoroutineScope(Dispatchers.Main).launch {
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun enbaleCheckboxes(enable: Boolean) {
        isCheckBoxesEnable = enable
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}



fun toPercent(progress: Progress) =
    (100 * (progress.currentBytes / progress.totalBytes.toDouble())).toInt().toByte()

interface OnClickEpisodeDownload {
    fun onDownloadedEpisodeClicked(data: Data)
}
