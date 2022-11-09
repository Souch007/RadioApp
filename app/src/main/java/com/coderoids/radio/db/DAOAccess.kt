package com.coderoids.radio.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coderoids.radio.ui.radioplayermanager.episodedata.Data

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineEpisode(data : Data)

    @Query("SELECT * FROM OfflineEpisodes")
    fun getOfflineEpisodes(): List<Data>
}