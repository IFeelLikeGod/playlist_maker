package com.example.playlist_maker

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.statusBarColor = if (isDark) {
            ContextCompat.getColor(this, R.color.background_dark)
        } else {
            Color.WHITE
        }

        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = !isDark

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switchDarkTheme)

        switchTheme.isChecked = isDark

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_theme", isChecked).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        ivBack.setOnClickListener {
            finish()
        }
    }
}