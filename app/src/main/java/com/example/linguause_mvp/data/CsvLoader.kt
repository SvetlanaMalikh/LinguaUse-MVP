package com.example.linguause_mvp.data

import android.content.Context
import com.example.linguause_mvp.model.WordEntity

object CsvLoader {
    fun loadWordsFromAssets(context: Context): List<WordEntity> {
        val words = mutableListOf<WordEntity>()
        val inputStream = context.assets.open("words.csv")
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(",")
                if (parts.size == 3) {
                    val word = parts[0].trim()
                    val translation = parts[1].trim()
                    val type = parts[2].trim()
                    words.add(WordEntity(word = word, translation = translation, type = type))
                }
            }
        }
        return words
    }
}