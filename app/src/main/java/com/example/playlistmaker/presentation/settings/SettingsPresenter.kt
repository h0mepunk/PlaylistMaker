package com.example.playlistmaker.presentation.settings

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.util.Creator

class SettingsPresenter(
    private val view: SettingsView,
    private val context: Context,
) {

    fun onCreate() {

        view.setTheme(Creator.provideThemeInteractor().getTheme())

        view.switchTheme {
            Creator.provideThemeInteractor().setTheme(it)
        }

        view.clickShareApp {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.share_url_value)
            )
            context.startActivity(intent)
        }

        view.clickContactSupport {
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

        view.clickUserAgreement {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = context.getString(R.string.user_agreement_url_value).toUri()
            context.startActivity(intent)
        }
    }
}