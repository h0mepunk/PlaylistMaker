package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val mainActivity = Intent(this, MainActivity::class.java)

        toolbar.setNavigationOnClickListener {
            startActivity(mainActivity)
        }
    }
}