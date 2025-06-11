package com.example.linguause_mvp.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.linguause_mvp.R
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.content.Intent
import android.app.AlertDialog
import android.util.Log
import android.content.DialogInterface
import com.example.linguause_mvp.ui.SentenceGameViewModel

class SentenceGameFragment : Fragment(R.layout.fragment_sentence_game) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private lateinit var viewModel: SentenceGameViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DEBUG_FLOW", "onViewCreated стартанул")


        val progressContainer: LinearLayout = view.findViewById(R.id.progress_container)
        Log.d("DEBUG_FLOW", "✔️ progressContainer подключён")

        // Подключаем ViewModel
        viewModel = ViewModelProvider(this)[SentenceGameViewModel::class.java]
        Log.d("DEBUG_FLOW", "viewModel инициализирован")


// Подключаем элементы интерфейса из layout
        val container: LinearLayout = view.findViewById(R.id.word_container)
        Log.d("DEBUG_FLOW", "✔️ container подключён")

        val userInputText: TextView = view.findViewById(R.id.user_input_text)
        Log.d("DEBUG_FLOW", "✔️ userInputText подключён")

        val resultText: TextView = view.findViewById(R.id.result_text)
        Log.d("DEBUG_FLOW", "✔️ resultText подключён")

        val soundButton: ImageView = view.findViewById(R.id.sound_button)
        Log.d("DEBUG_FLOW", "✔️ soundButton подключён")

        val undoButton: Button = view.findViewById(R.id.undo_button)
        Log.d("DEBUG_FLOW", "✔️ undoButton подключён")

        val overlay: View = view.findViewById(R.id.click_overlay)
        Log.d("DEBUG_FLOW", "✔️ overlay подключён")

        val micButton: Button = view.findViewById(R.id.mic_button)
        Log.d("DEBUG_FLOW", "✔️ micButton подключён")


// Слушаем слова для выбора
        viewModel.wordOptions.observe(viewLifecycleOwner) { options ->
            Log.d("DEBUG_FLOW", "viewModel: получили список слов")
            container.removeAllViews()
            options.forEach { word ->
                val btn = Button(requireContext())
                btn.text = word
                btn.setOnClickListener { viewModel.addWord(word) }
                container.addView(btn)
            }
        }

// Слушаем изменения в пользовательском вводе
        viewModel.userInput.observe(viewLifecycleOwner) {
            Log.d("DEBUG_FLOW", "viewModel: изменения в п. вводе")

            userInputText.text = it.joinToString(" ")
        }

// 🔄 Обновляем визуальный прогресс (кружочки)
        viewModel.progressList.observe(viewLifecycleOwner) { progress ->
            progressContainer.removeAllViews()
            Log.d("DEBUG_FLOW", "viewModel: обновили виз. програсс")

            for (item in progress) {
                val dot = View(requireContext())
                dot.layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    setMargins(6, 0, 6, 0)
                }

                dot.setBackgroundResource(
                    when (item) {
                        1 -> R.drawable.dot_green
                        0 -> R.drawable.dot_orange
                        -1 -> R.drawable.dot_red
                        else -> android.R.color.darker_gray
                    }
                )

                progressContainer.addView(dot)
            }
        }

// Когда пользователь завершил выбор — показываем результат
        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            Log.d("DEBUG_FLOW", "viewModel: показываем результат")

            if (completed) {
                val correct = viewModel.isCorrect.value == true
                val resultMessage = if (correct) {
                    "👍 Отлично!"
                } else {
                    "❌ Вы выбрали:\n${viewModel.getUserAnswer()}\n✅ Правильно:\n${viewModel.getCorrectAnswer()}"
                }

                resultText.text = resultMessage
                resultText.visibility = View.VISIBLE
                soundButton.visibility = View.VISIBLE
                overlay.visibility = View.VISIBLE
            }
        }

// Кнопка «Отменить слово»
        undoButton.setOnClickListener {
            viewModel.removeLastWord()
        }

// Клик по фону — переход к следующему предложению
        overlay.setOnClickListener {
            viewModel.startNewSentence()
            Log.d("DEBUG_FLOW", "viewModel: переход у след. предложению")

            resultText.visibility = View.GONE
            soundButton.visibility = View.GONE
            overlay.visibility = View.GONE
        }

// Запускаем первое предложение
        viewModel.startNewSentence()
        Log.d("DEBUG_FLOW", "viewModel: первое предложение")

    }

    // 🔊 Запускает голосовой ввод (SpeechRecognizer)
    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }

        // Проверка: доступен ли SpeechRecognizer на устройстве
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Голосовой ввод не поддерживается", Toast.LENGTH_SHORT).show()
            return
        }

        // Создаём и настраиваем распознаватель
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onError(error: Int) { isListening = false }
            override fun onBeginningOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onRmsChanged(rmsdB: Float) {}

            // 📌 Когда получен результат — показываем диалог с текстом
            override fun onResults(results: Bundle?) {
                isListening = false
                val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.trim()
                    ?.lowercase()

                if (spoken != null) {
                    showConfirmSpeechDialog(spoken)
                }
            }
        })

        // Запускаем прослушивание
        speechRecognizer?.startListening(intent)
    }

    // Показывает подтверждение распознанного текста
    private fun showConfirmSpeechDialog(text: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение")
            .setMessage("Вы сказали: \"$text\"")
            .setPositiveButton("✅ Использовать") { _, _ ->
                // Разбиваем строку на слова и добавляем в ViewModel
                val words = text.split(" ")
                for (word in words) {
                    viewModel.addWord(word)
                }
            }
            .setNegativeButton("❌ Отмена", null)
            .show()
    }

}