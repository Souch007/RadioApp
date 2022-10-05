package com.coderoids.radio.data_binding

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.coderoids.radio.R
import com.coderoids.radio.base.BaseAdapter
import com.coderoids.radio.interfaces.ListAdapterItem
import com.google.android.material.imageview.ShapeableImageView

@BindingAdapter("setAdapter")
fun setAdapter(
    recyclerView: RecyclerView,
    adapter: BaseAdapter<ViewDataBinding, ListAdapterItem>?
) {
    adapter?.let {
        recyclerView.adapter = it
    }
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("submitList")
fun submitList(recyclerView: RecyclerView, list: List<ListAdapterItem>?) {
    val adapter = recyclerView.adapter as BaseAdapter<ViewDataBinding, ListAdapterItem>?
    adapter?.updateData(list ?: listOf())
}

@BindingAdapter("manageState")
fun manageState(progressBar: ProgressBar, state: Boolean) {
    progressBar.visibility = if (state) View.VISIBLE else View.GONE
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("setImage")
fun setImage(imageView: ImageView, image: String) {
    Glide.with(imageView.context)
        .load(image)
        .error(R.drawable.logo)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)
        .into(imageView)
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("setImage")
fun setImage(imageView: ShapeableImageView, image: String) {
    Glide.with(imageView.context)
        .load(image)
        .error(R.drawable.logo)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)
        .into(imageView)
}

@BindingAdapter("setFavouriteCondition")
fun setFavouriteCondition(imageView: ShapeableImageView, isFavourite: Boolean) {
    if (isFavourite) {
        imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
    } else {
        imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
    }

}