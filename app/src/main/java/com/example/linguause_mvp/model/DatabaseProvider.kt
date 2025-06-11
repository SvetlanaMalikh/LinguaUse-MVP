package com.example.linguause_mvp.model

import android.content.Context
import androidx.room.Room
import com.example.linguause_mvp.model.AppDatabase



object DatabaseProvider {

    fun getDao(context: Context): WordDao {
        return AppDatabase.getInstance(context).wordDao()
    }

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "linguause_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}