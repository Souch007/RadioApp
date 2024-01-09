package com.netcast.baidutv.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netcast.baidutv.ui.radioplayermanager.episodedata.Data

@Database(entities = [Data::class,com.netcast.baidutv.ui.radio.data.temp.Data::class,com.netcast.baidutv.ui.podcast.poddata.Data::class], version = 13, exportSchema = false)
@TypeConverters(Converters::class, PodConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDap() : DAOAccess

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabaseClient(context: Context) : AppDatabase {
            if(INSTANCE != null) return INSTANCE!!
            synchronized(this){
                INSTANCE = Room
                    .databaseBuilder(context,AppDatabase::class.java, "netcast")
                    .fallbackToDestructiveMigration()
                    .build()
                return INSTANCE!!
            }
        }
    }
}