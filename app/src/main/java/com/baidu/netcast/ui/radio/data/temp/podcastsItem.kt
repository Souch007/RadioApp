package com.baidu.netcast.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import org.json.JSONObject

@Keep
data class podcastsItem(
    @SerializedName("audio_length_sec")
    val audioLengthSec: Int,
    @SerializedName("country")
    val country: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("earliest_pub_date_ms")
    val earliestPubDateMs: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("explicit_content")
    val explicitContent: Boolean,
    @SerializedName("extra")
    val extra: JSONObject,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("_id")
    val _id: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("is_claimed")
    val isClaimed: Boolean,
    @SerializedName("itunes_id")
    val itunesId: Int,
    @SerializedName("language")
    val language: String,
    @SerializedName("latest_episode_id")
    val latestEpisodeId: String,
    @SerializedName("latest_pub_date_ms")
    val latestPubDateMs: Long,
    @SerializedName("listen_score")
    val listenScore: String,
    @SerializedName("listen_score_global_rank")
    val listenScoreGlobalRank: String,
    @SerializedName("listennotes_url")
    val listennotesUrl: String,
    @SerializedName("looking_for")
    val lookingFor: JSONObject,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("rss")
    val rss: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_episodes")
    val totalEpisodes: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("update_frequency_hours")
    val updateFrequencyHours: Int,
    @SerializedName("website")
    val website: String
)