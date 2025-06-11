package com.example.linguause_mvp.model

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room

@Database(entities = [WordCard::class, WordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordCardDao(): WordCardDao
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "linguause_database" // ✅ Можно переименовать при необходимости
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}

