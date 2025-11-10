package com.example.playlistmaker.ui.settings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
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

        contactSupport.setOnClickListener { viewModel.clickContactSupport(this) }
        shareButton.setOnClickListener { viewModel.clickShareApp(this) }
        userAgreement.setOnClickListener { viewModel.clickUserAgreement(this) }
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}