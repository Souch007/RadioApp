package com.netcast.radio.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.netcast.radio.interfaces.ListAdapterItem

abstract class BaseAdapter<BINDING : ViewDataBinding, T : ListAdapterItem>(var data: List<T>) : RecyclerView.Adapter<BaseViewHolder<BINDING>>() {

    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun bind(binding: BINDING, item: T)

    abstract fun getItemsCount(data: List<T>) : Int

    fun updateData(list: List<T>) {
        this.data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BINDING> {
        val binder = DataBindingUtil.inflate<BINDING>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )

        return BaseViewHolder(binder)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BINDING>, position: Int) {
        bind(holder.binder, data[position])
        AppSingelton.currentPlayingPos=position
    }

    override fun getItemCount(): Int {
        return getItemsCount(data)
    }

}