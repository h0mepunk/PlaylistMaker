package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val mainActivity = Intent(this, MainActivity::class.java)

        toolbar.setNavigationOnClickListener {
            startActivity(mainActivity)
        }

        val shareButton = findViewById<MaterialTextView>(R.id.setting_item_share)
        val contactSupport = findViewById<MaterialTextView>(R.id.setting_item_contact_support)
        val userAgreement = findViewById<MaterialTextView>(R.id.setting_item_user_agreement)

        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://practicum.yandex.kz/learn/android-developer-plus/courses/"
            )
            startActivity(intent)
        }

        contactSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                "patrick.delroy.yohoho@gmail.com"
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Спасибо разработчикам и разработчицам за крутое приложение!"
            )
            startActivity(intent)
        }

        userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(intent)
        }

    }
}