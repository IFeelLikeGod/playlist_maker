package com.example.playlist_maker.presentation.settings

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)



        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switchDarkTheme)
        val layoutShare = findViewById<LinearLayout>(R.id.layoutShare)
        val layoutSupport = findViewById<LinearLayout>(R.id.layoutSupport)
        val layoutAgreement = findViewById<LinearLayout>(R.id.layoutAgreement)

        viewModel.observeTheme().observe(this) { settings ->
            val isDark = settings.darkTheme

            switchTheme.isChecked = isDark

            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            window.statusBarColor = if (isDark) {
                ContextCompat.getColor(this, R.color.background_dark)
            } else {
                Color.WHITE
            }
            WindowInsetsControllerCompat(window, window.decorView)
                .isAppearanceLightStatusBars = !isDark
        }

        switchTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeChanged(checked)
        }

        ivBack.setOnClickListener { finish() }
        layoutShare.setOnClickListener { viewModel.onShareClicked() }
        layoutSupport.setOnClickListener { viewModel.onSupportClicked() }
        layoutAgreement.setOnClickListener { viewModel.onTermsClicked() }
    }
}