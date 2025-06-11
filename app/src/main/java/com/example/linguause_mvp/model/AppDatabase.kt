package com.example.linguause_mvp.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WordCard::class, WordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordCardDao(): WordCardDao
    abstract fun wordDao(): WordDao
}