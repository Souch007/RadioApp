package com.netcast.radio.ui.radio.data.temp


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.netcast.radio.db.Converters

@Keep
@Entity(tableName = "RadioStations")
@TypeConverters(Converters::class)
data class Data(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "classical", defaultValue = "")
    @SerializedName("Dance")
    val classical: List<RadioLists>,
    @ColumnInfo(name = "music", defaultValue = "")
    @SerializedName("Other")
    val music: List<RadioLists>,
    @ColumnInfo(name = "news", defaultValue = "")
    @SerializedName("Talk")
    val news: List<RadioLists>,
    @ColumnInfo(name = "pop", defaultValue = "")
    @SerializedName("Pop")
    val pop: List<RadioLists>,
    @ColumnInfo(name = "publicRadio", defaultValue = "")
    @SerializedName("publicRadio")
    val publicRadio: List<RadioLists>,
//    @SerializedName("estación")
//    val estación: List<RadioLists>,
//    @SerializedName("méxico")
//    val méxico: List<RadioLists>,
//    @SerializedName("radio")
//    val radio: List<RadioLists>,
//    @SerializedName("Rock")
//    val rock: List<RadioLists>,
//    @SerializedName("podcasts")
//    val podcasts: List<podcastsItem>,
//    @SerializedName("Decades")
//    val talk: List<RadioLists>
)