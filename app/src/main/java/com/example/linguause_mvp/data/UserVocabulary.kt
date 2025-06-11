package com.example.linguause_mvp.data

import com.example.linguause_mvp.model.WordEntity
import com.example.linguause_mvp.model.WordDao

object UserVocabulary {
    // Список слов, которые знает пользователь (активный словарь)
    val knownWords = mutableListOf<WordEntity>()

    // Список слов, которые пользователь изучает
    val learningWords = mutableListOf<WordEntity>()

    // Метод для загрузки слов из базы и сохранения в память
    suspend fun initializeFromDatabase(dao: WordDao) {
        // Получаем слова с isInVocabulary = true (активный словарь)
        val activeWords = dao.getActiveVocabulary()

        // Очищаем старые данные
        knownWords.clear()

        // Добавляем новые слова
        knownWords.addAll(activeWords)
    }

    // Получаем объединённый список всех слов для игры
    fun getAllWordsForGame(): List<WordEntity> {
        return knownWords + learningWords
    }
}