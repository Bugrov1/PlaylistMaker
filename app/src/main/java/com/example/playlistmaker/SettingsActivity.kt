package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.switchmaterial.SwitchMaterial

const val PLAYLISTMAKER_SWITCH_CHECK = "playlistmaker_switch_check"
const val CHECKED_KEY = ""

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {

            finish()

        }
        val shareButton = findViewById<FrameLayout>(R.id.share_button)
        shareButton.setOnClickListener {
            val message = getString(R.string.share_button_message)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)

        }
        val supportButton = findViewById<FrameLayout>(R.id.support_button)
        supportButton.setOnClickListener {
            val mailAdress = getString(R.string.support_mail_to)
            val mailTheme = getString(R.string.support_theme_message)
            val mailText = getString(R.string.support_text_message)
            val supportIntent = Intent(Intent.ACTION_SEND)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAdress))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, mailTheme)
            supportIntent.putExtra(Intent.EXTRA_TEXT, mailText)
            startActivity(supportIntent)
        }
        val licenseButton = findViewById<FrameLayout>(R.id.users_license)
        licenseButton.setOnClickListener {
            val link = getString(R.string.users_licence_link)
            val uri = Uri.parse(link)
            val usersIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(usersIntent)

        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_SWITCH_CHECK, MODE_PRIVATE)
        themeSwitcher.isChecked = sharedPrefs.getString(CHECKED_KEY, false.toString()).toBoolean()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)


        }

    }

}