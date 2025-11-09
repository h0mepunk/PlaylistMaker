package com.example.playlistmaker.ui.settings

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.settings.SettingsView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity(), SettingsView {

    private lateinit var toolbar : Toolbar
    private lateinit var shareButton : MaterialTextView
    private lateinit var contactSupport : MaterialTextView
    private lateinit var userAgreement : MaterialTextView
    private lateinit var themeSwitcher : SwitchMaterial
    val settingsPresenter = Creator.provideSettingsPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar = findViewById(R.id.settings_toolbar)
        shareButton = findViewById(R.id.setting_item_share)
        contactSupport = findViewById(R.id.setting_item_contact_support)
        userAgreement = findViewById(R.id.setting_item_user_agreement)
        themeSwitcher = findViewById(R.id.setting_item_dark_theme)

        settingsPresenter.onCreate()

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun clickContactSupport(action: () -> Unit) {
        contactSupport.setOnClickListener { action() }
    }

    override fun clickShareApp(action: () -> Unit) {
        shareButton.setOnClickListener { action() }
    }

    override fun clickUserAgreement(action: () -> Unit) {
        userAgreement.setOnClickListener { action() }
    }

    override fun switchTheme(action: (Boolean) -> Unit) {
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            action(isChecked)
        }
    }

    override fun setTheme(isDarkMode: Boolean) {
        themeSwitcher.isChecked = isDarkMode
    }
}