package com.example.playlistmaker.presentation.settings

import android.app.Activity
import android.content.Intent
import android.widget.Toolbar
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.util.Creator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsController(
    private val activity: Activity,
) {

    private lateinit var toolbar : Toolbar
    private lateinit var shareButton : MaterialTextView
    private lateinit var contactSupport : MaterialTextView
    private lateinit var userAgreement : MaterialTextView
    private lateinit var themeSwitcher : SwitchMaterial

    fun onCreate() {
        toolbar = activity.findViewById(R.id.settings_toolbar)
        shareButton = activity.findViewById(R.id.setting_item_share)
        contactSupport = activity.findViewById(R.id.setting_item_contact_support)
        userAgreement = activity.findViewById(R.id.setting_item_user_agreement)
        themeSwitcher = activity.findViewById(R.id.setting_item_dark_theme)

        toolbar.setNavigationOnClickListener {
            activity.finish()
        }

        themeSwitcher.isChecked = Creator.provideThemeInteractor().getTheme()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            Creator.provideThemeInteractor().setTheme(checked)
        }

        themeSwitcher.setOnClickListener {

        }

        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                activity.getString(R.string.share_url_value)
            )
            activity.startActivity(intent)
        }

        contactSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                activity.getString(R.string.support_email_value)
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                activity.getString(R.string.support_message_default_subject_value)
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                activity.getString(R.string.support_message_default_text_value)
            )
            activity.startActivity(intent)
        }

        userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = activity.getString(R.string.user_agreement_url_value).toUri()
            activity.startActivity(intent)
        }
    }
}