package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.settings.SettingsState
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.util.Creator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import kotlin.getValue

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar : Toolbar
    private lateinit var shareButton : MaterialTextView
    private lateinit var contactSupport : MaterialTextView
    private lateinit var userAgreement : MaterialTextView
    private lateinit var themeSwitcher : SwitchMaterial
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar = findViewById(R.id.settings_toolbar)
        shareButton = findViewById(R.id.setting_item_share)
        contactSupport = findViewById(R.id.setting_item_contact_support)
        userAgreement = findViewById(R.id.setting_item_user_agreement)
        themeSwitcher = findViewById(R.id.setting_item_dark_theme)

        themeSwitcher.isChecked = Creator.provideThemeInteractor().getTheme()

        viewModel.observeState().observe(this) {
            render(it)
        }

        contactSupport.setOnClickListener { viewModel.clickContactSupport() }
        shareButton.setOnClickListener { viewModel.clickShareApp() }
        userAgreement.setOnClickListener { viewModel.clickUserAgreement() }
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun clickContactSupport() {
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

    fun clickShareButton() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.share_url_value)
        )
        startActivity(intent)
    }

    fun clickTheme(isDarkTheme: Boolean) {
        Creator.provideThemeInteractor().setTheme(isDarkTheme)
    }

    fun clickUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = getString(R.string.user_agreement_url_value).toUri()
        startActivity(intent)
    }

    fun render(state: SettingsState) {
        when(state) {
            is SettingsState.Share -> {
                clickShareButton()
            }
            is SettingsState.Support ->  {
                clickContactSupport()
            }
            is SettingsState.Theme -> {
                clickTheme(state.isDarkThemeEnabled)
            }
            is SettingsState.UserAgreement ->  {
                clickUserAgreement()
            }
        }
    }
}