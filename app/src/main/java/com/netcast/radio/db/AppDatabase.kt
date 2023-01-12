package com.netcast.radio.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.netcast.radio.ui.radioplayermanager.episodedata.Data

@Database(entities = arrayOf(Data::class), version = 6, exportSchema = false)
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