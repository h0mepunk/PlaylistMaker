package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.settings.SettingsView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class SettingsActivity : MvpAppCompatActivity(), SettingsView {

    private lateinit var toolbar : Toolbar
    private lateinit var shareButton : MaterialTextView
    private lateinit var contactSupport : MaterialTextView
    private lateinit var userAgreement : MaterialTextView
    private lateinit var themeSwitcher : SwitchMaterial
    private val presenter by moxyPresenter {
        Creator.provideSettingsPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar = findViewById(R.id.settings_toolbar)
        shareButton = findViewById(R.id.setting_item_share)
        contactSupport = findViewById(R.id.setting_item_contact_support)
        userAgreement = findViewById(R.id.setting_item_user_agreement)
        themeSwitcher = findViewById(R.id.setting_item_dark_theme)

        presenter.onCreate()

        contactSupport.setOnClickListener { presenter.contactSupport() }
        shareButton.setOnClickListener { presenter.share() }
        userAgreement.setOnClickListener { presenter.openUserAgreement() }
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            presenter.switchTheme(isChecked)
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
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

    override fun openShareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            this.getString(R.string.share_url_value)
        )
        startActivity(intent)
    }

    override fun openUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = getString(R.string.user_agreement_url_value).toUri()
        startActivity(intent)
    }

    override fun switchTheme(isDarkMode: Boolean) {
        Creator.provideThemeInteractor().setTheme(isDarkMode)
    }

    override fun setTheme(isDarkMode: Boolean) {
        themeSwitcher.isChecked = isDarkMode
    }
}