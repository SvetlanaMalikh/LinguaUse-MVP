package com.example.lingua_use_mvp.model

data class WordCard(
    val word: String,
    val correctTranslation: String,
    val wrongTranslations: List<String>
)