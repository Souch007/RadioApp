package com.netcast.baidutv.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netcast.baidutv.ui.radioplayermanager.episodedata.Data

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
    suspend fun insertRadioStations(data :com.netcast.baidutv.ui.radio.data.temp.Data)

    @Query("SELECT * FROM RadioStations")
    fun getRadioData(): com.netcast.baidutv.ui.radio.data.temp.Data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodCast(data :com.netcast.baidutv.ui.podcast.poddata.Data)

    @Query("SELECT * FROM PodStations")
    fun getPodData(): com.netcast.baidutv.ui.podcast.poddata.Data


}