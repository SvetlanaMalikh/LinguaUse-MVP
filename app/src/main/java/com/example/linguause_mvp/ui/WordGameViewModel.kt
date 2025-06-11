package com.example.linguause_mvp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.linguause_mvp.data.CsvLoader
import com.example.linguause_mvp.model.DatabaseProvider
import com.example.linguause_mvp.model.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordGameViewModel(application: Application) : AndroidViewModel(application) {

    val currentWord = MutableLiveData<WordEntity>()
    val options = MutableLiveData<List<String>>()
    val resultMessage = MutableLiveData<String>()
    val score = MutableLiveData<Int>(0)

    private val wordDao = DatabaseProvider.getDatabase(application).wordDao()

    init {
        loadWordsIfNeeded()
    }

    private fun loadWordsIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = wordDao.getWordCount()
            if (count == 0) {
                val context = getApplication<Application>().applicationContext
                CsvLoader.loadIfNeeded(context, wordDao)
            }
        }
    }

    fun loadNewCard() {
        viewModelScope.launch(Dispatchers.IO) {
            val allWords = wordDao.getWordsByType("noun")
            if (allWords.isNotEmpty()) {
                val word = allWords.random()
                val wrongAnswers = allWords.filter { it.word != word.word }
                    .shuffled()
                    .take(3)
                    .toMutableList()

                while (wrongAnswers.size < 3) {
                    wrongAnswers.add(allWords.random())
                }

                val answerOptions = (wrongAnswers.map { it.translation } + word.translation).shuffled()

                currentWord.postValue(word)
                options.postValue(answerOptions)
            }
        }
    }

    fun checkAnswer(selectedTranslation: String) {
        if (selectedTranslation == currentWord.value?.translation) {
            resultMessage.postValue("Верно!")
            score.postValue(score.value?.plus(1))
        } else {
            resultMessage.postValue("Неправильно!")
        }
    }
}