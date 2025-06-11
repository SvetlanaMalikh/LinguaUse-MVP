package com.example.linguause_mvp

import android.os.Bundle
import android.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.linguause_mvp.model.DatabaseProvider
import com.example.linguause_mvp.data.CsvLoader


import com.example.linguause_mvp.R // ← ЭТО ОБЯЗАТЕЛЬНО!

class MainActivity : AppCompatActivity() {
    private var selectedVoiceGender: String = "female" // по умолчанию

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 📦 Загружаем слова из CSV в базу (только если БД пуста)
        lifecycleScope.launch {
            val dao = DatabaseProvider.getDao(this@MainActivity)
            CsvLoader.loadIfNeeded(this@MainActivity, dao)
        }

        // Восстанавливаем ранее сохранённый выбор или показываем диалог
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        selectedVoiceGender = prefs.getString("tts_voice_gender", null) ?: "unknown"

        if (selectedVoiceGender == "unknown") {
            showVoiceChoiceDialog()
        }

        // Сохраняем отступы системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showVoiceChoiceDialog() {
        AlertDialog.Builder(this)
            .setTitle("Озвучка")
            .setMessage("Выберите голос для озвучивания:")
            .setPositiveButton("👩 Женский") { _, _ ->
                selectedVoiceGender = "female"
                saveVoiceChoice("female")
            }
            .setNegativeButton("👨 Мужской") { _, _ ->
                selectedVoiceGender = "male"
                saveVoiceChoice("male")
            }
            .setCancelable(false)
            .show()
    }

    private fun saveVoiceChoice(choice: String) {
        getSharedPreferences("settings", MODE_PRIVATE)
            .edit()
            .putString("tts_voice_gender", choice)
            .apply()
    }

}