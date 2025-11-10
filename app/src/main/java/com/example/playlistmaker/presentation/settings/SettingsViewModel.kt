package com.example.playlistmaker.presentation.settings

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.main.App
import com.example.playlistmaker.util.Creator

class SettingsViewModel( private val context: Context): ViewModel() {

    companion object {

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }
    fun switchTheme(isDarkTheme: Boolean) {
            Creator.provideThemeInteractor().setTheme(isDarkTheme)
        }

    fun clickShareApp() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.share_url_value)
            )
            context.startActivity(intent)
        }

    fun clickContactSupport() {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                context.getString(R.string.support_email_value)
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.support_message_default_subject_value)
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.support_message_default_text_value)
            )
            context.startActivity(intent)
        }

    fun clickUserAgreement() {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = context.getString(R.string.user_agreement_url_value).toUri()
            context.startActivity(intent)
        }

}