package com.coderoids.radio.ui.radio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.coderoids.radio.R
import com.coderoids.radio.ui.radio.data.temp.RadioLists
import com.coderoids.radio.ui.radio.data.temp.podcastsItem
import com.makeramen.roundedimageview.RoundedImageView

class DotIndicatorAdapter(var publicRadio: List<podcastsItem>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = LayoutInflater.from(container.context).inflate(R.layout.material_page, container, false)
        var textPodcastName =  item.findViewById<TextView>(R.id.podcast_title_r)
        var imageItemPodcast =  item.findViewById<RoundedImageView>(R.id.item_image)
        var podcastLocation =  item.findViewById<TextView>(R.id.podcast_location_r)
        textPodcastName.text = publicRadio.get(position).title
        podcastLocation.text = publicRadio.get(position).author
        Glide.with(container.context)
            .load(publicRadio.get(position).image)
            .error(R.drawable.logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .into(imageItemPodcast)
        container.addView(item)
        return item
    }

    override fun getCount(): Int {
        if(publicRadio.size >= 5)
            return 5;
        else
            return publicRadio.size;
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}