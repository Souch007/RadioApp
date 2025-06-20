package com.netcast.radio.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.netcast.radio.interfaces.ListAdapterItem
import kotlin.math.log

abstract class BaseAdapter<BINDING : ViewDataBinding, T : ListAdapterItem>(var data: List<T>) :
    RecyclerView.Adapter<BaseViewHolder<BINDING>>() {

    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun bind(binding: BINDING, item: T, position: Int)

    abstract fun getItemsCount(data: List<T>): Int

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
        try {
            bind(holder.binder, data[position], position)
            AppSingelton.currentPlayingPos = position
        } catch (e: Exception) {
            Log.d("BindingHolder exception", "onBindViewHolder: "+e.printStackTrace())
        }
    }

    override fun getItemCount(): Int {
        return getItemsCount(data)
    }


}