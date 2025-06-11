package com.example.linguause_mvp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.linguause_mvp.data.GrammarTemplates
import com.example.linguause_mvp.data.UserVocabulary
import com.example.linguause_mvp.logic.SentenceGenerator
import android.util.Log
import android.app.Application
import com.example.linguause_mvp.model.DatabaseProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch





class SentenceGameViewModel(application: Application) : AndroidViewModel(application) {

    // Хранит список прогресса: 1 - верно, -1 - ошибка, 0 - исправил
    private val _progressList = MutableLiveData<MutableList<Int>>(mutableListOf())
    val progressList: LiveData<MutableList<Int>> = _progressList

    // Список слов для выбора
    private val _wordOptions = MutableLiveData<List<String>>()
    val wordOptions: LiveData<List<String>> = _wordOptions

    // Текущий набор слов, выбранный пользователем
    private val _userInput = MutableLiveData<List<String>>(emptyList())
    val userInput: LiveData<List<String>> = _userInput

    // Правильная последовательность
    private lateinit var correctSequence: List<String>

    // Правильное предложение как строка
    private lateinit var correctAnswer: String

    // Состояние завершения фразы
    private val _isCompleted = MutableLiveData<Boolean>(false)
    val isCompleted: LiveData<Boolean> = _isCompleted

    // Проверка: правильный ли ответ
    private val _isCorrect = MutableLiveData<Boolean?>(null)
    val isCorrect: LiveData<Boolean?> = _isCorrect

    fun startNewSentence() {
        // Запускаем корутину (фоновый поток), чтобы работать с базой данных
        viewModelScope.launch {
            // Получаем DAO для работы с базой данных
            val dao = DatabaseProvider.getDatabase(getApplication()).wordDao()

            // Загружаем активные слова и сохраняем их в UserVocabulary
            UserVocabulary.initializeFromDatabase(dao)

            // Теперь запускаем игру (генератор предложений)
            startSentence()
        }
    }

    // Метод, который запускает генерацию предложения (логика перенесена отдельно)
    private fun startSentence() {
        Log.d("DEBUG_FLOW", "🔁 startSentence() вызван")

        // 🧱 Выбираем шаблон грамматики (в будущем — на основе выбора пользователя)
        val template = GrammarTemplates.presentSimpleNegative
        Log.d("DEBUG_FLOW", "📚 Шаблон выбран: ${template.name}")

        // 📦 Получаем слова из UserVocabulary (активный словарь)
        val userWords = UserVocabulary.getAllWordsForGame()
        Log.d("DEBUG_FLOW", "📄 Количество слов в словаре: ${userWords.size}")

        // 🛠 Инициализируем генератор предложений
        val generator = SentenceGenerator(template, userWords)
        Log.d("DEBUG_FLOW", "⚙️ Генератор готов, запускаем генерацию...")

        // 🚀 Генерируем предложение
        val generated = generator.generate()
        Log.d("DEBUG_FLOW", "📤 Предложение сгенерировано: ${generated.correctAnswer}")

        // ✅ Обновляем данные в интерфейсе
        correctSequence = generated.correctSequence
        correctAnswer = generated.correctAnswer
        _wordOptions.value = generated.options
        _userInput.value = emptyList()
        _isCompleted.value = false
        _isCorrect.value = null
        Log.d("DEBUG_FLOW", "📊 Данные обновлены, игра готова")
    }

    fun addWord(word: String) {
        // Если уже завершено — не добавляем
        if (_isCompleted.value == true) return

        val updated = _userInput.value.orEmpty().toMutableList()
        updated.add(word)
        _userInput.value = updated

        // Если длина совпадает с правильной — запускаем проверку
        if (updated.size == correctSequence.size) {
            checkAnswer(updated)
        }
    }

    fun removeLastWord() {
        val current = _userInput.value.orEmpty().toMutableList()
        if (current.isNotEmpty()) {
            current.removeAt(current.lastIndex) // ✅ Удаляем последнее слово
            _userInput.value = current          // Обновляем LiveData
        }
    }

    // Проверяет правильность ответа и обновляет прогресс
    private fun checkAnswer(answer: List<String>) {
        _isCompleted.value = true
        val isCorrectNow = answer == correctSequence
        _isCorrect.value = isCorrectNow

        // Код результата: 1 = верно, 0 = ошибка → исправил, -1 = ошибка
        val code = when {
            isCorrectNow && answer.size == correctSequence.size -> 1      // 🟢
            isCorrectNow -> 0                                             // 🟧
            else -> -1                                                    // 🔴
        }

        _progressList.value?.add(code)
        _progressList.value = _progressList.value // триггер обновления
    }

    fun getCorrectAnswer(): String = correctAnswer
    fun getUserAnswer(): String = _userInput.value?.joinToString(" ") ?: ""
}