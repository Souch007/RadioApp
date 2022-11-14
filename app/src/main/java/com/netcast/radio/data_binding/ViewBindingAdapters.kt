package com.netcast.radio.data_binding

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.netcast.radio.R
import com.netcast.radio.base.BaseAdapter
import com.netcast.radio.interfaces.ListAdapterItem
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
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
@BindingAdapter("setAdapter")
fun setAdapter(
    recyclerView: ShimmerRecyclerView,
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

@BindingAdapter("setPlayerVisibility")
fun setPlayerVisibility(linearLayout: LinearLayout, isVisibile: Boolean){
    if(isVisibile)
    linearLayout.visibility = View.VISIBLE
    else
        linearLayout.visibility = View.GONE


}

@BindingAdapter("setPlayer")
fun setPlayer(playerControlView: PlayerControlView, exoPlayer: ExoPlayer?){
        playerControlView.player = exoPlayer;
}

@BindingAdapter("setVisibilityLinear")
fun setVisibilityLinear(linearLayout: LinearLayout, string: String){
    if(string.matches("".toRegex())){
        linearLayout.visibility = View.GONE
    } else
        linearLayout.visibility = View.VISIBLE
}

@BindingAdapter("setVisibilityLinear")
fun setVisibilityLinear(linearLayout: LinearLayout, visibility: Boolean){
    if(!visibility){
        linearLayout.visibility = View.GONE
    } else
        linearLayout.visibility = View.VISIBLE
}

@BindingAdapter("setTextHtml")
fun setTextHTML(textView : TextView, text : String){
        textView.setText(HtmlCompat.fromHtml(text?:"", HtmlCompat.FROM_HTML_MODE_COMPACT))
}