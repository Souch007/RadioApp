package com.netcast.radio.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netcast.radio.ui.radio.data.temp.RadioLists
import com.netcast.radio.ui.radio.data.temp.RadioResponse
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineEpisode(data : Data)

    @Query("SELECT * FROM OfflineEpisodes")
    fun getOfflineEpisodes(): List<Data>

    @Query("SELECT * FROM OfflineEpisodes WHERE id=:id")
    fun getOfflineEpisodeById(id: String): Data

    @Query("DELETE FROM OfflineEpisodes WHERE id=:id")
    fun deleteOfflineEpisodeById(id: String) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadioStations(data :com.netcast.radio.ui.radio.data.temp.Data)

    @Query("SELECT * FROM RadioStations")
    fun getRadioData(): com.netcast.radio.ui.radio.data.temp.Data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodCast(data :com.netcast.radio.ui.podcast.poddata.Data)

    @Query("SELECT * FROM PodStations")
    fun getPodData(): com.netcast.radio.ui.podcast.poddata.Data?


}