package com.example.linguause_mvp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordCardDao {

    @Query("SELECT * FROM word_cards")
    suspend fun getAllWords(): List<WordCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordCard>)
}
