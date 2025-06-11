package com.example.linguause_mvp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_cards")
data class WordCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val translation: String,
    val type: String // "noun", "verb" или "adjective"
)