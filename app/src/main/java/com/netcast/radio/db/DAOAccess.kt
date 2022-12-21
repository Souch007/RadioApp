package com.netcast.radio.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineEpisode(data : Data)

    @Query("SELECT * FROM OfflineEpisodes")
    fun getOfflineEpisodes(): List<Data>

    @Query("SELECT * FROM OfflineEpisodes WHERE id=:id")
    fun getOfflineEpisodeById(id: Long): Data

    @Query("DELETE FROM OfflineEpisodes WHERE id=:id")
    fun deleteOfflineEpisodeById(id: Long) : Int
}