package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

const val THEME_SWITCH_KEY = "key_for_theme_switch"

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        val mainActivity = Intent(this, MainActivity::class.java)

        toolbar.setNavigationOnClickListener {
            startActivity(mainActivity)
        }

        val shareButton = findViewById<MaterialTextView>(R.id.setting_item_share)
        val contactSupport = findViewById<MaterialTextView>(R.id.setting_item_contact_support)
        val userAgreement = findViewById<MaterialTextView>(R.id.setting_item_user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.setting_item_dark_theme)

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        themeSwitcher.isChecked = sharedPrefs.getString(THEME_SWITCH_KEY, "false")!!.toBoolean()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            sharedPrefs.edit()
                .putString(THEME_SWITCH_KEY, checked.toString())
                .apply()

            (applicationContext as App)
                .switchTheme(checked)
        }

        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_url_value)
            )
            startActivity(intent)
        }

        contactSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                getString(R.string.support_email_value)
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.support_message_default_subject_value)
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.support_message_default_text_value)
            )
            startActivity(intent)
        }

        userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(getString(R.string.user_agreement_url_value)))
            startActivity(intent)
        }

    }
}