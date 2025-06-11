package com.example.linguause_mvp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Представляет одно слово в словаре.
 * Используется в упражнениях и хранится в Room.
 */
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val word: String,               // само слово (например: leave)
    val translation: String,        // перевод (например: уходить)
    val transcription: String,      // транскрипция ([liːv])
    val partOfSpeech: String,       // часть речи (verb, noun, adjective)
    val level: String,              // уровень (A1, B2, C1 и т.д.)
    val topic: String,              // тема (например: travel, family)

    val isStudying: Boolean = false,       // сейчас изучается (временно false, станет true после запуска в игре)
    val isInVocabulary: Boolean = true     // активный словарь (временно всегда true — TODO: сделать позже)
)