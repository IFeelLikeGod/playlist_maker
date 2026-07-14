    package com.example.playlist_maker

    import android.content.Context
    import android.content.Intent
    import android.graphics.Color
    import android.net.Uri
    import android.os.Bundle
    import android.widget.ImageView
    import android.widget.LinearLayout
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.app.AppCompatDelegate
    import androidx.core.content.ContextCompat
    import androidx.core.view.WindowInsetsControllerCompat
    import com.google.android.material.switchmaterial.SwitchMaterial
    import androidx.core.net.toUri

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
            val layoutShare = findViewById<LinearLayout>(R.id.layoutShare)
            val layoutSupport = findViewById<LinearLayout>(R.id.layoutSupport)
            val layoutAgreement = findViewById<LinearLayout>(R.id.layoutAgreement)

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

            layoutShare.setOnClickListener {
                shareCourse()
            }

            layoutSupport.setOnClickListener {
                writeToSupport()
            }
            layoutAgreement.setOnClickListener {
                userAgreement()
            }
        }

        private fun shareCourse() {
            val shareMessage = getString(R.string.share_course_message)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareMessage)
            }

            startActivity(
                Intent.createChooser(
                    shareIntent,
                    getString(R.string.share_course_chooser_title)
                )
            )
        }

        private fun writeToSupport() {
            val email = getString(R.string.support_email)
            val subjectMessage = getString(R.string.support_subject)
            val bodyMessage = getString(R.string.support_body)

            val supportUri =
                "mailto:$email?subject=${Uri.encode(subjectMessage)}&body=${Uri.encode(bodyMessage)}"
                    .toUri()

            val supportIntent = Intent(Intent.ACTION_SENDTO, supportUri)

            try {
                startActivity(supportIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun userAgreement() {
            val agreementUrl = getString(R.string.user_agreement_url)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))

            try {
                startActivity(agreementIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }